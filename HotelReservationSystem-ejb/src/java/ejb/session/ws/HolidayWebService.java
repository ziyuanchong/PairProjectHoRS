/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/EjbWebService.java to edit this template
 */
package ejb.session.ws;

import ejb.session.stateless.PartnerSessionBeanLocal;
import entity.Guest;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author ziyuanchong
 */
@WebService(serviceName = "HolidayWebService", targetNamespace = "http://ws.session.ejb/")
@Stateless()
public class HolidayWebService {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

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

    /*@WebMethod(operationName = "searchAvailableRoomTypes")
    public List<RoomType> searchAvailableRoomTypes(@WebParam(name = "startDate") Date startDate,
            @WebParam(name = "endDate") Date endDate,
            @WebParam(name = "numberOfRooms") int numberOfRooms)
            throws ReservationUnavailableException {
        List<RoomType> roomTypes = partnerSessionBean.searchAvailableRoomTypes(startDate, endDate, numberOfRooms);

        //Access lazy-loaded fields before returning, if needed
          roomTypes.forEach(roomType -> {
            roomType.getRoomRates().size(); // Ensure room rates are loaded
    // Access other fields if necessary
        });
        return roomTypes;
    }
    **/
    @WebMethod(operationName = "searchAvailableRoomTypes")
    public List<RoomType> searchAvailableRoomTypes(@WebParam(name = "startDate") String startDate,
            @WebParam(name = "endDate") String endDate,
            @WebParam(name = "numberOfRooms") int numberOfRooms)
            throws ReservationUnavailableException {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedStartDate = formatter.parse(startDate);
            Date parsedEndDate = formatter.parse(endDate);

            List<RoomType> roomTypes = partnerSessionBean.searchAvailableRoomTypes(parsedStartDate, parsedEndDate, numberOfRooms);

            // Detach entities and remove bidirectional references to avoid cyclic issues
            for (RoomType roomType : roomTypes) {
                em.detach(roomType);

                for (Room room : roomType.getRooms()) {
                    em.detach(room);
                    room.setRoomType(null); // Break cycle with RoomType
                }

                for (RoomRate roomRate : roomType.getRoomRates()) {
                    em.detach(roomRate);
                    roomRate.setRoomType(null); // Break cycle with RoomType
                }

                for (Reservation reservation : roomType.getReservations()) {
                    em.detach(reservation);

                    Guest guest = reservation.getGuest();
                    if (guest != null) {
                        em.detach(guest);
                        guest.getReservations().clear(); // Clear guest's reservation list to avoid cycle
                        reservation.setGuest(null); // Break cycle with Guest
                    }
                }
            }

            return roomTypes;

        } catch (ParseException e) {
            throw new ReservationUnavailableException("Invalid date format. Please use 'yyyy-MM-dd'.");
        }
    }


    /*@WebMethod(operationName = "searchAvailableRoomTypes")
    public String searchAvailableRoomTypes(
            @WebParam(name = "startDate") String startDate,
            @WebParam(name = "endDate") String endDate,
            @WebParam(name = "numberOfRooms") int numberOfRooms) {
        return "Testing successful";  // Temporary return for debugging
    }
**/
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
