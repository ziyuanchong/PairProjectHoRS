/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import exception.GeneralException;
import exception.GuestExistException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 * @author ziyuanchong
 */
@Stateless
public class GuestSessionBean implements GuestSessionBeanRemote, GuestSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public Guest createNewGuest(Guest guest) throws GuestExistException, GeneralException {
        try {
            // Check if guest with the same email already exists
            Query query = em.createQuery("SELECT g FROM Guest g WHERE g.email = :email");
            query.setParameter("email", guest.getEmail());
            
            if (!query.getResultList().isEmpty()) {
                throw new GuestExistException("A guest with the same email already exists.");
            }
            
            em.persist(guest);
            em.flush();
            return guest;
        } 
        catch (PersistenceException ex) {
            throw new GeneralException("An unexpected error occurred while creating a new guest: " + ex.getMessage());
        }
    }
    

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
