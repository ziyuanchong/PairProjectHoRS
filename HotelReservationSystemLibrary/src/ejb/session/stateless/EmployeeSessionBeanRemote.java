/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Employee;
import exception.EmployeeAlreadyLoggedInException;
import exception.EmployeeNotFoundException;
import exception.EmployeeNotLoggedInException;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author ziyuanchong
 */
@Remote
public interface EmployeeSessionBeanRemote {

    public Long createNewEmployee(Employee employee);

    public Employee login(String employeeName, String password) throws EmployeeAlreadyLoggedInException, EmployeeNotFoundException;

    public List<Employee> retrieveAllEmployees();
    
    public void logout(Long employeeId) throws EmployeeNotLoggedInException, EmployeeNotFoundException;

    public Employee retrieveEmployeeByUsername(String username);

}
