/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ziyuanchong
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Guest implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guestId;
    
    @Column(length = 32, nullable = false)
    private String firstName;
    
    @Column(length = 32, nullable = false)
    private String lastName;
    
    @Column(length = 64, nullable = false)
    private String phoneNumber;
    
    @Column(length= 256, nullable = false, unique = true)
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    private String email;
    
    @Column(nullable = false)
    private boolean checkIn = false;
    
    @OneToMany(mappedBy = "guest")
    @XmlTransient
    private List<Reservation> reservations = new ArrayList<>();

    public Guest(String firstName, String lastName, String phoneNumber, String email) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.checkIn = false;
    }
    

    public Guest() {
        this.reservations = new ArrayList<>();
    }

    public boolean isCheckIn() {
        return checkIn;
    }

    public void setCheckIn(boolean checkIn) {
        this.checkIn = checkIn;
    }
    
    

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
    
    

    public Long getGuestId() {
        return guestId;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (guestId != null ? guestId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the guestId fields are not set
        if (!(object instanceof Guest)) {
            return false;
        }
        Guest other = (Guest) object;
        if ((this.guestId == null && other.guestId != null) || (this.guestId != null && !this.guestId.equals(other.guestId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Guest[ id=" + guestId + " ]";
    }
    
}
