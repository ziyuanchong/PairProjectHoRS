/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import Enum.RoomRateEnum;
import entity.RoomRate;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Witt
 */
@Stateless
public class SalesManagerSessionBean implements SalesManagerSessionBeanRemote, SalesManagerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public RoomRate createRoomRate(Date startDate, Date endDate, RoomRateEnum roomRateEnum, BigDecimal ratePerNight, boolean available) {
        RoomRate newRoomRate = new RoomRate(startDate, endDate, roomRateEnum, ratePerNight, available);
        em.persist(newRoomRate);
        return newRoomRate;
    }
    
    public RoomRate viewRoomRateDetails(Long roomRateId) {
        RoomRate retrievedRoomRate = em.find(RoomRate.class, roomRateId);
        return retrievedRoomRate;
    }   
    
    public RoomRate updateRoomRate(Long roomRateId, Date startDate, Date endDate, RoomRateEnum roomRateEnum, BigDecimal ratePerNight, boolean available){
        RoomRate currentRoomRate = viewRoomRateDetails(roomRateId);
        currentRoomRate.setStartDate(startDate);
        currentRoomRate.setEndDate(endDate);
        currentRoomRate.setRoomRateEnum(roomRateEnum);
        currentRoomRate.setRatePerNight(ratePerNight);
        currentRoomRate.setAvailable(available);
        em.persist(currentRoomRate);
        return currentRoomRate;
    }
    
    public void deleteRoomRate(Long roomRateId) {
        RoomRate roomRateToBeDeleted = viewRoomRateDetails(roomRateId);
        if (roomRateToBeDeleted.getRoomType() == null) {
            em.remove(roomRateToBeDeleted);
        } else {
           roomRateToBeDeleted.setAvailable(false);
           em.persist(roomRateToBeDeleted);
        }
    }
    
    public List<RoomRate> viewAllRoomRates() {
        return em.createQuery("SELECT r FROM RoomRate r").getResultList();
    }
}
