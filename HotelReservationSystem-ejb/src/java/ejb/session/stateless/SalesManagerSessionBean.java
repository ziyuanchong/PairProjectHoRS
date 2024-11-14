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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * <<<<<<< HEAD @a
 *
 *
 * uthor ziyuanchong ======= @author Witt >>>>>>> origin/main
 */
@Stateless
public class SalesManagerSessionBean implements SalesManagerSessionBeanRemote, SalesManagerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
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

    @Override
    public RoomRate findRoomRateByName(String name) throws RoomRateNotFoundException {
        try {
            return em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.name = :name", RoomRate.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
            throw new RoomRateNotFoundException("Room rate with name '" + name + "' does not exist.");
        }
    }

    @Override
    public RoomRate updateRoomRate(String rateName, String newName, RoomRateTypeEnum rateType, BigDecimal ratePerNight,
            Date startDate, Date endDate, String roomTypeName)
            throws RoomRateNotFoundException, RoomTypeNotFoundException {
        RoomRate roomRate = findRoomRateByName(rateName);

        // Find the RoomType by name
        RoomType roomType;
        try {
            roomType = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.name = :roomTypeName", RoomType.class)
                    .setParameter("roomTypeName", roomTypeName)
                    .getSingleResult();
        } catch (NoResultException ex) {
            throw new RoomTypeNotFoundException("Room type with name '" + roomTypeName + "' does not exist.");
        }

        roomRate.setName(newName);
        roomRate.setRateType(rateType);
        roomRate.setRatePerNight(ratePerNight);
        roomRate.setStartDate(startDate);
        roomRate.setEndDate(endDate);
        roomRate.setRoomType(roomType);

        return roomRate;
    }

    @Override
    public boolean deleteRoomRate(String rateName) throws RoomRateNotFoundException {
        RoomRate roomRate = findRoomRateByName(rateName);

        if (isRoomRateInUse(roomRate.getRoomRateId())) {
            roomRate.setRateType(RoomRateTypeEnum.DISABLED); // Disable instead of deleting if in use
            em.merge(roomRate);
            System.out.println("Room rate '" + rateName + "' is in use and has been marked as disabled.");
            return false;
        } else {
            em.remove(roomRate); // Delete if not in use
            System.out.println("Room rate '" + rateName + "' has been successfully deleted.");
            return true;
        }
    }

    private boolean isRoomRateInUse(Long roomRateId) {
        // Find the RoomRate entity associated with the provided ID
        RoomRate roomRate = em.find(RoomRate.class, roomRateId);
        if (roomRate == null) {
            return false;
        }

        // Check if there are any reservations that have the roomRate in their applicableRoomRates
        Long reservationCount = em.createQuery(
                "SELECT COUNT(r) FROM Reservation r JOIN r.applicableRoomRates arr WHERE arr = :roomRate", Long.class)
                .setParameter("roomRate", roomRate)
                .getSingleResult();

        // If reservationCount is greater than 0, it means the RoomRate is in use
        return reservationCount > 0;
    }

    public List<RoomRate> retrieveAllRoomRates() {
        return em.createQuery("SELECT rr FROM RoomRate rr", RoomRate.class).getResultList();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
