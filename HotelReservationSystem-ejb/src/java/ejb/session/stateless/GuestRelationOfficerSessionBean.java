/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import Enum.AllocationExceptionTypeEnum;
import entity.ExceptionAllocationReport;
import entity.Guest;
import entity.Reservation;
import entity.ReservationRoom;
import entity.Room;
import entity.RoomType;
import exception.GeneralException;
import exception.GuestCheckInException;
import exception.GuestCheckOutException;
import exception.GuestExistException;
import exception.ReservationNotFoundException;
import exception.ReservationUnavailableException;
import exception.RoomNotAvailableException;
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
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 * @author ziyuanchong
 */
@Stateless
public class GuestRelationOfficerSessionBean implements GuestRelationOfficerSessionBeanRemote, GuestRelationOfficerSessionBeanLocal {

    @EJB
    private ReservationSessionBeanLocal reservationSessionBean;

    @EJB
    private RoomAllocationSessionBeanLocal roomAllocationSessionBean;

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    //use case 23: walk-in search room
    @Override
    public List<RoomType> searchAvailableRooms(Date checkInDate, Date checkOutDate, int numberOfRooms) throws ReservationUnavailableException, RoomTypeNotFoundException {
        try {
            return reservationSessionBeanLocal.retrieveListOfAvailableRoomType(checkOutDate, checkInDate, numberOfRooms);
        } catch (ReservationUnavailableException e) {
            throw new ReservationUnavailableException("There are not enough rooms for the dates provided");
        } catch (RoomTypeNotFoundException ex) {
            throw new RoomTypeNotFoundException("There are no available rooms");
        }
    }

    public Guest createNewGuest(String firstName, String lastName, String phoneNumber, String email) {
        Guest guest = new Guest(firstName, lastName, phoneNumber, email);
        em.persist(guest);
        em.flush();
        return guest;
    }

