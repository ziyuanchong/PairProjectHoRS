/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Reservation;
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
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ziyuanchong
 */
@Local
public interface GuestRelationOfficerSessionBeanLocal {

    public List<RoomType> searchAvailableRooms(Date checkInDate, Date checkOutDate, int numRooms) throws ReservationUnavailableException, RoomTypeNotFoundException;

    public void checkInGuest(Long reservationId) throws ReservationNotFoundException, GuestCheckInException;

    public void checkOutGuest(Long reservationId) throws ReservationNotFoundException, GuestCheckOutException;
    
    public boolean checkIfGuestExists(String email);

    public void checkOutGuestByRoomNumber(String roomNumber) throws ReservationNotFoundException, GuestCheckOutException;

    public Guest createNewGuest(String firstName, String lastName, String phoneNumber, String email);

    public Reservation walkInReserveRoom(Long guestId, String name, Date checkInDate, Date checkOutDate, int numberOfRooms, BigDecimal totalAmount) throws RoomNotAvailableException;

     public Guest retrieveGuestByEmail(String email);
}
