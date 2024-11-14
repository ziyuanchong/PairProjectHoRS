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
        Partner partner = partnerSessionBean.loginPartner(username, password);

        // Detach the partner entity and clear cyclic references in associated entities
        em.detach(partner);
        partner.getReservations().forEach(reservation -> {
            em.detach(reservation);
            reservation.setPartner(null);  // Break cycle with Partner

            // Handle RoomType in Reservation and break any cycles
            RoomType roomType = reservation.getRoomType();
            if (roomType != null) {
                em.detach(roomType);
                roomType.setReservations(null);  // Clear back-reference to Reservation
                roomType.setNextHigherRoomType(null);
                roomType.setRooms(null);// Clear self-referencing field

                // Detach and clear RoomRates in RoomType to prevent cycles
                roomType.getRoomRates().forEach(roomRate -> {
                    em.detach(roomRate);
                    roomRate.setRoomType(null);  // Break cycle with RoomType in RoomRate
                });

                reservation.setRoomType(null);  // Ensure RoomType is detached from Reservation to avoid a cycle
            }

            // Handle RoomRates in Reservation and clear any RoomType references
            reservation.getApplicableRoomRates().forEach(roomRate -> {
                em.detach(roomRate);
                roomRate.setRoomType(null);  // Break cycle with RoomType in RoomRate
            });

            // Detach ReservationRooms in Reservation and associated Room entities
            reservation.getReservationRooms().forEach(reservationRoom -> {
                em.detach(reservationRoom);
                reservationRoom.setReservation(null);  // Break cycle with Reservation

                Room room = reservationRoom.getRoom();
                if (room != null) {
                    em.detach(room);
                    room.setRoomType(null);  // Break cycle with RoomType in Room
                }
            });

            // Handle Guest in Reservation to prevent cycles
            Guest guest = reservation.getGuest();
            if (guest != null) {
                em.detach(guest);
                guest.getReservations().clear();  // Clear guest's reservations to prevent cycles
                reservation.setGuest(null);  // Break cycle with Guest
            }
        });

        return partner;
    }

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

            // Detach and remove bidirectional references
            for (RoomType roomType : roomTypes) {
                em.detach(roomType);

                // Break cycle with Room entities
                roomType.setRooms(null); // Detach entire room list instead of individual entities
                roomType.setReservations(null); // Detach entire reservation list
                for (RoomRate roomRate : roomType.getRoomRates()) {
                    em.detach(roomRate);
                    roomRate.setRoomType(null); // Break cycle with RoomType in RoomRate
                }
            }

            return roomTypes;

        } catch (ParseException e) {
            throw new ReservationUnavailableException("Invalid date format. Please use 'yyyy-MM-dd'.");
        }
    }

    @WebMethod(operationName = "reserveRoom")
    public Reservation reserveRoom(
            @WebParam(name = "partnerId") Long partnerId,
            @WebParam(name = "roomTypeName") String roomTypeName,
            @WebParam(name = "checkInDate") String checkInDate,
            @WebParam(name = "checkOutDate") String checkOutDate,
            @WebParam(name = "numberOfRooms") int numberOfRooms)
            throws RoomTypeNotFoundException, ReservationUnavailableException, PartnerNotFoundException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date parsedCheckInDate = formatter.parse(checkInDate);
            Date parsedCheckOutDate = formatter.parse(checkOutDate);

            // Fetch reservation via session bean
            Reservation reservation = partnerSessionBean.reserveRoom(
                    partnerId, roomTypeName, parsedCheckInDate, parsedCheckOutDate, numberOfRooms);

            // Detach reservation and associated entities to avoid cycles
            if (reservation != null) {
                em.detach(reservation);
                reservation.setPartner(null);  // Break cycle with Partner

                // Detach and clean up RoomType entity
                RoomType roomType = reservation.getRoomType();
                if (roomType != null) {
                    em.detach(roomType);

                    if (roomType.getRooms() != null) {
                        roomType.getRooms().forEach(room -> {
                            em.detach(room);
                            room.setRoomType(null);  // Break cycle with RoomType in Room
                        });
                    }

                    if (roomType.getRoomRates() != null) {
                        roomType.getRoomRates().forEach(roomRate -> {
                            em.detach(roomRate);
                            roomRate.setRoomType(null); // Break cycle with RoomType in RoomRate
                        });
                    }

                    roomType.setReservations(null);
                }

                // Break cycles for other entities if needed (e.g., Guest, ReservationRoom)
                Guest guest = reservation.getGuest();
                if (guest != null) {
                    em.detach(guest);
                    guest.getReservations().clear(); // Clear guest's reservations to prevent cycles
                    reservation.setGuest(null); // Break cycle with Guest
                }
            }

            return reservation;

        } catch (ParseException e) {
            throw new ReservationUnavailableException("Invalid date format. Please use 'yyyy-MM-dd'.");
        }
    }

    /**
     * Helper method to detach Reservation and associated entities, breaking
     * cycles.
     */
    @WebMethod(operationName = "viewPartnerReservation")
    public Reservation viewPartnerReservation(@WebParam(name = "reservationId") Long reservationId)
            throws ReservationNotFoundException {
        // Retrieve the reservation via session bean
        Reservation reservation = partnerSessionBean.viewPartnerReservation(reservationId);
        em.detach(reservation);

        // Break the cycle with Partner entity
        reservation.setPartner(null);

        // Handle RoomType entity by creating a simplified version to avoid cycles
        RoomType roomType = reservation.getRoomType();
        if (roomType != null) {
            em.detach(roomType);

            // Create a lightweight RoomType with only essential fields to prevent cycles
            RoomType simplifiedRoomType = new RoomType();
            simplifiedRoomType.setRoomTypeId(roomType.getRoomTypeId());
            simplifiedRoomType.setName(roomType.getName());
            reservation.setRoomType(simplifiedRoomType); // Attach lightweight version to reservation
        }

        // Handle Guest entity and break cycles
        Guest guest = reservation.getGuest();
        if (guest != null) {
            em.detach(guest);
            guest.getReservations().clear();  // Clear guest's reservations to prevent cycles
            reservation.setGuest(null);  // Break cycle with Guest
        }

        // Detach ReservationRooms in Reservation and associated Room entities
        reservation.getReservationRooms().forEach(reservationRoom -> {
            em.detach(reservationRoom);
            reservationRoom.setReservation(null);  // Break cycle with Reservation

            Room room = reservationRoom.getRoom();
            if (room != null) {
                em.detach(room);
                room.setRoomType(null);  // Break cycle with RoomType in Room
            }
        });

        // Handle RoomRates in Reservation and clear any RoomType references
        reservation.getApplicableRoomRates().forEach(roomRate -> {
            em.detach(roomRate);
            roomRate.setRoomType(null);  // Break cycle with RoomType in RoomRate
        });

        return reservation;
    }

    @WebMethod(operationName = "viewAllPartnerReservations")
    public List<Reservation> viewAllPartnerReservations(@WebParam(name = "username") String username) {
        List<Reservation> reservations = partnerSessionBean.viewAllPartnerReservations(username);

        for (Reservation reservation : reservations) {
            em.detach(reservation);
            reservation.setPartner(null);  // Break cycle with Partner

            // Handle RoomType in Reservation
            RoomType roomType = reservation.getRoomType();
            if (roomType != null) {
                em.detach(roomType);

                // Create a lightweight RoomType to avoid cycles
                RoomType simplifiedRoomType = new RoomType();
                simplifiedRoomType.setRoomTypeId(roomType.getRoomTypeId());
                simplifiedRoomType.setName(roomType.getName());
                reservation.setRoomType(simplifiedRoomType); // Attach lightweight version to reservation
            }

            // Handle Guest entity to prevent cycles
            Guest guest = reservation.getGuest();
            if (guest != null) {
                em.detach(guest);
                guest.getReservations().clear();  // Clear guest's reservations to prevent cycles
                reservation.setGuest(null);  // Break cycle with Guest
            }

            // Detach ReservationRooms in Reservation and associated Room entities
            reservation.getReservationRooms().forEach(reservationRoom -> {
                em.detach(reservationRoom);
                reservationRoom.setReservation(null);  // Break cycle with Reservation

                Room room = reservationRoom.getRoom();
                if (room != null) {
                    em.detach(room);
                    room.setRoomType(null);  // Break cycle with RoomType in Room
                }
            });

            // Handle RoomRates in Reservation and clear any RoomType references
            reservation.getApplicableRoomRates().forEach(roomRate -> {
                em.detach(roomRate);
                roomRate.setRoomType(null);  // Break cycle with RoomType in RoomRate
            });
        }

        return reservations;
    }
}
