/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Employee;
import exception.EmployeeAlreadyLoggedInException;
import exception.EmployeeNotFoundException;
import exception.EmployeeNotLoggedInException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ziyuanchong
 */
@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanRemote, EmployeeSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewEmployee(Employee employee) {
        em.persist(employee);
        em.flush();

        return employee.getEmployeeId();
    }

    @Override
    public Employee retrieveEmployeeByUsername(String username) {
        try {
            return em.createQuery("SELECT e FROM Employee e WHERE e.username = :username", Employee.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null; // No employee found with this username
        }
    }

    @Override
    public List<Employee> retrieveAllEmployees() {
        Query query = em.createQuery("SELECT e FROM Employee e");

        return query.getResultList();
    }

    @Override
    public Employee login(String username, String password) throws EmployeeAlreadyLoggedInException, EmployeeNotFoundException {
        try {
            Employee employee = em.createQuery("SELECT e FROM Employee e WHERE e.username = :username AND e.password = :password", Employee.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .getSingleResult();

            if (employee.isLoggedIn()) {
                throw new EmployeeAlreadyLoggedInException("Employee is already logged in.");
            }

            employee.setLoggedIn(true);
            em.merge(employee);
            return employee;
        } catch (NoResultException ex) {
            throw new EmployeeNotFoundException("Employee not found or invalid credentials.");
        }
    }

    @Override
    public void logout(Long employeeId) throws EmployeeNotLoggedInException, EmployeeNotFoundException {
        Employee employee = em.find(Employee.class, employeeId);
        if (employee == null) {
            throw new EmployeeNotFoundException("Employee not found with ID: " + employeeId);
        }

        if (!employee.isLoggedIn()) {
            throw new EmployeeNotLoggedInException("Employee is not logged in.");
        }

        employee.setLoggedIn(false);
        em.merge(employee);
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
