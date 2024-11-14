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
import java.util.ArrayList;
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
    public void resetRoomAvailability() {
        Date today = stripTime(new Date());

        // Find all reservations ending today
        List<Reservation> endingReservations = em.createQuery(
                "SELECT r FROM Reservation r WHERE r.endDate = :today", Reservation.class)
                .setParameter("today", today)
                .getResultList();

        // Mark each room in the ending reservations as available
        endingReservations.forEach(reservation -> {
            reservation.getReservationRooms().forEach(reservationRoom -> {
                Room room = reservationRoom.getRoom();
                room.setIsAllocated(false); // Make room available
                em.merge(room);
            });
        });
    }

    /**
     *
     */
    @Override
    public List<ExceptionAllocationReport> allocateRoomsForDate(Date date) {
        Date checkInDate = stripTime(date);
        List<ExceptionAllocationReport> exceptionReports = new ArrayList<>();

        List<Reservation> reservationsForDate = em.createQuery(
                "SELECT r FROM Reservation r WHERE r.startDate = :checkInDate", Reservation.class)
                .setParameter("checkInDate", checkInDate)
                .getResultList();

        reservationsForDate.forEach(reservation -> {
            Room allocatedRoom = allocateRoomToReservation(reservation);

            if (allocatedRoom != null) {
                allocatedRoom.setIsAllocated(true);
                em.merge(allocatedRoom);

                ReservationRoom reservationRoom = new ReservationRoom(allocatedRoom, reservation);
                em.persist(reservationRoom);

                reservation.getReservationRooms().add(reservationRoom);
                em.merge(reservation);
            } else {
                Room upgradedRoom = allocateUpgradeRoom(reservation);

                if (upgradedRoom != null) {
                    upgradedRoom.setIsAllocated(true);
                    em.merge(upgradedRoom);

                    ReservationRoom reservationRoom = new ReservationRoom(upgradedRoom, reservation);
                    em.persist(reservationRoom);

                    reservation.getReservationRooms().add(reservationRoom);
                    em.merge(reservation);

                    // Only add a report if the ReservationRoom was successfully allocated
                    ExceptionAllocationReport report = new ExceptionAllocationReport(
                            AllocationExceptionTypeEnum.UPGRADE_AVAILABLE,
                            new Date(),
                            reservation.getRoomType().getName(),
                            reservationRoom
                    );
                    em.persist(report);
                    exceptionReports.add(report);
                } else {
                    // Create report indicating no rooms available without setting a reservationRoom
                    ExceptionAllocationReport report = new ExceptionAllocationReport(
                            AllocationExceptionTypeEnum.NO_ROOM_AVAILABLE,
                            new Date(),
                            reservation.getRoomType().getName(),
                            null
                    );
                    em.persist(report);
                    exceptionReports.add(report);
                }
            }
        });

        return exceptionReports;
    }

    private Room allocateRoomToReservation(Reservation reservation) {
        try {
            return em.createQuery(
                    "SELECT r FROM Room r WHERE r.roomType = :roomType AND r.isAllocated = false", Room.class)
                    .setParameter("roomType", reservation.getRoomType())
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    private Room allocateUpgradeRoom(Reservation reservation) {
        try {
            RoomType requestedRoomType = reservation.getRoomType();
            String nextHigherRoomTypeName = requestedRoomType.getNextHigherRoomType();

            while (nextHigherRoomTypeName != null) {
                RoomType nextHigherRoomType = em.createQuery(
                        "SELECT rt FROM RoomType rt WHERE rt.name = :name", RoomType.class)
                        .setParameter("name", nextHigherRoomTypeName)
                        .getSingleResult();

                Room availableRoom = em.createQuery(
                        "SELECT r FROM Room r WHERE r.roomType = :roomType AND r.isAllocated = false", Room.class)
                        .setParameter("roomType", nextHigherRoomType)
                        .setMaxResults(1)
                        .getSingleResult();

                if (availableRoom != null) {
                    return availableRoom;
                }

                // Move to the next higher room type if current one is full
                nextHigherRoomTypeName = nextHigherRoomType.getNextHigherRoomType();
            }
            return null;

        } catch (NoResultException ex) {
            return null;
        }
    }

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
