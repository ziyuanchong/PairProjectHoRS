/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HelperClass;

import entity.RoomType;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Witt
 */
public class RoomTypeAvailability implements Serializable {

    private RoomType roomType;
    private int availableRooms;

    public RoomTypeAvailability(RoomType roomType, int availableRooms) {
        this.roomType = roomType;
        this.availableRooms = availableRooms;
    }

    // Getter for RoomType
    public RoomType getRoomType() {
        return roomType;
    }

    // Setter for RoomType
    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    // Getter for AvailableRooms
    public int getAvailableRooms() {
        return availableRooms;
    }

    // Setter for AvailableRooms
    public void setAvailableRooms(int availableRooms) {
        this.availableRooms = availableRooms;
    }

}
