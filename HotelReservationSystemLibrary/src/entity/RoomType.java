/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import Enum.RoomTypeEnum;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Witt
 */
@Entity
public class RoomType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;

    @Column(length = 32, nullable = false, unique = true)
    private String name; // Unique name for room type, e.g., "Deluxe", "Suite"

    @Column(length = 1000)
    private String description;

    private double size;

    @Column(length = 32)
    private String bed;

    private int capacity;

    @ElementCollection
    private List<String> amenities = new ArrayList<>(); // List of amenities

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomTypeEnum roomCategory; // Enum to represent categories like DELUXE, SUITE

    private boolean available;

    @Column(length = 32) // Store the name of the next higher room type
    private String nextHigherRoomType;

    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "roomType")
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL)
    private List<RoomRate> roomRates = new ArrayList<>(); // One-to-many relationship with RoomRate

    public RoomType(String name, String description, double size, String bed, int capacity, List<String> amenities, RoomTypeEnum roomCategory, boolean available) {

        this.name = name;
        this.description = description;
        this.size = size;
        this.bed = bed;
        this.capacity = capacity;
        this.amenities = amenities;
        this.roomCategory = roomCategory;
        this.available = available;

    }

    public RoomType() {

    }

    public String getNextHigherRoomType() {
        return nextHigherRoomType;
    }

    public void setNextHigherRoomType(String nextHigherRoomType) {
        this.nextHigherRoomType = nextHigherRoomType;
    }

    public RoomTypeEnum getRoomCategory() {
        return roomCategory;
    }

    public void setRoomCategory(RoomTypeEnum roomCategory) {
        this.roomCategory = roomCategory;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // Getters and Setters for all fields
    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getBed() {
        return bed;
    }

    public void setBed(String bed) {
        this.bed = bed;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public RoomTypeEnum getRoomTypeEnum() {
        return roomCategory;
    }

    public void setRoomTypeEnum(RoomTypeEnum roomCategory) {
        this.roomCategory = roomCategory;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<RoomRate> getRoomRates() {
        return roomRates;
    }

    public void setRoomRates(List<RoomRate> roomRates) {
        this.roomRates = roomRates;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getRoomTypeId() != null ? getRoomTypeId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RoomType)) {
            return false;
        }
        RoomType other = (RoomType) object;
        return (this.getRoomTypeId() != null || other.getRoomTypeId() == null) && (this.getRoomTypeId() == null || this.roomTypeId.equals(other.roomTypeId));
    }

    @Override
    public String toString() {
        String amenitiesStr = amenities != null ? String.join(", ", amenities) : "No amenities listed";

        return "RoomType{"
                + "ID=" + roomTypeId
                + ", Name='" + name + '\''
                + ", Description='" + description + '\''
                + ", Size=" + size
                + ", Bed='" + bed + '\''
                + ", Capacity=" + capacity
                + ", Amenities=" + amenitiesStr
                + ", Room Category=" + roomCategory
                + ", Available=" + available
                + ", Next Higher Room Type='" + (nextHigherRoomType != null ? nextHigherRoomType : "None") + '\''
                + '}';
    }

}
