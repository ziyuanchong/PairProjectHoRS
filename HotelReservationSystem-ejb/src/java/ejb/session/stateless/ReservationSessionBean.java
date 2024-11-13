/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Reservation;
import entity.ReservationRoom;
import entity.Room;
import entity.RoomType;
import exception.ReservationUnavailableException;
import exception.RoomTypeNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Witt
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public boolean checkForAvailableRooms(String name, Date startDate, Date endDate, int numberOfRooms) throws RoomTypeNotFoundException {
        System.out.println("Checking available rooms for RoomType: " + name + " from " + startDate + " to " + endDate);

        RoomType rt = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.name = :name", RoomType.class)
                .setParameter("name", name)
                .getSingleResult();

        if (rt != null) {
            List<Reservation> overlapReservations = em.createQuery(
                    "SELECT r FROM Reservation r WHERE r.startDate <= :endDate AND r.endDate >= :startDate AND r.roomType = :roomType",
                    Reservation.class)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .setParameter("roomType", rt)
                    .getResultList();

            System.out.println("Found " + overlapReservations.size() + " overlapping reservations for RoomType: " + name);

            int roomsInUse = overlapReservations.stream().mapToInt(Reservation::getNumberOfRooms).sum();
            System.out.println("Total rooms in use for overlapping dates: " + roomsInUse);

            int totalAvailableRooms = (int) rt.getRooms().stream().filter(Room::getIsAvailable).count();
            System.out.println("Total available rooms in inventory for " + name + ": " + totalAvailableRooms);

            if (totalAvailableRooms - roomsInUse >= numberOfRooms) {
                System.out.println("RoomType " + name + " has sufficient availability.");
                return true;
            } else {
                System.out.println("RoomType " + name + " does not have enough rooms available.");
                return false;
            }
        } else {
            throw new RoomTypeNotFoundException("This room type does not exist: " + name);
        }
    }

    public List<RoomType> retrieveListOfAvailableRoomType(Date startDate, Date endDate, int numberOfRooms) throws ReservationUnavailableException, RoomTypeNotFoundException { //checks all the roomtypes for those that can accomodate
        List<RoomType> listOfRoomTypes = em.createQuery("SELECT rt FROM RoomType rt", RoomType.class).getResultList();
        if (listOfRoomTypes != null) { //check if there are at least 1 roomtype
            List<RoomType> listOfAvailableRoomTypes = new ArrayList<>();
            for (RoomType rt : listOfRoomTypes) {
                if (checkForAvailableRooms(rt.getName(), startDate, endDate, numberOfRooms)) {
                    listOfAvailableRoomTypes.add(rt);
                }
            }
            if (listOfAvailableRoomTypes.isEmpty()) { // no roomtypes available
                throw new ReservationUnavailableException("There are no available RoomTypes for this check-in date, check-out date and number of rooms");

            } else {
                return listOfAvailableRoomTypes; //return full list to customer
            }
        } else {
            throw new RoomTypeNotFoundException("There are no room types currently");
        }
    }

    public Reservation createReservation(Long guestId, String name, Date checkInDate, Date checkOutDate, int numberOfRooms, BigDecimal totalAmount) {
        Guest guest = em.find(Guest.class, guestId); // returns Customer if finds customer;
        Reservation reservation = new Reservation(checkInDate, checkOutDate, numberOfRooms, totalAmount);
        RoomType rt = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.name = :name", RoomType.class)
                .setParameter("name", name)
                .getSingleResult();
        reservation.setGuest(guest);
        reservation.setRoomType(rt); //reservation set as Roomtype, assume exists as we will run previous methods before this
        if (checkInDate.equals(new Date())) {
            List<Room> rooms = rt.getRooms();
            while (numberOfRooms-- > 0) {
                for (Room room : rooms) {
                    if (!room.getIsAllocated()) { //allocate any room, assume that reservation can be made through earlier methods
                        room.setIsAllocated(true);
                        ReservationRoom reservationRoom = new ReservationRoom();
                        reservationRoom.setRoom(room);
                        reservationRoom.setReservation(reservation);
                        reservation.getReservationRooms().add(reservationRoom);
                        //add reservation room into reservation
                        em.persist(reservationRoom);
                        em.flush();//save new reservationroom
                    }
                }
            }
        }
        guest.getReservations().add(reservation);
        em.persist(reservation); // Save the reservation
        em.flush();
        return reservation;
    }

}
