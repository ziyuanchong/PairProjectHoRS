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
import exception.GuestCheckInException;
import exception.GuestCheckOutException;
import exception.ReservationNotFoundException;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author ziyuanchong
 */
@Stateless
public class GuestRelationOfficerSessionBean implements GuestRelationOfficerSessionBeanRemote, GuestRelationOfficerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    //use case 23: walk-in search room
    public List<Room> searchAvailableRooms(Date checkInDate, Date checkOutDate, int numRooms) {
        List<Room> availableRooms = em.createQuery(
                "SELECT r FROM Room r WHERE r.isAvailable = true AND r.roomType.id NOT IN "
                + "(SELECT rr.roomType.id FROM Reservation rr WHERE rr.startDate <= :checkOutDate AND rr.endDate >= :checkInDate)", Room.class)
                .setParameter("checkInDate", checkInDate)
                .setParameter("checkOutDate", checkOutDate)
                .getResultList();

        // Filter rooms by the number needed and calculate rate based on RoomType rate
        // Assuming a rate calculation method `calculateTotalRate`
        // Filter availableRooms list for sufficient inventory here
        return availableRooms;
    }

    //use case 24: walk in reserve room
    public Reservation reserveRoomsForWalkIn(Guest guest, List<Room> selectedRooms, Date checkInDate, Date checkOutDate) {
        // Create a new reservation linked to the walk-in guest
        Reservation reservation = new Reservation(checkInDate, checkOutDate);
        reservation.setGuest(guest);

        selectedRooms.forEach(room -> {
            // Allocate the room immediately if it's a same-day check-in
            if (checkInDate.equals(new Date())) {
                room.setIsAvailable(false);
                em.merge(room);
            }

            ReservationRoom reservationRoom = new ReservationRoom(room, reservation);
            em.persist(reservationRoom);
            reservation.getReservationRooms().add(reservationRoom);
        });

        em.persist(reservation); // Save the reservation
        return reservation;
    }

    //usecase 25: checkin guest
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
            room.setIsAvailable(false);
            em.merge(room);
        }

        guest.setCheckIn(true);  // Mark guest as checked in
        em.merge(guest);
        em.merge(reservation);  // Update reservation status

        System.out.println("Guest " + guest.getFirstName() + " " + guest.getLastName()
                + " has been checked into allocated rooms.");
    }

    //use case 26 checkout guest
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
            if (room.getIsAvailable()) {
                throw new GuestCheckOutException("Room " + room.getRoomNumber() + " is already available, check-out cannot proceed.");
            }
            room.setIsAvailable(true);
            em.merge(room);
        }

        guest.setCheckIn(false); // Mark guest as checked out
        em.merge(guest);

        reservation.setEndDate(new Date());  // Mark reservation as completed
        em.merge(reservation);

        System.out.println("Guest " + guest.getFirstName() + " " + guest.getLastName() + " has successfully checked out.");
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
