/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import Enum.EmployeeEnum;
import entity.Employee;
import entity.Partner;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Witt
 */
@Stateless
public class SystemAdministratorSessionBean implements SystemAdministratorSessionBeanRemote, SystemAdministratorSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public Employee createEmployee(String name, String username, String password, EmployeeEnum employeeEnum) throws Exception {
        // Check for existing username
        try {
            Employee existingEmployee = em.createQuery("SELECT e FROM Employee e WHERE e.username = :username", Employee.class)
                    .setParameter("username", username)
                    .getSingleResult();
            if (existingEmployee != null) {
                throw new Exception("Username already exists.");
            }
        } catch (NoResultException e) {
            // No existing employee, safe to create new one
        }

        Employee newEmployee = new Employee(name, username, password, employeeEnum);
        em.persist(newEmployee);
        return newEmployee;
    }

    public List<Employee> retrieveAllEmployees() {
        return em.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();
    }

    public Partner createPartner(String partnerName, String password) {
        Partner newPartner = new Partner(partnerName, password);
        em.persist(newPartner);
        return newPartner;
    }

    public List<Partner> retrieveAllPartners() {
        return em.createQuery("SELECT p FROM Partner p", Partner.class).getResultList();
    }

    public void updateEmployee(Employee employee) {
        em.merge(employee);  // Update the entity in the database
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
