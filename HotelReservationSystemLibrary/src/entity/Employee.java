/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import Enum.EmployeeEnum;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author ziyuanchong
 */
@Entity
public class Employee implements Serializable { 

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;
    
    @Column(length = 32, nullable = false, unique = true)
    @NotNull(message = "Username cannot be null")
    @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters long")
    private String username;
    
    @Column(length = 32, nullable = false)
    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    
    @Column(length = 32, nullable = false)
    @NotNull(message = "Name cannot be null")
    private String employeeName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Role cannot be null")
    private EmployeeEnum employeeEnum;
    
    private boolean loggedIn = false;

    public Employee() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    
    public Employee(String employeeName, String username, String password, EmployeeEnum employeeEnum) {
        this.employeeName = employeeName;
        this.username = username;
        this.password = password;
        this.employeeEnum = employeeEnum;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
    
    
/**
     * @return the employeeName
     */
    public String getEmployeeName() {
        return employeeName;
    }

    /**
     * @param employeeName the employeeName to set
     */
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
    
    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (employeeId != null ? employeeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the employeeId fields are not set
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee other = (Employee) object;
        if ((this.employeeId == null && other.employeeId != null) || (this.employeeId != null && !this.employeeId.equals(other.employeeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Username: " + username+ ", Type: "+ employeeEnum;
    }

    /**
     * @return the employeeEnum
     */
    public EmployeeEnum getEmployeeEnum() {
        return employeeEnum;
    }

    /**
     * @param employeeEnum the employeeEnum to set
     */
    public void setEmployeeEnum(EmployeeEnum employeeEnum) {
        this.employeeEnum = employeeEnum;
    }
    
}
