/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import Enum.RoomTypeEnum;
import entity.ExceptionAllocationReport;

import entity.Room;
import entity.RoomType;
import exception.RoomNotFoundException;
import exception.RoomTypeIsDisabledException;
import exception.RoomTypeNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author Witt
 */
@Remote
public interface OperationManagerSessionBeanRemote {

    public RoomType createRoomType(String name, String description, double size, String bed, int capacity, List<String> amenities, RoomTypeEnum roomTypeEnum, boolean available, String nextHigherRoomType);

    public Room findRoomByNumber(String roomNumber) throws RoomNotFoundException;

    public boolean deleteRoomType(String name) throws RoomTypeNotFoundException;

    public RoomType viewRoomTypeDetails(String name) throws RoomTypeNotFoundException;
    public List<RoomType> retrieveListOfRoomTypes();

    public RoomType updateRoomType(RoomType updatedRoomType) throws RoomTypeNotFoundException;

    public Room createRoom(String roomNumber, boolean isAvailable, Long roomTypeId)  throws RoomTypeNotFoundException, RoomTypeIsDisabledException;

    public Room updateRoom(Long roomId, String roomNumber, boolean isAvailable, Long roomTypeId) throws RoomNotFoundException, RoomTypeNotFoundException;

    public boolean deleteRoom(Long roomId) throws RoomNotFoundException;

    public List<Room> retrieveAllRooms();

    public ExceptionAllocationReport generateRoomAllocationExceptionReport(String roomTypeRequested, Long reservationRoomId) throws RoomNotFoundException;
    public List<ExceptionAllocationReport> retrieveRoomAllocationExceptionReports();
    public RoomType findRoomTypeByName(String name) throws RoomTypeNotFoundException;
    public BigDecimal getPublishedRate(Long roomTypeId);

}
