/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import exception.ReservationUnavailableException;
import exception.RoomTypeNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Witt
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @EJB
    private PaymentSessionBeanLocal paymentSessionBean;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public boolean checkForAvailableRooms(String name, Date startDate, Date endDate, int numberOfRooms) throws RoomTypeNotFoundException {

        RoomType rt = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.name = :name", RoomType.class)
                .setParameter("name", name)
                .getSingleResult();

        if (rt != null) {
            List<Reservation> overlapReservations = em.createQuery(
                    "SELECT r FROM Reservation r WHERE r.startDate < :endDate AND r.endDate > :startDate AND r.roomType = :roomType",
                    Reservation.class)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .setParameter("roomType", rt)
                    .getResultList();

            int roomsInUse = overlapReservations.stream().mapToInt(Reservation::getNumberOfRooms).sum();

            int totalAvailableRooms = (int) rt.getRooms().stream().filter(Room::getIsAvailable).count();

            if (totalAvailableRooms - roomsInUse >= numberOfRooms) {
                return true;
            } else {
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
                if (rt.isAvailable() && checkForAvailableRooms(rt.getName(), startDate, endDate, numberOfRooms)) {
                    listOfAvailableRoomTypes.add(rt);
                    rt.getAmenities().size();
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
        Guest guest = em.find(Guest.class, guestId);
        Reservation reservation = new Reservation(checkInDate, checkOutDate, numberOfRooms, totalAmount);
        RoomType rt = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.name = :name", RoomType.class)
                .setParameter("name", name)
                .getSingleResult();
        reservation.setGuest(guest);
        reservation.setRoomType(rt);

        // Populate applicableRoomRates based on date overlap with RoomRate periods
        List<RoomRate> applicableRates = paymentSessionBean.findApplicableRoomRatesForPeriod(rt, checkInDate, checkOutDate);
        reservation.setApplicableRoomRates(applicableRates);

        // Your allocation logic here...
        em.persist(reservation);
        em.flush();
        return reservation;
    }

// Method to find RoomRates based on the date range
    
}
