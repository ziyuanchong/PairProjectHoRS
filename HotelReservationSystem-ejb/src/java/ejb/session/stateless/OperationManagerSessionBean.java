/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import Enum.AllocationExceptionTypeEnum;
import Enum.RoomRateTypeEnum;
import Enum.RoomTypeEnum;
import entity.ExceptionAllocationReport;
import entity.ReservationRoom;

import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import exception.RoomNotFoundException;
import exception.RoomTypeNotFoundException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author Witt
 */
@Stateless
public class OperationManagerSessionBean implements OperationManagerSessionBeanRemote, OperationManagerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public RoomType createRoomType(String name, String description, double size, String bed, int capacity, List<String> amenities, RoomTypeEnum roomTypeEnum, boolean available, String nextHigherRoomType) {
        RoomType newRoomType = new RoomType(name, description, size, bed, capacity, amenities, roomTypeEnum, available);
        newRoomType.setNextHigherRoomType(nextHigherRoomType);

        em.persist(newRoomType);
        return newRoomType;
    }

    @Override
    public RoomType viewRoomTypeDetails(String name) throws RoomTypeNotFoundException {
        try {
            RoomType chosenRoomType = em.createQuery(
                    "SELECT r FROM RoomType r WHERE r.name = :name", RoomType.class)
                    .setParameter("name", name)
                    .getSingleResult();
            chosenRoomType.getAmenities().size(); // This will force the lazy field to be loaded

            // If there are other lazy-loaded collections (like reservations or rooms), initialize them as well:
            chosenRoomType.getReservations().size();
            chosenRoomType.getRooms().size();
            return chosenRoomType;
        } catch (NoResultException ex) {
            throw new RoomTypeNotFoundException("Room type with name " + name + " does not exist.");
        }
    }

    @Override
    public boolean deleteRoomType(String name) throws RoomTypeNotFoundException {
        try {
            RoomType roomType = em.createQuery(
                    "SELECT r FROM RoomType r WHERE r.name = :name", RoomType.class)
                    .setParameter("name", name)
                    .getSingleResult();

            // Check if there are any rooms or reservations associated with this room type
            boolean hasAssociatedRooms = !roomType.getRooms().isEmpty();
            boolean hasAssociatedReservations = !roomType.getReservations().isEmpty();

            if (hasAssociatedRooms || hasAssociatedReservations) {
                // If in use, mark the room type as disabled instead of deleting
                roomType.setAvailable(false);
                em.merge(roomType);
                System.out.println("Room type '" + name + "' is in use and has been marked as disabled.");
                return false;
            } else {
                // If not in use, proceed with deletion
                em.remove(roomType);
                System.out.println("Room type '" + name + "' has been successfully deleted.");
                return true;
            }
        } catch (NoResultException ex) {
            throw new RoomTypeNotFoundException("Room type with name " + name + " does not exist.");
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Error occurred while attempting to delete room type.", ex);
        }
    }

    @Override
    public RoomType updateRoomType(RoomType updatedRoomType) throws RoomTypeNotFoundException {
        RoomType existingRoomType = em.find(RoomType.class, updatedRoomType.getRoomTypeId());
        if (existingRoomType == null) {
            throw new RoomTypeNotFoundException("Room type with ID " + updatedRoomType.getRoomTypeId() + " does not exist.");
        }

        // Update fields
        existingRoomType.setName(updatedRoomType.getName());
        existingRoomType.setDescription(updatedRoomType.getDescription());
        existingRoomType.setSize(updatedRoomType.getSize());
        existingRoomType.setBed(updatedRoomType.getBed());
        existingRoomType.setCapacity(updatedRoomType.getCapacity());
        existingRoomType.setAmenities(updatedRoomType.getAmenities());
        existingRoomType.setNextHigherRoomType(updatedRoomType.getNextHigherRoomType());

        return existingRoomType;
    }

    @Override
    public List<RoomType> retrieveListOfRoomTypes() {
        List<RoomType> list = em.createQuery("SELECT r FROM RoomType r", RoomType.class).getResultList();
        return list;
    }

    @Override
    public Room createRoom(String roomNumber, boolean isAvailable, Long roomTypeId) throws RoomTypeNotFoundException {
        RoomType roomType = em.find(RoomType.class, roomTypeId);
        if (roomType == null) {
            throw new IllegalArgumentException("RoomType not found with ID: " + roomTypeId);
        }
        Room newRoom = new Room(roomNumber, isAvailable, roomType);
        em.persist(newRoom);
        return newRoom;
    }

    @Override
    public Room updateRoom(Long roomId, String roomNumber, boolean isAvailable, Long roomTypeId)
            throws RoomNotFoundException, RoomTypeNotFoundException {
        Room room = em.find(Room.class, roomId);
        if (room == null) {
            throw new RoomNotFoundException("Room not found with ID: " + roomId);
        }

        room.setRoomNumber(roomNumber);
        room.setIsAvailable(isAvailable);

        // Update room type if needed
        if (roomTypeId != null) {
            RoomType roomType = em.find(RoomType.class, roomTypeId);
            if (roomType == null) {
                throw new RoomTypeNotFoundException("Room type not found with ID: " + roomTypeId);
            }
            room.setRoomType(roomType);
        }

        return room;
    }

    @Override
    public void deleteRoom(Long roomId) throws RoomNotFoundException {
        Room room = em.find(Room.class, roomId);
        if (room == null) {
            throw new RoomNotFoundException("Room not found with ID: " + roomId);
        }

        // Check if the room is currently allocated
        if (room.getIsAllocated()) {
            room.setIsAvailable(false); // Mark as unavailable if currently allocated
            em.merge(room);
            System.out.println("Room is currently allocated and has been marked as unavailable.");
        } else {
            em.remove(room); // Delete if it is not currently allocated
            System.out.println("Room deleted successfully.");
        }
    }

    @Override
    public List<Room> retrieveAllRooms() {
        try {
            return em.createQuery("SELECT r FROM Room r", Room.class).getResultList();
        } catch (Exception ex) {
            throw new IllegalStateException("An error occurred while retrieving room records.", ex);
        }
    }

    @Override
    public ExceptionAllocationReport generateRoomAllocationExceptionReport(String roomTypeRequested, Long reservationRoomId)
            throws RoomNotFoundException {
        ReservationRoom reservationRoom = em.find(ReservationRoom.class, reservationRoomId);
        if (reservationRoom == null) {
            throw new RoomNotFoundException("ReservationRoom not found with ID: " + reservationRoomId);
        }

        AllocationExceptionTypeEnum exceptionType;
        boolean roomAvailable = checkRoomAvailability(roomTypeRequested); // Method to check availability
        boolean upgradeAvailable = checkUpgradeAvailability(roomTypeRequested); // Method to check upgrade

        if (!roomAvailable && upgradeAvailable) {
            exceptionType = AllocationExceptionTypeEnum.UPGRADE_AVAILABLE;
        } else if (!roomAvailable) {
            exceptionType = AllocationExceptionTypeEnum.NO_ROOM_AVAILABLE;
        } else {
            // No exception if a room is available for the requested type
            return null;
        }

        // Create and persist the exception report
        ExceptionAllocationReport report = new ExceptionAllocationReport(
                exceptionType,
                new Date(),
                roomTypeRequested,
                reservationRoom
        );
        em.persist(report);
        return report;
    }

    private boolean checkRoomAvailability(String roomTypeRequested) {
        try {
            // Query to find an available room of the requested type
            em.createQuery("SELECT r FROM Room r WHERE r.roomType.name = :roomType AND r.isAvailable = true")
                    .setParameter("roomType", roomTypeRequested)
                    .getSingleResult();
            return true;
        } catch (NoResultException ex) {
            return false; // No available room of the requested type
        }
    }

    private boolean checkUpgradeAvailability(String roomTypeRequested) {
        try {
            RoomType requestedRoomType = em.createQuery(
                    "SELECT rt FROM RoomType rt WHERE rt.name = :name", RoomType.class)
                    .setParameter("name", roomTypeRequested)
                    .getSingleResult();

            String nextHigherRoomTypeName = requestedRoomType.getNextHigherRoomType();

            while (nextHigherRoomTypeName != null) {
                RoomType nextHigherRoomType = em.createQuery(
                        "SELECT rt FROM RoomType rt WHERE rt.name = :name", RoomType.class)
                        .setParameter("name", nextHigherRoomTypeName)
                        .getSingleResult();

                Room availableRoom = em.createQuery(
                        "SELECT r FROM Room r WHERE r.roomType = :roomType AND r.isAvailable = true", Room.class)
                        .setParameter("roomType", nextHigherRoomType)
                        .setMaxResults(1)
                        .getSingleResult();

                if (availableRoom != null) {
                    return true;
                }

                nextHigherRoomTypeName = nextHigherRoomType.getNextHigherRoomType();
            }
            return false;

        } catch (NoResultException ex) {
            return false;
        }
    }

    @Override
    public Room findRoomByNumber(String roomNumber) throws RoomNotFoundException {
        try {
            return em.createQuery("SELECT r FROM Room r WHERE r.roomNumber = :roomNumber", Room.class)
                    .setParameter("roomNumber", roomNumber)
                    .getSingleResult();
        } catch (NoResultException ex) {
            throw new RoomNotFoundException("Room with room number " + roomNumber + " does not exist.");
        }
    }

    @Override
    public RoomType findRoomTypeByName(String name) throws RoomTypeNotFoundException {
        try {
            return em.createQuery("SELECT rt FROM RoomType rt WHERE rt.name = :name", RoomType.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
            throw new RoomTypeNotFoundException("RoomType with name: " + name + " does not exist.");
        }
    }

    @Override
    public List<ExceptionAllocationReport> retrieveRoomAllocationExceptionReports() {
        TypedQuery<ExceptionAllocationReport> query = em.createQuery("SELECT e FROM ExceptionAllocationReport e", ExceptionAllocationReport.class);
        return query.getResultList();
    }

    @Override
    public BigDecimal getPublishedRate(Long roomTypeId) {
        RoomType roomType = em.createQuery(
                "SELECT rt FROM RoomType rt LEFT JOIN FETCH rt.roomRates WHERE rt.roomTypeId = :id", RoomType.class)
                .setParameter("id", roomTypeId)
                .getSingleResult();

        return roomType.getRoomRates().stream()
                .filter(rate -> rate.getRateType() == RoomRateTypeEnum.PUBLISHED)
                .findFirst()
                .map(RoomRate::getRatePerNight)
                .orElse(BigDecimal.ZERO);
    }
}
