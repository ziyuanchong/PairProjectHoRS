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
import java.util.ArrayList;
import java.util.Calendar;
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
public class PaymentSessionBean implements PaymentSessionBeanRemote, PaymentSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public BigDecimal calculatePaymentForReservationClient(String name, Date startDate, Date endDate, int numberOfRooms) throws RoomTypeNotFoundException, RoomRateNotFoundException { //payment for online customer
        RoomType rt = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.name = :name", RoomType.class)
                .setParameter("name", name)
                .getSingleResult();
        BigDecimal totalAmount = new BigDecimal(0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        if (rt == null) {
            throw new RoomTypeNotFoundException("There is no such roomtype");
        } else {
            while (!calendar.getTime().after(endDate)) { // check if calendar is past the enddate of reservation, if not continue
                Date currentDate = calendar.getTime();
                RoomRate applicableRate = findRoomRate(rt.getRoomRates(), currentDate);
                if (applicableRate != null) {
                    totalAmount = totalAmount.add(applicableRate.getRatePerNight());
                } else {
                    throw new RoomRateNotFoundException("No Room Rate is provided for the listed dates");
                }
                calendar.add(Calendar.DAY_OF_MONTH, 1); //Go to next day 
            }
        }
        BigDecimal bigDecimalValue = new BigDecimal(numberOfRooms);
        return bigDecimalValue.multiply(totalAmount);
    }

    private RoomRate findRoomRate(List<RoomRate> roomRates, Date date) {
        RoomRate bestRate = null;
        for (RoomRate rate : roomRates) {
            // Skip PUBLISHED rates and check for valid date range and enabled rate type
            boolean isInDateRange = (rate.getStartDate() == null || !date.before(rate.getStartDate()))
                    && (rate.getEndDate() == null || !date.after(rate.getEndDate()));
            if (rate.getRateType() != RoomRateTypeEnum.PUBLISHED && isInDateRange && rate.getRateType() != RoomRateTypeEnum.DISABLED) {
                // Update bestRate if this rate has a higher priority
                if (bestRate == null || hasHigherPriority(rate, bestRate)) {
                    bestRate = rate;
                }
            }
        }
        return bestRate;
    }

    private boolean hasHigherPriority(RoomRate newRate, RoomRate currentBestRate) {
        // Define priority order based on the enum values, assuming they are ordered by priority
        // PROMOTION > PEAK > NORMAL
        if (newRate.getRateType() == RoomRateTypeEnum.PROMOTION) {
            return true;
        } else if (newRate.getRateType() == RoomRateTypeEnum.PEAK && currentBestRate.getRateType() != RoomRateTypeEnum.PROMOTION) {
            return true;
        } else if (newRate.getRateType() == RoomRateTypeEnum.NORMAL && currentBestRate.getRateType() == RoomRateTypeEnum.NORMAL) {
            return true;
        }
        return false;
    }

    public BigDecimal calculatePaymentForManagementClient(String name, Date startDate, Date endDate, int numberOfRooms) throws RoomRateNotFoundException {
        RoomType rt = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.name = :name", RoomType.class)
                .setParameter("name", name)
                .getSingleResult();
        BigDecimal totalAmount = new BigDecimal(0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        List<RoomRate> roomRates = rt.getRoomRates();
        RoomRate applicableRoomRate = findPublishedRoomRate(roomRates);
        if (applicableRoomRate == null) {
            throw new RoomRateNotFoundException("No Room Rate is provided for the listed dates");
        } else {
            while (!calendar.getTime().after(endDate)) {
                totalAmount = totalAmount.add(applicableRoomRate.getRatePerNight());
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            BigDecimal bigDecimalValue = new BigDecimal(numberOfRooms);
            return bigDecimalValue.multiply(totalAmount);
        }
    }

    private RoomRate findPublishedRoomRate(List<RoomRate> roomRates) throws RoomRateNotFoundException { //Supporting method for calculatePaymentForManagementClient
        for (RoomRate rr : roomRates) {
            if (rr.getRateType() == RoomRateTypeEnum.PUBLISHED) {
                return rr;
            }
        }
        throw new RoomRateNotFoundException("No published RoomRate found");
    }

    public List<RoomRate> findApplicableRoomRatesForPeriod(RoomType roomType, Date startDate, Date endDate) {
        List<RoomRate> applicableRoomRates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (!calendar.getTime().after(endDate)) {
            Date currentDate = calendar.getTime();
            RoomRate bestRateForDate = findRoomRate(roomType.getRoomRates(), currentDate);

            // Add the best rate for the day if it is not already in the list
            if (bestRateForDate != null && !applicableRoomRates.contains(bestRateForDate)) {
                applicableRoomRates.add(bestRateForDate);
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1); // Move to the next day
        }

        return applicableRoomRates;
    }
}
