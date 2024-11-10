/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Customer;
import entity.Reservation;
import exception.CustomerExistException;
import exception.CustomerWrongCredentialsException;
import exception.ReservationNotFoundException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Witt
 */
@Local
public interface CustomerSessionBeanLocal {

    public void createNewCustomer(Customer customer) throws CustomerExistException;

    public Customer login(String email, String password) throws CustomerWrongCredentialsException;

    public List<Reservation> retrieveListOfReservationByCustomerId(Long customerId);

    public Reservation retrieveReservationDetails(Long reservationId, Long customerId) throws ReservationNotFoundException;

}
