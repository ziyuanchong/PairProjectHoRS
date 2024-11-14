/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import Enum.AllocationExceptionTypeEnum;
import entity.ExceptionAllocationReport;
import entity.Guest;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import exception.InvalidPartnerInfoException;
import exception.PartnerNotFoundException;
import exception.ReservationNotFoundException;
import exception.ReservationUnavailableException;
import exception.RoomRateNotFoundException;
import exception.RoomTypeNotFoundException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ziyuanchong
 */
@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {

    @EJB
    private RoomAllocationSessionBeanLocal roomAllocationSessionBean;

    @EJB
    private PaymentSessionBeanLocal paymentSessionBean;

    @EJB
    private OperationManagerSessionBeanLocal operationManagerSessionBean;

    @EJB
    private ReservationSessionBeanLocal reservationSessionBean;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public Partner loginPartner(String username, String password) throws InvalidPartnerInfoException {
        try {
            Query query = em.createQuery("SELECT p FROM Partner p WHERE p.username = :username AND p.password = :password");
            query.setParameter("username", username);
            query.setParameter("password", password);

            Partner partner = (Partner) query.getSingleResult();

            // Check if the partner is already logged in
            // Set partner as logged in
            // Update the entity in the database
            return partner;
        } catch (NoResultException ex) {
            throw new InvalidPartnerInfoException("Invalid username or password.");
        }
    }

    @Override
    public List<RoomType> searchAvailableRoomTypes(Date startDate, Date endDate, int numberOfRooms)
            throws ReservationUnavailableException {
        try {
            return reservationSessionBean.retrieveListOfAvailableRoomType(startDate, endDate, numberOfRooms);
        } catch (RoomTypeNotFoundException e) {
            // Wrap and rethrow as ReservationUnavailableException, or log as needed
            throw new ReservationUnavailableException("No available room types found for the specified dates and room count.");
        }
    }

    // Reserve room for a partner
    @Override
    public Reservation reserveRoom(Long partnerId, String roomTypeName, Date checkInDate, Date checkOutDate, int numberOfRooms)
            throws RoomTypeNotFoundException, ReservationUnavailableException, PartnerNotFoundException {

        Partner partner = retrievePartnerById(partnerId); // Throws PartnerNotFoundException if not found

        if (!reservationSessionBean.checkForAvailableRooms(roomTypeName, checkInDate, checkOutDate, numberOfRooms)) {
            throw new ReservationUnavailableException("Not enough rooms available for the selected dates and room type.");
        }

        try {
            // Calculate total amount using PaymentSessionBean
            BigDecimal totalAmount = paymentSessionBean.calculatePaymentForReservationClient(roomTypeName, checkInDate, checkOutDate, numberOfRooms);

            // Create reservation with ReservationSessionBean
            Reservation reservation = reservationSessionBean.createReservation(partner.getPartnerId(), roomTypeName, checkInDate, checkOutDate, numberOfRooms, totalAmount);
            reservation.setBookingByPartner(true);
            reservation.setPartner(partner);

            em.persist(reservation);
            em.flush();

            // Check if the reservation is for same-day check-in after 2 a.m.
            if (isSameDay(checkInDate, new Date()) && isAfter2AM(new Date())) {
                List<ExceptionAllocationReport> allocationReports = roomAllocationSessionBean.allocateRoomsForDate(checkInDate);

                // Optionally, handle or log allocation reports as needed
                allocationReports.forEach(report -> {
                    if (report.getExceptionType() == AllocationExceptionTypeEnum.NO_ROOM_AVAILABLE) {
                        System.out.println("Allocation issue: No room available for upgrade");
                    }
                });
            }

            return reservation;

        } catch (RoomRateNotFoundException | RoomTypeNotFoundException ex) {
            throw new ReservationUnavailableException("Room rate not found for the specified dates and room type.");
        }
    }

    // Helper method to check if two dates are the same day
    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    // Helper method to check if the current time is after 2 a.m.
    private boolean isAfter2AM(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour >= 2;
    }

    // View a specific partner reservation
    @Override
    public Reservation viewPartnerReservation(Long reservationId) throws ReservationNotFoundException {
        Reservation reservation = em.find(Reservation.class, reservationId);
        if (reservation == null || !reservation.isBookingByPartner()) {
            throw new ReservationNotFoundException("Reservation ID " + reservationId + " not found or not a partner reservation.");
        }

        // Trigger lazy loading of RoomType by accessing one of its fields
        if (reservation.getRoomType() != null) {
            reservation.getRoomType().getName(); // Accessing a property forces loading
        }

        em.detach(reservation);

        // Proceed with breaking cycles as previously done
        reservation.setPartner(null);
        RoomType roomType = reservation.getRoomType();
        if (roomType != null) {
            em.detach(roomType);
            roomType.getRooms().forEach(room -> {
                em.detach(room);
                room.setRoomType(null);  // Break cycle with RoomType in Room
            });
            roomType.getRoomRates().forEach(roomRate -> {
                em.detach(roomRate);
                roomRate.setRoomType(null);  // Break cycle with RoomType in RoomRate
            });
            roomType.setReservations(null);
        }

        Guest guest = reservation.getGuest();
        if (guest != null) {
            em.detach(guest);
            guest.getReservations().clear(); // Clear guest's reservations to prevent cycles
            reservation.setGuest(null); // Break cycle with Guest
        }

        reservation.getReservationRooms().forEach(reservationRoom -> {
            em.detach(reservationRoom);
            Room room = reservationRoom.getRoom();
            if (room != null) {
                em.detach(room);
                room.setRoomType(null);  // Break cycle if Room has a reference to RoomType
            }
            reservationRoom.setReservation(null);  // Break cycle back to Reservation
        });

        return reservation;
    }

    // View all reservations made by a specific partner
    @Override
    public List<Reservation> viewAllPartnerReservations(String username) {
        return em.createQuery("SELECT r FROM Reservation r WHERE r.bookingByPartner = true AND r.partner.username = :username", Reservation.class)
                .setParameter("username", username)
                .getResultList();
    }

    @Override
    public Partner retrievePartnerById(Long partnerId) throws PartnerNotFoundException {
        Partner partner = em.find(Partner.class, partnerId);
        if (partner == null) {
            throw new PartnerNotFoundException("Partner ID " + partnerId + " does not exist.");
        }
        return partner;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
