/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package ejb.session.singleton;

import ejb.session.stateless.RoomAllocationSessionBeanLocal;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Startup;

/**
 *
 * @author ziyuanchong
 */
@Singleton
@LocalBean
@Startup
public class RoomAllocationSchedulerSessionBean {

    @EJB
    private RoomAllocationSessionBeanLocal roomAllocationSessionBean;
    
    @Schedule(hour = "1", minute = "50", second = "0", persistent = false)
    public void dailyResetRoomAvailability() {
        System.out.println("Starting room reset availability at 1:50 a.m.");
        roomAllocationSessionBean.resetRoomAvailability();
        System.out.println("Room reset availability completed.");
    }
    

    // Automatically triggers room allocation daily at 2 a.m.
    @Schedule(hour = "14", minute = "30", second = "0", persistent = false)
    public void dailyRoomAllocation() {
        System.out.println("Starting room allocation process at 2 a.m.");
        roomAllocationSessionBean.allocateRoomsForDate(new Date());
        System.out.println("Room allocation process completed.");
    }

    // Manually triggers room allocation for a specified date
    public void manualRoomAllocation(Date date) {
        System.out.println("Starting manual room allocation for date: " + date);
        roomAllocationSessionBean.allocateRoomsForDate(date);
        System.out.println("Manual room allocation process completed.");
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
