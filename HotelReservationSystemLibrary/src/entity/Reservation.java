/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import Enum.RoomRateTypeEnum;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ziyuanchong
 */
@Entity
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Temporal(TemporalType.DATE)
    private Date endDate;
    private int numberOfRooms;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    @XmlTransient
    private Guest guest;

    private BigDecimal totalAmount;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    @XmlTransient
    private RoomType roomType;

    @OneToMany(mappedBy = "reservation")
    @XmlTransient
    private List<ReservationRoom> reservationRooms;

    private boolean bookingByPartner;

    @ManyToOne
    @XmlTransient
    private Partner partner;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "reservation_id")
    private List<RoomRate> applicableRoomRates = new ArrayList<>();

    public Reservation() {
        this.reservationRooms = new ArrayList<>();
    }

    public Reservation(Date startDate, Date endDate, int numberOfRooms, BigDecimal totalAmount) {
        this();
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfRooms = numberOfRooms;
        this.totalAmount = totalAmount;
        this.bookingByPartner = false;
    }

    public Reservation(Date startDate, Date endDate, int numberOfRooms, RoomType roomType) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfRooms = numberOfRooms;
        this.roomType = roomType;
        this.bookingByPartner = false;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public boolean isBookingByPartner() {
        return bookingByPartner;
    }

    public void setBookingByPartner(boolean bookingByPartner) {
        this.bookingByPartner = bookingByPartner;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public List<ReservationRoom> getReservationRooms() {
        return reservationRooms;
    }

    public void setReservationRooms(List<ReservationRoom> reservationRooms) {
        this.reservationRooms = reservationRooms;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationId fields are not set
        if (!(object instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    public List<RoomRate> getApplicableRoomRates() {
        return applicableRoomRates;
    }

    public void setApplicableRoomRates(List<RoomRate> applicableRoomRates) {
        this.applicableRoomRates = applicableRoomRates;
    }

   

    @Override
    public String toString() {
        return "entity.Reservation[ id=" + reservationId + " ]";
    }

}
