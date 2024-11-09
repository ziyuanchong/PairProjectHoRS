/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 *
 * @author ziyuanchong
 */
@Entity
public class Customer extends Guest implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String password;
    

    public Customer(){
    }

    public Customer(String firstName, String lastName, String phoneNumber, String email, boolean checkIn, String password) {
        super(firstName, lastName, phoneNumber, email,checkIn);
        this.password = password;
        
        
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "entity.Customer[ id=" + getGuestId() + " ]";
    }
    
}
