/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package ejb.session.singleton;

import Enum.EmployeeEnum;
import Enum.RoomRateTypeEnum;
import Enum.RoomTypeEnum;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.OperationManagerSessionBeanLocal;
import ejb.session.stateless.SalesManagerSessionBeanLocal;
import entity.Employee;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import exception.RoomTypeNotFoundException;
import java.math.BigDecimal;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author ziyuanchong
 */
@Singleton
@LocalBean
@Startup

public class DataInitSessionBean {

    @EJB
    private SalesManagerSessionBeanLocal salesManagerSessionBean;

    @EJB
    private OperationManagerSessionBeanLocal operationManagerSessionBean;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBean;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @PostConstruct
    public void postConstruct() {
        initializeEmployees();
        initializeRoomTypes();
        initializeRoomRates();
        initializeRooms();
    }

    private void initializeEmployees() {
        if (em.find(Employee.class, 1l) == null) {
            employeeSessionBean.createNewEmployee(new Employee("sysadmin", "sysadmin", "password", EmployeeEnum.SYSTEMADMINISTRATOR));
            employeeSessionBean.createNewEmployee(new Employee("opmanager", "opmanager", "password", EmployeeEnum.OPERATIONMANAGER));
            employeeSessionBean.createNewEmployee(new Employee("salesmanager", "salesmanager", "password", EmployeeEnum.SALESMANAGER));
            employeeSessionBean.createNewEmployee(new Employee("guestrelo", "guestrelo", "password", EmployeeEnum.GUESTRELATIONOFFICER));
        }
    }

    private void initializeRoomTypes() {
        if (em.find(RoomType.class, 1l) == null) {
            RoomType deluxeRoom = operationManagerSessionBean.createRoomType("Deluxe Room", "Deluxe room with queen bed", 25.0, "Queen", 2, Arrays.asList("WiFi", "TV"), RoomTypeEnum.DELUXE, true, "Premier Room");
            RoomType premierRoom = operationManagerSessionBean.createRoomType("Premier Room", "Premier room with king bed", 30.0, "King", 3, Arrays.asList("WiFi", "TV", "Mini Bar"), RoomTypeEnum.PREMIER, true, "Family Room");
            RoomType familyRoom = operationManagerSessionBean.createRoomType("Family Room", "Family room with two queen beds", 35.0, "Two Queen Beds", 4, Arrays.asList("WiFi", "TV", "Sofa"), RoomTypeEnum.FAMILY, true, "Junior Suite");
            RoomType juniorSuite = operationManagerSessionBean.createRoomType("Junior Suite", "Junior Suite with luxury amenities", 40.0, "King", 4, Arrays.asList("WiFi", "TV", "Kitchenette"), RoomTypeEnum.JUNIOR, true, "Grand Suite");
            RoomType grandSuite = operationManagerSessionBean.createRoomType("Grand Suite", "Grand Suite with ocean view", 50.0, "King", 5, Arrays.asList("WiFi", "TV", "Mini Bar", "Ocean View"), RoomTypeEnum.GRAND, true, null);
        }
    }

    private void initializeRoomRates() {
        try {
            if (em.find(RoomRate.class, 1l) == null) {
                RoomType deluxeRoomType = operationManagerSessionBean.findRoomTypeByName("Deluxe Room");
                RoomType premierRoomType = operationManagerSessionBean.findRoomTypeByName("Premier Room");
                RoomType familyRoomType = operationManagerSessionBean.findRoomTypeByName("Family Room");
                RoomType juniorSuiteType = operationManagerSessionBean.findRoomTypeByName("Junior Suite");
                RoomType grandSuiteType = operationManagerSessionBean.findRoomTypeByName("Grand Suite");

                salesManagerSessionBean.createRoomRate("Deluxe Room Published", RoomRateTypeEnum.PUBLISHED, new BigDecimal("100.00"), null, null, deluxeRoomType.getRoomTypeId());
                salesManagerSessionBean.createRoomRate("Deluxe Room Normal", RoomRateTypeEnum.NORMAL, new BigDecimal("50.00"), null, null, deluxeRoomType.getRoomTypeId());
                salesManagerSessionBean.createRoomRate("Premier Room Published", RoomRateTypeEnum.PUBLISHED, new BigDecimal("200.00"), null, null, premierRoomType.getRoomTypeId());
                salesManagerSessionBean.createRoomRate("Premier Room Normal", RoomRateTypeEnum.NORMAL, new BigDecimal("100.00"), null, null, premierRoomType.getRoomTypeId());
                salesManagerSessionBean.createRoomRate("Family Room Published", RoomRateTypeEnum.PUBLISHED, new BigDecimal("300.00"), null, null, familyRoomType.getRoomTypeId());
                salesManagerSessionBean.createRoomRate("Family Room Normal", RoomRateTypeEnum.NORMAL, new BigDecimal("150.00"), null, null, familyRoomType.getRoomTypeId());
                salesManagerSessionBean.createRoomRate("Junior Suite Published", RoomRateTypeEnum.PUBLISHED, new BigDecimal("400.00"), null, null, juniorSuiteType.getRoomTypeId());
                salesManagerSessionBean.createRoomRate("Junior Suite Normal", RoomRateTypeEnum.NORMAL, new BigDecimal("200.00"), null, null, juniorSuiteType.getRoomTypeId());
                salesManagerSessionBean.createRoomRate("Grand Suite Published", RoomRateTypeEnum.PUBLISHED, new BigDecimal("500.00"), null, null, grandSuiteType.getRoomTypeId());
                salesManagerSessionBean.createRoomRate("Grand Suite Normal", RoomRateTypeEnum.NORMAL, new BigDecimal("250.00"), null, null, grandSuiteType.getRoomTypeId());
            }
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("An error occurred initializing room rates: " + ex.getMessage());
        }
    }

