/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import Enum.RoomRateTypeEnum;
import entity.RoomRate;
import entity.RoomType;
import exception.RoomRateNotFoundException;
import exception.RoomTypeNotFoundException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author ziyuanchong
 */
@Stateless
public class SalesManagerSessionBean implements SalesManagerSessionBeanRemote, SalesManagerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public RoomRate createRoomRate(String name, RoomRateTypeEnum rateType, BigDecimal ratePerNight, Date startDate, Date endDate, Long roomTypeId)
            throws RoomTypeNotFoundException {
        RoomType roomType = em.find(RoomType.class, roomTypeId);
        if (roomType == null) {
            throw new RoomTypeNotFoundException("Room type with ID " + roomTypeId + " does not exist.");
        }

        RoomRate newRoomRate = new RoomRate(name, rateType, ratePerNight, startDate, endDate, roomType);
        em.persist(newRoomRate);
        return newRoomRate;
    }

    public RoomRate viewRoomRateDetails(Long rateId) throws RoomRateNotFoundException {
        RoomRate roomRate = em.find(RoomRate.class, rateId);
        if (roomRate == null) {
            throw new RoomRateNotFoundException("Room rate with ID " + rateId + " does not exist.");
        }
        return roomRate;
    }

    public RoomRate updateRoomRate(Long rateId, String name, RoomRateTypeEnum rateType, BigDecimal ratePerNight, Date startDate, Date endDate, Long roomTypeId)
            throws RoomRateNotFoundException, RoomTypeNotFoundException {
        RoomRate roomRate = em.find(RoomRate.class, rateId);
        if (roomRate == null) {
            throw new RoomRateNotFoundException("Room rate with ID " + rateId + " does not exist.");
        }

        RoomType roomType = em.find(RoomType.class, roomTypeId);
        if (roomType == null) {
            throw new RoomTypeNotFoundException("Room type with ID " + roomTypeId + " does not exist.");
        }

        roomRate.setName(name);
        roomRate.setRateType(rateType);
        roomRate.setRatePerNight(ratePerNight);
        roomRate.setStartDate(startDate);
        roomRate.setEndDate(endDate);
        roomRate.setRoomType(roomType);

        return roomRate;
    }

    public void deleteRoomRate(Long rateId) throws RoomRateNotFoundException {
        RoomRate roomRate = em.find(RoomRate.class, rateId);
        if (roomRate == null) {
            throw new RoomRateNotFoundException("Room rate with ID " + rateId + " does not exist.");
        }

        if (isRoomRateInUse(rateId)) {
            roomRate.setRateType(RoomRateTypeEnum.DISABLED); // Assuming DISABLED is a type in RoomRateTypeEnum
        } else {
            em.remove(roomRate);
        }
    }

    private boolean isRoomRateInUse(Long rateId) {
        // Checks if any reservations are associated with this room rate
        return em.createQuery("SELECT COUNT(r) FROM Reservation r WHERE r.roomRate.roomRateId = :rateId", Long.class)
                .setParameter("rateId", rateId)
                .getSingleResult() > 0;
    }

    public List<RoomRate> retrieveAllRoomRates() {
        return em.createQuery("SELECT rr FROM RoomRate rr", RoomRate.class).getResultList();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
