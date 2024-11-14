/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Partner;
import entity.Reservation;
import entity.RoomType;
import exception.InvalidPartnerInfoException;
import exception.PartnerNotFoundException;
import exception.ReservationNotFoundException;
import exception.ReservationUnavailableException;
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
public interface PartnerSessionBeanRemote {
    public Partner loginPartner(String username, String password) throws InvalidPartnerInfoException;

    public Partner retrievePartnerById(Long partnerId) throws PartnerNotFoundException;

    public List<RoomType> searchAvailableRoomTypes(Date startDate, Date endDate, int numberOfRooms) throws ReservationUnavailableException;

    public Reservation viewPartnerReservation(Long reservationId) throws ReservationNotFoundException;

    public List<Reservation> viewAllPartnerReservations(String username);

    public Reservation reserveRoom(Long partnerId, String roomTypeName, Date checkInDate, Date checkOutDate, int numberOfRooms) throws RoomTypeNotFoundException, ReservationUnavailableException, PartnerNotFoundException;
    
}