    public boolean checkIfGuestExists(String email) {
        try {
            Guest guest = em.createQuery("SELECT g FROM Guest g WHERE g.email = :email", Guest.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return false;
        }
        return true;
    }
//use case 24: walk in reserve room

    @Override
    public Reservation walkInReserveRoom(Long guestId, String name, Date checkInDate, Date checkOutDate, int numberOfRooms, BigDecimal totalAmount) throws RoomNotAvailableException {
        Reservation reservation = reservationSessionBean.createReservation(guestId, name, checkInDate, checkOutDate, numberOfRooms, totalAmount);

        // Check if it's a same-day check-in after 2 a.m.
        if (isSameDay(checkInDate, new Date()) && isAfter2AM(new Date())) {
            // Immediately allocate rooms for today's reservations
            roomAllocationSessionBean.allocateRoomsForDate(new Date());
        }

        return reservation;
    }

// Helper methods for date comparison
    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean isAfter2AM(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour >= 2;
    }

    //usecase 25: checkin guest
    @Override
    public void checkInGuest(Long reservationId) throws ReservationNotFoundException, GuestCheckInException {
        Reservation reservation = em.find(Reservation.class, reservationId);
        if (reservation == null) {
            throw new ReservationNotFoundException("Reservation not found with ID: " + reservationId);
        }

        Guest guest = reservation.getGuest();
        if (guest == null) {
            throw new GuestCheckInException("Guest information is missing for reservation ID: " + reservationId);
        }

        // Check if the guest is already checked in
        if (guest.isCheckIn()) {
            throw new GuestCheckInException("Guest is already checked in.");
        }

        List<ReservationRoom> allocatedRooms = reservation.getReservationRooms();

        if (allocatedRooms.isEmpty()) {
            // No rooms allocated; handle exception for manual intervention
            ExceptionAllocationReport exceptionReport = new ExceptionAllocationReport(
                    AllocationExceptionTypeEnum.NO_ROOM_AVAILABLE,
                    new Date(),
                    reservation.getRoomType().getName(),
                    null
            );
            em.persist(exceptionReport);
            throw new GuestCheckInException("No rooms allocated for this reservation. Manual handling required.");
        }

        // Mark rooms as occupied and update guest's check-in status
        for (ReservationRoom reservationRoom : allocatedRooms) {
            Room room = reservationRoom.getRoom();
            room.setIsAllocated(true);
            em.merge(room);
        }

        guest.setCheckIn(true);  // Mark guest as checked in
        em.merge(guest);
        em.merge(reservation);  // Update reservation status

        System.out.println("Guest " + guest.getFirstName() + " " + guest.getLastName()
                + " has been checked into allocated rooms.");
    }

    //use case 26 checkout guest
    @Override
    public void checkOutGuest(Long reservationId) throws ReservationNotFoundException, GuestCheckOutException {
        Reservation reservation = em.find(Reservation.class, reservationId);
        if (reservation == null) {
            throw new ReservationNotFoundException("Reservation not found with ID: " + reservationId);
        }

        Guest guest = reservation.getGuest();
        if (guest == null) {
            throw new GuestCheckOutException("Guest information is missing for reservation ID: " + reservationId);
        }

        List<ReservationRoom> allocatedRooms = reservation.getReservationRooms();

        for (ReservationRoom reservationRoom : allocatedRooms) {
            Room room = reservationRoom.getRoom();
            if (room.getIsAllocated()) {
                throw new GuestCheckOutException("Room " + room.getRoomNumber() + " is already available, check-out cannot proceed.");
            }
            room.setIsAllocated(false);
            em.merge(room);
        }

        guest.setCheckIn(false); // Mark guest as checked out
        em.merge(guest);

        reservation.setEndDate(new Date());  // Mark reservation as completed
        em.merge(reservation);

        System.out.println("Guest " + guest.getFirstName() + " " + guest.getLastName() + " has successfully checked out.");
    }

    @Override
    public void checkOutGuestByRoomNumber(String roomNumber) throws ReservationNotFoundException, GuestCheckOutException {
        // Find the room by room number
        Room room = em.createQuery("SELECT r FROM Room r WHERE r.roomNumber = :roomNumber", Room.class)
                .setParameter("roomNumber", roomNumber)
                .getSingleResult();
        if (room == null || !room.getIsAllocated()) {
            throw new GuestCheckOutException("Room " + roomNumber + " is either not found or not currently allocated.");
        }

        // Find the active reservation for the room
        ReservationRoom reservationRoom = em.createQuery(
                "SELECT rr FROM ReservationRoom rr WHERE rr.room = :room AND rr.reservation.endDate >= :today", ReservationRoom.class)
                .setParameter("room", room)
                .setParameter("today", new Date())
                .setMaxResults(1)
                .getSingleResult();
        if (reservationRoom == null) {
            throw new ReservationNotFoundException("No active reservation found for room " + roomNumber + ".");
        }

        Reservation reservation = reservationRoom.getReservation();
        Guest guest = reservation.getGuest();

        // Retrieve all allocated rooms for this reservation and mark them as available
        for (ReservationRoom rr : reservation.getReservationRooms()) {
            Room allocatedRoom = rr.getRoom();
            allocatedRoom.setIsAllocated(false); // Mark the room as available
            em.merge(allocatedRoom);
        }

        // Update guest's check-in status and reservation end date
        guest.setCheckIn(false);
        em.merge(guest);

        reservation.setEndDate(new Date());  // Mark reservation as completed
        em.merge(reservation);

        System.out.println("Guest " + guest.getFirstName() + " " + guest.getLastName()
                + " has successfully checked out from room " + roomNumber + ".");
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public Guest retrieveGuestByEmail(String email) {
        return em.createQuery("SELECT g FROM Guest g WHERE g.email = :email", Guest.class)
                .setParameter("email", email)
                .getSingleResult();
    }
}
