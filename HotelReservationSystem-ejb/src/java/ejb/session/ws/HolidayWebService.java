/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/EjbWebService.java to edit this template
 */
package ejb.session.ws;

import ejb.session.stateless.PartnerSessionBeanLocal;
import entity.Partner;
import entity.Reservation;
import entity.RoomType;
import exception.InvalidPartnerInfoException;
import exception.PartnerNotFoundException;
import exception.ReservationNotFoundException;
import exception.ReservationUnavailableException;
import exception.RoomTypeNotFoundException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import java.text.ParseException;

/**
 *
 * @author ziyuanchong
 */
@WebService(serviceName = "HolidayWebService", targetNamespace = "http://ws.session.ejb/")
@Stateless()
public class HolidayWebService {

    /**
     * This is a sample web service operation
     */
    @EJB
    private PartnerSessionBeanLocal partnerSessionBean;

    @WebMethod(operationName = "loginPartner")
    public Partner loginPartner(@WebParam(name = "username") String username, @WebParam(name = "password") String password)
            throws InvalidPartnerInfoException {
        return partnerSessionBean.loginPartner(username, password);
    }

    //@WebMethod(operationName = "searchAvailableRoomTypes")
    //public List<RoomType> searchAvailableRoomTypes(@WebParam(name = "startDate") Date startDate,
    //        @WebParam(name = "endDate") Date endDate,
    //        @WebParam(name = "numberOfRooms") int numberOfRooms)
    //        throws ReservationUnavailableException {
    //    List<RoomType> roomTypes = partnerSessionBean.searchAvailableRoomTypes(startDate, endDate, numberOfRooms);
//
//        // Access lazy-loaded fields before returning, if needed
    //      roomTypes.forEach(roomType -> {
    //        roomType.getRoomRates().size(); // Ensure room rates are loaded
    // Access other fields if necessary
    //    });
    //    return roomTypes;
    //}
    @WebMethod(operationName = "searchAvailableRoomTypes")
    public List<RoomType> searchAvailableRoomTypes(@WebParam(name = "startDate") String startDate,
            @WebParam(name = "endDate") String endDate,
            @WebParam(name = "numberOfRooms") int numberOfRooms)
            throws ReservationUnavailableException {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedStartDate = formatter.parse(startDate);
            Date parsedEndDate = formatter.parse(endDate);

            // Now call the original method using parsed dates
            return partnerSessionBean.searchAvailableRoomTypes(parsedStartDate, parsedEndDate, numberOfRooms);

        } catch (ParseException e) {
            // Handle the parsing error, perhaps by logging it and/or throwing a specific exception
            throw new ReservationUnavailableException("Invalid date format. Please use 'yyyy-MM-dd'.");
        }
    }

    @WebMethod(operationName = "reserveRoom")
    public Reservation reserveRoom(@WebParam(name = "partnerId") Long partnerId,
            @WebParam(name = "roomTypeName") String roomTypeName,
            @WebParam(name = "checkInDate") Date checkInDate,
            @WebParam(name = "checkOutDate") Date checkOutDate,
            @WebParam(name = "numberOfRooms") int numberOfRooms,
            @WebParam(name = "totalAmount") BigDecimal totalAmount)
            throws RoomTypeNotFoundException, ReservationUnavailableException, PartnerNotFoundException {
        return partnerSessionBean.reserveRoom(partnerId, roomTypeName, checkInDate, checkOutDate, numberOfRooms, totalAmount);
    }

    @WebMethod(operationName = "viewPartnerReservation")
    public Reservation viewPartnerReservation(@WebParam(name = "reservationId") Long reservationId)
            throws ReservationNotFoundException {
        Reservation reservation = partnerSessionBean.viewPartnerReservation(reservationId);
        reservation.getRoomType().getRoomRates().size();  // Ensure lazy fields are loaded
        reservation.getReservationRooms().size();
        return reservation;
    }

    @WebMethod(operationName = "viewAllPartnerReservations")
    public List<Reservation> viewAllPartnerReservations(@WebParam(name = "username") String username) {
        List<Reservation> reservations = partnerSessionBean.viewAllPartnerReservations(username);
        reservations.forEach(reservation -> {
            reservation.getRoomType().getRoomRates().size(); // Load lazy fields for each reservation
            reservation.getReservationRooms().size();
        });
        return reservations;
    }
}
