/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;


import Enum.RoomRateTypeEnum;
import entity.RoomRate;
import exception.RoomRateNotFoundException;
import exception.RoomTypeNotFoundException;
import java.math.BigDecimal;
import java.util.Date;
import javax.ejb.Remote;

/**
 *
<<<<<<< HEAD
 * @author ziyuanchong
 */
@Remote
public interface SalesManagerSessionBeanRemote {
    public RoomRate createRoomRate(String name, RoomRateTypeEnum rateType, BigDecimal ratePerNight, Date startDate, Date endDate, Long roomTypeId) throws RoomTypeNotFoundException;

    public RoomRate viewRoomRateDetails(Long rateId) throws RoomRateNotFoundException;

    public RoomRate updateRoomRate(Long rateId, String name, RoomRateTypeEnum rateType, BigDecimal ratePerNight, Date startDate, Date endDate, Long roomTypeId) throws RoomRateNotFoundException, RoomTypeNotFoundException;

    public void deleteRoomRate(Long rateId) throws RoomRateNotFoundException;
    

}
