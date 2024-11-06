/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import Enum.RoomRateEnum;
import entity.RoomRate;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Witt
 */
@Local
public interface SalesManagerSessionBeanLocal {

    public RoomRate createRoomRate(Date startDate, Date endDate, RoomRateEnum roomRateEnum, BigDecimal ratePerNight, boolean available);

    public RoomRate viewRoomRateDetails(Long roomRateId);

    public RoomRate updateRoomRate(Long roomRateId, Date startDate, Date endDate, RoomRateEnum roomRateEnum, BigDecimal ratePerNight, boolean available);

    public void deleteRoomRate(Long roomRateId);

    public List<RoomRate> viewAllRoomRates();
}