    private void initializeRooms() {
        try {
            if (em.find(Room.class, 1l) == null) {
                RoomType deluxeRoomType = operationManagerSessionBean.findRoomTypeByName("Deluxe Room");
                RoomType premierRoomType = operationManagerSessionBean.findRoomTypeByName("Premier Room");
                RoomType familyRoomType = operationManagerSessionBean.findRoomTypeByName("Family Room");
                RoomType juniorSuiteType = operationManagerSessionBean.findRoomTypeByName("Junior Suite");
                RoomType grandSuiteType = operationManagerSessionBean.findRoomTypeByName("Grand Suite");

                operationManagerSessionBean.createRoom("0101", true, deluxeRoomType.getRoomTypeId());
                operationManagerSessionBean.createRoom("0201", true, deluxeRoomType.getRoomTypeId());
                operationManagerSessionBean.createRoom("0301", true, deluxeRoomType.getRoomTypeId());
                operationManagerSessionBean.createRoom("0401", true, deluxeRoomType.getRoomTypeId());
                operationManagerSessionBean.createRoom("0501", true, deluxeRoomType.getRoomTypeId());

                operationManagerSessionBean.createRoom("0102", true, premierRoomType.getRoomTypeId());
                operationManagerSessionBean.createRoom("0202", true, premierRoomType.getRoomTypeId());
                operationManagerSessionBean.createRoom("0302", true, premierRoomType.getRoomTypeId());
                operationManagerSessionBean.createRoom("0402", true, premierRoomType.getRoomTypeId());
                operationManagerSessionBean.createRoom("0502", true, premierRoomType.getRoomTypeId());

                operationManagerSessionBean.createRoom("0103", true, familyRoomType.getRoomTypeId());
                operationManagerSessionBean.createRoom("0203", true, familyRoomType.getRoomTypeId());
                operationManagerSessionBean.createRoom("0303", true, familyRoomType.getRoomTypeId());
                operationManagerSessionBean.createRoom("0403", true, familyRoomType.getRoomTypeId());
                operationManagerSessionBean.createRoom("0503", true, familyRoomType.getRoomTypeId());

                operationManagerSessionBean.createRoom("0104", true, juniorSuiteType.getRoomTypeId());
                operationManagerSessionBean.createRoom("0204", true, juniorSuiteType.getRoomTypeId());
                operationManagerSessionBean.createRoom("0304", true, juniorSuiteType.getRoomTypeId());
                operationManagerSessionBean.createRoom("0404", true, juniorSuiteType.getRoomTypeId());
                operationManagerSessionBean.createRoom("0504", true, juniorSuiteType.getRoomTypeId());

                operationManagerSessionBean.createRoom("0105", true, grandSuiteType.getRoomTypeId());
                operationManagerSessionBean.createRoom("0205", true, grandSuiteType.getRoomTypeId());
                operationManagerSessionBean.createRoom("0305", true, grandSuiteType.getRoomTypeId());
                operationManagerSessionBean.createRoom("0405", true, grandSuiteType.getRoomTypeId());
                operationManagerSessionBean.createRoom("0505", true, grandSuiteType.getRoomTypeId());
            }
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("An error occurred initializing rooms: " + ex.getMessage());
        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
