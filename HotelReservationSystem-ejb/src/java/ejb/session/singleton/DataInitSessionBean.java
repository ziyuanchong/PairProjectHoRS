/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author ziyuanchong
 */
@Singleton
@LocalBean
@Startup

public class DataInitSessionBean {

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    
    @PostConstruct
    public void postConstruct() {
        if(em.find(Employee.class, 1l) == null) {
            employeeSessionBeanLocal.createNewEmployee(new Employee("A", "password"));
            employeeSessionBeanLocal.createNewEmployee(new Employee("B", "password"));
            employeeSessionBeanLocal.createNewEmployee(new Employee("C", "password"));
            employeeSessionBeanLocal.createNewEmployee(new Employee("D", "password"));
            employeeSessionBeanLocal.createNewEmployee(new Employee("E", "password"));



        }
        
    }
    
    
    
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
