/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import exception.GuestCheckInException;
import exception.GuestCheckOutException;
import exception.ReservationNotFoundException;
import exception.ReservationUnavailableException;
import exception.RoomNotAvailableException;
import exception.RoomTypeNotFoundException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author ziyuanchong
 */
@Remote
public interface GuestRelationOfficerSessionBeanRemote {

    public List<RoomType> searchAvailableRooms(Date checkInDate, Date checkOutDate, int numRooms) throws ReservationUnavailableException, RoomTypeNotFoundException;

    public Reservation walkInReserveRoom(RoomType roomType, int numberOfRooms, Date checkInDate, Date checkOutDate) throws RoomNotAvailableException;

    public void checkInGuest(Long reservationId) throws ReservationNotFoundException, GuestCheckInException;

    public void checkOutGuestByRoomNumber(String roomNumber) throws ReservationNotFoundException, GuestCheckOutException;

    public void checkOutGuest(Long reservationId) throws ReservationNotFoundException, GuestCheckOutException;
}
