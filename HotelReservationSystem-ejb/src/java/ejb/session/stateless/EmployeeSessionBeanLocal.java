/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Employee;
import exception.EmployeeAlreadyLoggedInException;
import exception.EmployeeNotFoundException;
import exception.EmployeeNotLoggedInException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author ziyuanchong
 */
@Local
public interface EmployeeSessionBeanLocal {

    public Long createNewEmployee(Employee employee);

    public List<Employee> retrieveAllEmployees();
    
    public Employee login(String employeeName, String password) throws EmployeeAlreadyLoggedInException, EmployeeNotFoundException;

    public void logout(Long employeeId) throws EmployeeNotLoggedInException, EmployeeNotFoundException;

    public Employee retrieveEmployeeByUsername(String username);
    
}
