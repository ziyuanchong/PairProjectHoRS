/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author ziyuanchong
 */
@Entity
public class Customer extends Guest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    private boolean loggedIn = false;

    public Customer() {
    }

    public Customer(String firstName, String lastName, String phoneNumber, String email, boolean checkIn, String password) {
        super(firstName, lastName, phoneNumber, email);
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

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

}
