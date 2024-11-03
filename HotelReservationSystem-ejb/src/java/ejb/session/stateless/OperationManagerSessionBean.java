/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.RoomType;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Witt
 */
@Stateless
public class OperationManagerSessionBean implements OperationManagerSessionBeanRemote, OperationManagerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public RoomType createRoomType(String name, String description, double size, String bed, int capacity, String amenities) {
        RoomType newRoomType = new RoomType(name, description, size,  bed, capacity, amenities);
        em.persist(newRoomType);
        return newRoomType;
    }

    public RoomType viewRoomTypeDetails(String name) {
        RoomType chosenRoomType = em.createQuery(
                "SELECT r FROM RoomType r WHERE r.name =: name", RoomType.class)
                .setParameter("name", name)
                .getSingleResult();
        return chosenRoomType;
    }

    public void deleteRoomType(String name) { //unless we set name as primary identifier
        RoomType deleteRoomType = em.createQuery(
                "SELECT r FROM RoomType r where r.name =: name", RoomType.class)
                .setParameter("name", name)
                .getSingleResult();
        em.remove(deleteRoomType);
    }

    public RoomType updateRoomType(RoomType updatedRoomType) {
        RoomType existingRoomType = em.find(RoomType.class, updatedRoomType.getId());
        existingRoomType.setName(updatedRoomType.getName());
        existingRoomType.setDescription(updatedRoomType.getDescription());
        existingRoomType.setSize(updatedRoomType.getSize());
        existingRoomType.setBed(updatedRoomType.getBed());
        existingRoomType.setCapacity(updatedRoomType.getCapacity());
        existingRoomType.setAmenities(updatedRoomType.getAmenities());
        em.persist(existingRoomType);
        return existingRoomType;
    }

    public List<RoomType> retrieveListOfRoomTypes() {
        List<RoomType> list = em.createQuery("SELECT r FROM RoomType r", RoomType.class).getResultList();
        return list;
    }
}
