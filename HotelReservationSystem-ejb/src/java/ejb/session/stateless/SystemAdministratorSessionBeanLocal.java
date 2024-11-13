/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import Enum.EmployeeEnum;
import entity.Employee;
import entity.Partner;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Witt
 */
@Local
public interface SystemAdministratorSessionBeanLocal {
        
    public List<Employee> retrieveAllEmployees();

    public Partner createPartner(String partnerName,String username, String password);
    
    public List<Partner> retrieveAllPartners();

    public Employee createEmployee(String name, String username, String password, EmployeeEnum employeeEnum) throws Exception;

    public void updateEmployee(Employee employee);
    
}
