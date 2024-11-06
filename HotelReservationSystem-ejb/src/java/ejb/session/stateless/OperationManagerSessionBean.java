/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
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
    public RoomType createRoomType(String name, String description, double size, String bed, int capacity, String amenities, boolean available) {
        RoomType newRoomType = new RoomType(name, description, size,  bed, capacity, amenities, available);
        em.persist(newRoomType);
        return newRoomType;
    }

    public RoomType viewRoomTypeDetails(String name) {
        RoomType chosenRoomType = em.createQuery(
                "SELECT r FROM RoomType r WHERE r.name =: name", RoomType.class)
                .setParameter("name", name)
                .getSingleResult();
        return chosenRoomType;
    }

    public void deleteRoomType(String name) { 
        RoomType deleteRoomType = viewRoomTypeDetails(name);
        if (deleteRoomType.getRooms() == null && deleteRoomType.getReservations() == null) {
            em.remove(deleteRoomType);
        } else {
            deleteRoomType.setAvailable(false);
            updateRoomType(deleteRoomType);
        }
    }

    @Override
    public RoomType updateRoomType(RoomType updatedRoomType) {
        RoomType existingRoomType = viewRoomTypeDetails(updatedRoomType.getName());
        existingRoomType.setName(updatedRoomType.getName());
        existingRoomType.setDescription(updatedRoomType.getDescription());
        existingRoomType.setSize(updatedRoomType.getSize());
        existingRoomType.setBed(updatedRoomType.getBed());
        existingRoomType.setCapacity(updatedRoomType.getCapacity());
        existingRoomType.setAmenities(updatedRoomType.getAmenities());
        em.persist(existingRoomType);
        return existingRoomType;
    }
    
    @Override
    public RoomType updateRoomType(String name, String description, double size, String bed, int capacity, String amenities, boolean available) {
        RoomType existingRoomType = viewRoomTypeDetails(name);
        existingRoomType.setName(name); 
        existingRoomType.setDescription(description);
        existingRoomType.setSize(size);
        existingRoomType.setBed(bed);
        existingRoomType.setCapacity(capacity);
        existingRoomType.setAmenities(amenities);
        existingRoomType.setAvailable(available);
        em.persist(existingRoomType);
        return existingRoomType;
    }
            

    public List<RoomType> retrieveListOfRoomTypes() {
        List<RoomType> list = em.createQuery("SELECT r FROM RoomType r", RoomType.class).getResultList();
        return list;
    }
    
    public Room createNewRoom(String roomNumber, String roomStatus) {
        Room newRoom = new Room(roomNumber, roomStatus);
        em.persist(newRoom);
        return newRoom;
    }
    
    public Room updateRoom(Long roomId, String roomNumber, String roomStatus) {
        Room currentRoom = em.find(Room.class, roomId);
        currentRoom.setRoomNumber(roomNumber);
        currentRoom.setRoomStatus(roomStatus);
        em.persist(currentRoom);
        return currentRoom;
    }
    
    public Room updateRoom(Room updatedRoom) {
        Room currentRoom = em.find(Room.class, updatedRoom.getRoomId());
        currentRoom.setRoomNumber(updatedRoom.getRoomNumber());
        currentRoom.setRoomStatus(updatedRoom.getRoomStatus());
        em.persist(currentRoom);
        return currentRoom;
    }
    
    public void deleteRoom(Long roomId) { //Checks if Room has any ReservationRoom relationship, if have rls change status, if not remove completely)
        Room roomToBeDeleted = em.find(Room.class, roomId);
        if (roomToBeDeleted.getReservationRooms() == null) {
            em.remove(roomToBeDeleted);
        } else {
            updateRoom(roomId, roomToBeDeleted.getRoomNumber(), "NotAvailable");
        }
    }
    
    public List<Room> viewAllRooms() {
        return em.createQuery("SELECT r FROM Room r").getResultList();
    }
}
