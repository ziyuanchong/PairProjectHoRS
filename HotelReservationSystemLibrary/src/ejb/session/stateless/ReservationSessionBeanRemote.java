/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import HelperClass.RoomTypeAvailability;
import entity.Guest;
import entity.Reservation;
import entity.RoomType;
import exception.ReservationUnavailableException;
import exception.RoomTypeNotFoundException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author Witt
 */
@Remote
public interface ReservationSessionBeanRemote {

    public boolean checkForAvailableRooms(String name, Date startDate, Date endDate, int numberOfRooms) throws RoomTypeNotFoundException;

    public List<RoomType> retrieveListOfAvailableRoomType(Date startDate, Date endDate, int numberOfRooms) throws ReservationUnavailableException, RoomTypeNotFoundException;

    public Reservation createReservation(Long guestId, String name, Date checkInDate, Date checkOutDate, int numberOfRooms, BigDecimal totalAmount);

    public Reservation createNewReservation(Long guestId, String roomTypeName, Date checkInDate, Date checkOutDate, int numberOfRooms, BigDecimal totalAmount) throws RoomTypeNotFoundException;

    public List<RoomTypeAvailability> retrieveRoomTypeAvailability(Date startDate, Date endDate) throws RoomTypeNotFoundException;
    
}
