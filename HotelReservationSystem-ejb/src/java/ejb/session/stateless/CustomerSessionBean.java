/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Customer;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import exception.CustomerExistException;
import exception.CustomerWrongCredentialsException;
import exception.ReservationNotFoundException;
import exception.ReservationUnavailableException;
import exception.RoomTypeNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

/**
 *
 * @author Witt
 */
@Stateless
public class CustomerSessionBean implements CustomerSessionBeanRemote, CustomerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public void createNewCustomer(Customer customer) throws CustomerExistException { //use case 2 : register
        String email = customer.getEmail();
        try {
            Customer customerExists = em.createQuery("SELECT c FROM Customer c WHERE c.email = :email", Customer.class)
                    .setParameter("email", email)
                    .getSingleResult();

            throw new CustomerExistException("This Customer is already registered");
        } catch (NoResultException ex) {
            em.persist(customer);
            em.flush();
        }

    }

    public Customer login(String email, String password) throws CustomerWrongCredentialsException { //use case 1 : login
        Customer customer = em.createQuery("SELECT c FROM Customer c WHERE c.email = :email AND c.password = :password", Customer.class)
                .setParameter("password", password)
                .setParameter("email", email)
                .getSingleResult();
        if (customer == null) {
            throw new CustomerWrongCredentialsException("Customer credentials are wrong");
        } else {
            return customer;
        }
    }

    public List<Reservation> retrieveListOfReservationByCustomerId(Long customerId) { //useCase 6 : all reservation
        Customer customer = em.find(Customer.class, customerId);
        return customer.getReservations();

        //never throw exception, assume in main that user is logged in already.
        //return empty list if no reservation
    }

    public Reservation retrieveReservationDetails(Long reservationId, Long customerId) throws ReservationNotFoundException { // use case 5: 1 reservation
        Reservation reservation = em.find(Reservation.class, reservationId);
        Customer customer = em.find(Customer.class, customerId);
        if (!customer.getReservations().contains(reservation)) {
            throw new ReservationNotFoundException("This reservationId does not exist");
        } else {
            return reservation;
        }
        //retrieve reservation first by customerId, then check all the reservations under customer
        //checks if reservation exists under customer, if doesnt return exception
    }
}
