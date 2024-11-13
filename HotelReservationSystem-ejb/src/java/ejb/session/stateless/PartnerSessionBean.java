/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Partner;
import entity.Reservation;
import entity.RoomType;
import exception.InvalidPartnerInfoException;
import exception.PartnerNotFoundException;
import exception.ReservationNotFoundException;
import exception.ReservationUnavailableException;
import exception.RoomTypeNotFoundException;
import java.math.BigDecimal;
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

            return (Partner) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new InvalidPartnerInfoException("Invalid username or password.");
        }
    }

    @Override
    public Partner retrievePartnerById(Long partnerId) throws PartnerNotFoundException {
        Partner partner = em.find(Partner.class, partnerId);
        if (partner == null) {
            throw new PartnerNotFoundException("Partner ID " + partnerId + " does not exist.");
        }
        return partner;
    }

    @Override
    public List<RoomType> searchAvailableRoomTypes(Date startDate, Date endDate, int numberOfRooms)
            throws ReservationUnavailableException {
        List<RoomType> roomTypes;
        try {
            roomTypes = reservationSessionBean.retrieveListOfAvailableRoomType(startDate, endDate, numberOfRooms);
            roomTypes.forEach(roomType -> roomType.getRoomRates().size());
            return roomTypes;
        } catch (RoomTypeNotFoundException ex) {
            // Handle the exception or log it as needed
            throw new ReservationUnavailableException("Room types not available for the specified dates.");
        }
    }

    @Override
    public Reservation reserveRoom(Long partnerId, String roomTypeName, Date checkInDate, Date checkOutDate, int numberOfRooms, BigDecimal totalAmount)
            throws RoomTypeNotFoundException, ReservationUnavailableException, PartnerNotFoundException {

        Partner partner;
        try {
            partner = retrievePartnerById(partnerId);
        } catch (PartnerNotFoundException ex) {
            throw new PartnerNotFoundException("Partner ID " + partnerId + " does not exist.");
        }

        if (reservationSessionBean.checkForAvailableRooms(roomTypeName, checkInDate, checkOutDate, numberOfRooms)) {
            Reservation reservation = new Reservation(checkInDate, checkOutDate, numberOfRooms, totalAmount);
            RoomType roomType = operationManagerSessionBean.findRoomTypeByName(roomTypeName);
            reservation.setRoomType(roomType);
            reservation.setBookingByPartner(true);
            em.persist(reservation);

            return reservation;
        } else {
            throw new ReservationUnavailableException("Not enough rooms available for the selected dates and room type.");
        }
    }

    @Override
    public Reservation viewPartnerReservation(Long reservationId) throws ReservationNotFoundException {
        Reservation reservation = em.find(Reservation.class, reservationId);
        if (reservation == null || !reservation.isBookingByPartner()) {
            throw new ReservationNotFoundException("Reservation ID " + reservationId + " not found or not a partner reservation.");
        }

        // Eagerly load required fields
        reservation.getRoomType().getRoomRates().size();  // Access to ensure loading
        reservation.getReservationRooms().size();         // Access to ensure loading

        return reservation;
    }

    @Override
    public List<Reservation> viewAllPartnerReservations(String username) {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.bookingByPartner = true AND r.partner.username = :username");
        query.setParameter("username", username);

        List<Reservation> reservations = query.getResultList();

        // Eagerly load fields for each reservation
        reservations.forEach(reservation -> {
            reservation.getRoomType().getRoomRates().size();
            reservation.getReservationRooms().size();
        });

        return reservations;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
