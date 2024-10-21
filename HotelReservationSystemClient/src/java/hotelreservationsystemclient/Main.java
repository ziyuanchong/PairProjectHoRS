/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package hotelreservationsystemclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import entity.Employee;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author ziyuanchong
 */
public class Main {

    @EJB
    private static EmployeeSessionBeanRemote employeeSessionBeanRemote;

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        List<Employee> employees = employeeSessionBeanRemote.retrieveAllEmployees();
        
        for(Employee employee:employees) {
            System.out.println("employeeId=" + employee.getEmployeeId() + "; employeeName=" + employee.getEmployeeName());
        }
        
    }
    
    
    
}
