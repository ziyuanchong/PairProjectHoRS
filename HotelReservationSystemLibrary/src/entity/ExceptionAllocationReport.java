/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import Enum.AllocationExceptionTypeEnum;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author ziyuanchong
 */
@Entity
public class ExceptionAllocationReport implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;
    
    @Enumerated(EnumType.STRING)
    private AllocationExceptionTypeEnum exceptionType;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date generatedAt;
    
    private String roomTypeRequested;
    
    @OneToOne
    @JoinColumn(name = "reservation_room_id", unique= true)
    private ReservationRoom reservationRoom;

    public ExceptionAllocationReport() {
    }

    public ExceptionAllocationReport(AllocationExceptionTypeEnum exceptionType, Date generatedAt, String roomTypeRequested, ReservationRoom reservationRoom) {
        this.exceptionType = exceptionType;
        this.generatedAt = generatedAt;
        this.roomTypeRequested = roomTypeRequested;
        this.reservationRoom = reservationRoom;
    }

    public AllocationExceptionTypeEnum getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(AllocationExceptionTypeEnum exceptionType) {
        this.exceptionType = exceptionType;
    }

    public Date getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Date generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getRoomTypeRequested() {
        return roomTypeRequested;
    }

    public void setRoomTypeRequested(String roomTypeRequested) {
        this.roomTypeRequested = roomTypeRequested;
    }

    public ReservationRoom getReservationRoom() {
        return reservationRoom;
    }

    public void setReservationRoom(ReservationRoom reservationRoom) {
        this.reservationRoom = reservationRoom;
    }
    
    

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reportId != null ? reportId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reportId fields are not set
        if (!(object instanceof ExceptionAllocationReport)) {
            return false;
        }
        ExceptionAllocationReport other = (ExceptionAllocationReport) object;
        if ((this.reportId == null && other.reportId != null) || (this.reportId != null && !this.reportId.equals(other.reportId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ExceptionAllocationReport[ id=" + reportId + " ]";
    }
    
}
