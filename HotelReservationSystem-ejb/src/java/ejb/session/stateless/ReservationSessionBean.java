/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Customer;
import entity.Guest;
import entity.Reservation;
import entity.ReservationRoom;
import entity.Room;
import entity.RoomType;
import exception.ReservationUnavailableException;
import exception.RoomTypeNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    public boolean checkForAvailableRooms(String name, Date startDate, Date endDate, int numberOfRooms) throws RoomTypeNotFoundException { //check if roomtype can accomodate
        List<Reservation> overlapReservations = em.createQuery( //retrieve list of reservations which dates overlap with the current reservation
                "SELECT r FROM Reservation r WHERE r.startDate <=: endDate AND r.endDate >=: startDate", Reservation.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
        int totalUsedRooms = 0;
        if (overlapReservations != null) {
            for (Reservation r : overlapReservations) { //number of rooms required by previous reservations
                totalUsedRooms += r.getNumberOfRooms();
            }
        }
        RoomType rt = em.createQuery("SELECT rt FROM RoomType WHERE rt.name =: roomType", RoomType.class)
                .setParameter("name", name)
                .getSingleResult(); //retrieving RoomType
        int numberOfAvailableRooms = 0;
        if (rt != null) {
            for (Room room : rt.getRooms()) { //counting number of isAvailable rooms under this roomtype
                if (room.getIsAvailable()) {
                    numberOfAvailableRooms++;
                }
            }

            if (numberOfAvailableRooms - totalUsedRooms - numberOfRooms >= 0) { //if there are enough rooms, return true
                return true;
            }
            return false;
        } else {
            throw new RoomTypeNotFoundException("This roomtype does not exist");
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
        // Create a new reservation linked to the walk-in guest
        Guest guest = em.find(Guest.class, guestId); // returns Customer if finds customer;
        Reservation reservation = new Reservation(checkInDate, checkOutDate, numberOfRooms, totalAmount);
        RoomType rt = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.name =: name", RoomType.class)
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
                        reservation.getReservationRooms().add(reservationRoom); //add reservation room into reservation
                        em.persist(reservationRoom); //save new reservationroom
                    }
                }
            }
        }

        if (guest instanceof Customer) {
            guest.getReservations().add(reservation); //add reservation to customer if guest is customer
        }
        em.persist(reservation); // Save the reservation
        return reservation;
    }

}
