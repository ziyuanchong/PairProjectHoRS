/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Customer;
import entity.Reservation;
import exception.CustomerAlreadyLoggedInException;
import exception.CustomerExistException;
import exception.CustomerNotFoundException;
import exception.CustomerNotLoggedInException;
import exception.CustomerWrongCredentialsException;
import exception.ReservationNotFoundException;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author Witt
 */
@Remote
public interface CustomerSessionBeanRemote {

    public void createNewCustomer(Customer customer) throws CustomerExistException;

    public Customer login(String email, String password) throws CustomerWrongCredentialsException, CustomerAlreadyLoggedInException;

    public List<Reservation> retrieveListOfReservationByCustomerId(Long customerId);

    public Reservation retrieveReservationDetails(Long reservationId, Long customerId) throws ReservationNotFoundException;

    public void logout(Long customerId) throws CustomerNotLoggedInException, CustomerNotFoundException;
    
}
