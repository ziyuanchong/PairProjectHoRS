/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import Enum.RoomTypeEnum;
import entity.Room;
import entity.RoomType;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Witt
 */
@Local
public interface OperationManagerSessionBeanLocal {

    public RoomType createRoomType(String name, String description, double size, String bed, int capacity, String amenities, boolean available);

    public RoomType viewRoomTypeDetails(String name);

    public void deleteRoomType(String name);

    public RoomType updateRoomType(RoomType updatedRoomType);
    
    public RoomType updateRoomType(String name, String description, double size, String bed, int capacity, String amenities, boolean available);

    public List<RoomType> retrieveListOfRoomTypes();
    
    public Room createNewRoom(String roomNumber, String roomStatus);
    
    public Room updateRoom(Long roomId, String roomNumber, String roomStatus);
    
    public Room updateRoom(Room updatedRoom);
    
    public void deleteRoom(Long roomId);
    
}
