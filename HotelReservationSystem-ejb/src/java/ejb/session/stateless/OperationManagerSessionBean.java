/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import Enum.AllocationExceptionTypeEnum;
import Enum.RoomTypeEnum;
import entity.ExceptionAllocationReport;
import entity.ReservationRoom;
import entity.Room;
import entity.RoomType;
import exception.RoomNotFoundException;
import exception.RoomTypeNotFoundException;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Witt
 */
@Stateless
public class OperationManagerSessionBean implements OperationManagerSessionBeanRemote, OperationManagerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public RoomType createRoomType(String name, String description, double size, String bed, int capacity, List<String> amenities, RoomTypeEnum roomTypeEnum) {
        RoomType newRoomType = new RoomType(name, description, size, bed, capacity, amenities, roomTypeEnum);
        em.persist(newRoomType);
        return newRoomType;
    }

    public RoomType viewRoomTypeDetails(String name) throws RoomTypeNotFoundException {
        try {
            RoomType chosenRoomType = em.createQuery(
                    "SELECT r FROM RoomType r WHERE r.name = :name", RoomType.class)
                    .setParameter("name", name)
                    .getSingleResult();
            return chosenRoomType;
        } catch (NoResultException ex) {
            throw new RoomTypeNotFoundException("Room type with name " + name + " does not exist.");
        }
    }

    @Override
    public void deleteRoomType(String name) throws RoomTypeNotFoundException {
        try {
            RoomType deleteRoomType = em.createQuery(
                    "SELECT r FROM RoomType r WHERE r.name = :name", RoomType.class)
                    .setParameter("name", name)
                    .getSingleResult();
            em.remove(deleteRoomType);
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

        if (room.getReservationRooms() == null || room.getReservationRooms().isEmpty()) {
            em.remove(room); // Delete if there are no associated reservations
        } else {
            room.setIsAvailable(false); // Mark as disabled if the room is in use
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
            // Step 1: Find the requested room type by name
            RoomType requestedRoomType = em.createQuery(
                    "SELECT rt FROM RoomType rt WHERE rt.name = :name", RoomType.class)
                    .setParameter("name", roomTypeRequested)
                    .getSingleResult();

            int requestedRank = requestedRoomType.getRoomTypeEnum().ordinal();

            // Step 2: Find the next higher room type by enum ordinal
            Long upgradeRoomTypeId = em.createQuery(
                    "SELECT rt.id FROM RoomType rt WHERE rt.roomTypeEnum.ordinal > :requestedRank ORDER BY rt.roomTypeEnum.ordinal ASC", Long.class)
                    .setParameter("requestedRank", requestedRank)
                    .setMaxResults(1)
                    .getSingleResult();

            // Step 3: Check for an available room with the higher room type
            em.createQuery("SELECT r FROM Room r WHERE r.roomType.id = :roomTypeId AND r.isAvailable = true")
                    .setParameter("roomTypeId", upgradeRoomTypeId)
                    .setMaxResults(1)
                    .getSingleResult();

            return true;
        } catch (NoResultException ex) {
            return false; // No upgrade available
        }
    }
}
