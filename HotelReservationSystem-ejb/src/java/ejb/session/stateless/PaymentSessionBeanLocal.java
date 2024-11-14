/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import exception.RoomRateNotFoundException;
import exception.RoomTypeNotFoundException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Witt
 */
@Local
public interface PaymentSessionBeanLocal {

    public BigDecimal calculatePaymentForReservationClient(String name, Date startDate, Date endDate, int numberOfRooms) throws RoomTypeNotFoundException, RoomRateNotFoundException;

    public BigDecimal calculatePaymentForManagementClient(String name, Date startDate, Date endDate, int numberOfRooms) throws RoomRateNotFoundException;

    public List<RoomRate> findApplicableRoomRatesForPeriod(RoomType roomType, Date startDate, Date endDate);
}
