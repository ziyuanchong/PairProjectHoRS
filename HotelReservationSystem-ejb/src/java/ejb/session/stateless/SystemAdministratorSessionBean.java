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
import javax.persistence.PersistenceContext;

/**
 *
 * @author Witt
 */
@Stateless
public class SystemAdministratorSessionBean implements SystemAdministratorSessionBeanRemote, SystemAdministratorSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public Employee createEmployee(String username, String password, EmployeeEnum employeeEnum) {
        Employee newEmployee = new Employee(username, password, employeeEnum);
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
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
