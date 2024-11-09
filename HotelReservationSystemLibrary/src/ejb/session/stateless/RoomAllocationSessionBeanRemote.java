/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import java.util.Date;
import javax.ejb.Remote;

/**
 *
 * @author ziyuanchong
 */
@Remote
public interface RoomAllocationSessionBeanRemote {
    public void allocateRoomsForDate(Date date);

}
