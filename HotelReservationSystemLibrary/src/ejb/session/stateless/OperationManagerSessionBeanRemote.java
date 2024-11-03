/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import Enum.RoomTypeEnum;
import entity.RoomType;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author Witt
 */
@Remote
public interface OperationManagerSessionBeanRemote {

    public RoomType createRoomType(String name, String description, double size, String bed, int capacity, String amenities);

    public List<RoomType> retrieveListOfRoomTypes();

    public void deleteRoomType(String name);
    
}
