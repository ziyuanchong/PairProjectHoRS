/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.ExceptionAllocationReport;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ziyuanchong
 */
@Local
public interface RoomAllocationSessionBeanLocal {

    public List<ExceptionAllocationReport> allocateRoomsForDate(Date date);

    public void resetRoomAvailability();

    
}
