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
    public List<ExceptionAllocationReport> allocateRoomsForDate(Date date) {
        System.out.println("Starting allocateRoomsForDate with date: " + date);
        Date checkInDate = stripTime(date);
        List<ExceptionAllocationReport> exceptionReports = new ArrayList<>();

        List<Reservation> reservationsForDate = em.createQuery(
                "SELECT r FROM Reservation r WHERE r.startDate = :checkInDate", Reservation.class)
                .setParameter("checkInDate", checkInDate)
                .getResultList();

        System.out.println("Found " + reservationsForDate.size() + " reservations for date: " + checkInDate);

        reservationsForDate.forEach(reservation -> {
            System.out.println("Processing reservation ID: " + reservation.getReservationId());

            for (int i = 0; i < reservation.getNumberOfRooms(); i++) {
                System.out.println("Allocating room " + (i + 1) + " for reservation ID: " + reservation.getReservationId());

                Room allocatedRoom = allocateRoomToReservation(reservation);
                if (allocatedRoom != null) {
                    System.out.println("Room allocated: " + allocatedRoom.getRoomId());
                    allocatedRoom.setIsAllocated(true);
                    em.merge(allocatedRoom);

                    ReservationRoom reservationRoom = new ReservationRoom(allocatedRoom, reservation);
                    em.persist(reservationRoom);

                    reservation.getReservationRooms().add(reservationRoom);
                    em.merge(reservation);
                } else {
                    System.out.println("No room available, attempting to allocate upgrade for reservation ID: " + reservation.getReservationId());

                    Room upgradedRoom = allocateUpgradeRoom(reservation);
                    if (upgradedRoom != null) {
                        System.out.println("Upgraded room allocated: " + upgradedRoom.getRoomId());
                        upgradedRoom.setIsAllocated(true);
                        em.merge(upgradedRoom);

                        ReservationRoom reservationRoom = new ReservationRoom(upgradedRoom, reservation);
                        em.persist(reservationRoom);

                        reservation.getReservationRooms().add(reservationRoom);
                        em.merge(reservation);

                        ExceptionAllocationReport report = new ExceptionAllocationReport(
                                AllocationExceptionTypeEnum.UPGRADE_AVAILABLE,
                                new Date(),
                                reservation.getRoomType().getName(),
                                reservationRoom
                        );
                        System.out.println("Creating upgrade exception report: " + report);
                        em.persist(report);
                        exceptionReports.add(report);
                    } else {
                        System.out.println("No upgraded room available for reservation ID: " + reservation.getReservationId());

                        ExceptionAllocationReport report = new ExceptionAllocationReport(
                                AllocationExceptionTypeEnum.NO_ROOM_AVAILABLE,
                                new Date(),
                                reservation.getRoomType() != null ? reservation.getRoomType().getName() : "Unknown Room Type",
                                null
                        );
                        System.out.println("Creating no-room-available exception report: " + report);
                        em.persist(report);
                        exceptionReports.add(report);
                    }
                }
            }
        });

        System.out.println("Finished allocateRoomsForDate with exception reports count: " + exceptionReports.size());
        return exceptionReports;
    }

    private Room allocateRoomToReservation(Reservation reservation) {
        try {
            return em.createQuery(
                    "SELECT r FROM Room r WHERE r.roomType = :roomType AND r.isAllocated = false AND r.isAvailable = true", Room.class)
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

            RoomType nextHigherRoomType = em.createQuery(
                    "SELECT rt FROM RoomType rt WHERE rt.name = :name", RoomType.class)
                    .setParameter("name", nextHigherRoomTypeName)
                    .getSingleResult();

            Room availableRoom = em.createQuery(
                    "SELECT r FROM Room r WHERE r.roomType = :roomType AND r.isAllocated = false AND r.isAvailable = true", Room.class)
                    .setParameter("roomType", nextHigherRoomType)
                    .setMaxResults(1)
                    .getSingleResult();

            if (availableRoom != null) {
                return availableRoom;
            }

            // Move to the next higher room type if current one is full
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
