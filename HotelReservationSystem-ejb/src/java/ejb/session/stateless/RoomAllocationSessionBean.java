/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import Enum.AllocationExceptionTypeEnum;
import entity.ExceptionAllocationReport;
import entity.Reservation;
import entity.ReservationRoom;
import entity.Room;
import entity.RoomType;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author ziyuanchong
 */
@Stateless
public class RoomAllocationSessionBean implements RoomAllocationSessionBeanRemote, RoomAllocationSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public void allocateRoomsForDate(Date date) {
        Date checkInDate = stripTime(date);  // Strip time for accurate comparison

        // Retrieve all reservations for check-in on the specified date
        List<Reservation> reservationsForDate = em.createQuery(
                "SELECT r FROM Reservation r WHERE r.startDate = :checkInDate", Reservation.class)
                .setParameter("checkInDate", checkInDate)
                .getResultList();

        reservationsForDate.forEach(reservation -> {
            Room allocatedRoom = allocateRoomToReservation(reservation);

            if (allocatedRoom != null) {
                // Room successfully allocated; mark it as unavailable
                allocatedRoom.setIsAllocated(true);
                em.merge(allocatedRoom);

                // Link Room to Reservation
                ReservationRoom reservationRoom = new ReservationRoom(allocatedRoom, reservation);
                em.persist(reservationRoom);

                reservation.getReservationRooms().add(reservationRoom);
                em.merge(reservation);
            } else {
                // Try to upgrade if no room of the requested type is available
                Room upgradedRoom = allocateUpgradeRoom(reservation);

                if (upgradedRoom != null) {
                    // Upgrade successful; mark the upgraded room as unavailable
                    upgradedRoom.setIsAllocated(true);
                    em.merge(upgradedRoom);

                    ReservationRoom reservationRoom = new ReservationRoom(upgradedRoom, reservation);
                    em.persist(reservationRoom);

                    reservation.getReservationRooms().add(reservationRoom);
                    em.merge(reservation);
                } else {
                    // Neither requested nor upgrade room is available; generate ExceptionAllocationReport
                    ExceptionAllocationReport report = new ExceptionAllocationReport(
                            AllocationExceptionTypeEnum.NO_ROOM_AVAILABLE,
                            new Date(),
                            reservation.getRoomType().getName(),
                            null
                    );
                    em.persist(report);
                }
            }
        });
    }

    // Helper method to allocate a room of the requested type
    private Room allocateRoomToReservation(Reservation reservation) {
        try {
            return em.createQuery(
                    "SELECT r FROM Room r WHERE r.roomType = :roomType AND r.isAllocated = false", Room.class)
                    .setParameter("roomType", reservation.getRoomType())
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null; // No available room of the requested type
        }
    }

    // Helper method to allocate an upgraded room
    private Room allocateUpgradeRoom(Reservation reservation) {
        try {
            RoomType requestedRoomType = reservation.getRoomType();
            int requestedRank = requestedRoomType.getRoomTypeEnum().ordinal();

            // Find the next available room in a higher rank room type
            return em.createQuery(
                    "SELECT r FROM Room r WHERE r.roomType.roomTypeEnum.ordinal > :requestedRank AND r.isAllocated = false ORDER BY r.roomType.roomTypeEnum.ordinal ASC", Room.class)
                    .setParameter("requestedRank", requestedRank)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null; // No upgrade available
        }
    }

    // Helper method to strip the time component from a date
    private Date stripTime(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
