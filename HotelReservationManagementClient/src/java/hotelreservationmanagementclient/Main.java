/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package hotelreservationmanagementclient;

import Enum.EmployeeEnum;
import Enum.RoomRateTypeEnum;
import Enum.RoomTypeEnum;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.GuestRelationOfficerSessionBeanRemote;
import ejb.session.stateless.OperationManagerSessionBeanRemote;
import ejb.session.stateless.PaymentSessionBeanRemote;
import ejb.session.stateless.RoomAllocationSessionBeanRemote;
import ejb.session.stateless.SalesManagerSessionBeanRemote;
import ejb.session.stateless.SystemAdministratorSessionBeanRemote;
import entity.Employee;
import entity.ExceptionAllocationReport;
import entity.Guest;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import exception.EmployeeAlreadyLoggedInException;
import exception.EmployeeNotFoundException;
import exception.GeneralException;
import exception.GuestCheckInException;
import exception.GuestCheckOutException;
import exception.GuestExistException;
import exception.ReservationNotFoundException;
import exception.ReservationUnavailableException;
import exception.RoomNotAvailableException;
import exception.RoomNotFoundException;
import exception.RoomRateNotFoundException;
import exception.RoomTypeIsDisabledException;
import exception.RoomTypeNotFoundException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import javax.ejb.EJB;

/**
 *
 * @author ziyuanchong
 */
public class Main {

    @EJB
    private static PaymentSessionBeanRemote paymentSessionBean;

    @EJB
    private static RoomAllocationSessionBeanRemote roomAllocationSessionBean;

    @EJB
    private static GuestRelationOfficerSessionBeanRemote guestRelationOfficerSessionBean;

    @EJB
    private static SystemAdministratorSessionBeanRemote systemAdministratorSessionBean;

    @EJB
    private static SalesManagerSessionBeanRemote salesManagerSessionBean;

    @EJB
    private static OperationManagerSessionBeanRemote operationManagerSessionBean;

    @EJB
    private static EmployeeSessionBeanRemote employeeSessionBean;

    private Employee currentEmployee;

    private Scanner sc = new Scanner(System.in);

    public void runApp() {
        while (true) {
            if (currentEmployee == null) {
                System.out.println("1: Login");
                System.out.println("2: Exit");
                System.out.print("Enter option> ");
                int option = sc.nextInt();
                sc.nextLine(); // consume the newline

                if (option == 1) {
                    doLogin();
                } else if (option == 2) {
                    System.out.println("Exiting application. Goodbye!");
                    break; // Exit the loop and end the application
                } else {
                    System.out.println("Invalid option. Please try again.");
                }
            } else {
                showMainMenu();
            }
        }
    }

    private void doLogin() {
        System.out.println("*** HoRS Management Client :: Login ***\n");
        System.out.print("Enter username> ");
        String username = sc.nextLine().trim();
        System.out.print("Enter password> ");
        String password = sc.nextLine().trim();

        try {
            currentEmployee = employeeSessionBean.login(username, password);
            System.out.println("Login successful as " + currentEmployee.getEmployeeEnum());
        } catch (EmployeeAlreadyLoggedInException | EmployeeNotFoundException ex) {
            System.out.println("Login failed: " + ex.getMessage());
        }
    }

    private void doLogout() {
        if (currentEmployee != null) {
            currentEmployee.setLoggedIn(false);  // Set loggedIn to false
            try {
                systemAdministratorSessionBean.updateEmployee(currentEmployee); // Persist the update
                System.out.println("Logged out successfully.");
            } catch (Exception ex) {
                System.out.println("Failed to update employee logout status: " + ex.getMessage());
            }
        }
        currentEmployee = null;
    }

    private void showMainMenu() {
        System.out.println("\n*** HoRS Management Client ***");

        switch (currentEmployee.getEmployeeEnum()) {
            case SYSTEMADMINISTRATOR:
                System.out.println("1: Employee Logout");
                System.out.println("3: Create New Employee");
                System.out.println("4: View All Employees");
                System.out.println("5: Create New Partner");
                System.out.println("6: View All Partners");
                break;

            case OPERATIONMANAGER:
                System.out.println("1: Employee Logout");
                System.out.println("7: Create New Room Type");
                System.out.println("8: View Room Type Details");
                System.out.println("9: Update Room Type");
                System.out.println("10: Delete Room Type");
                System.out.println("11: View All Room Types");
                System.out.println("12: Create New Room");
                System.out.println("13: Update Room");
                System.out.println("14: Delete Room");
                System.out.println("15: View All Rooms");
                System.out.println("16: View Room Allocation Exception Report");
                break;

            case SALESMANAGER:
                System.out.println("1: Employee Logout");
                System.out.println("17: Create New Room Rate");
                System.out.println("18: View Room Rate Details");
                System.out.println("19: Update Room Rate");
                System.out.println("20: Delete Room Rate");
                System.out.println("21: View All Room Rates");
                break;

            case GUESTRELATIONOFFICER:
                System.out.println("1: Employee Logout");
                System.out.println("23: Walk-in Search Room");
                System.out.println("24: Walk-in Reserve Room");
                System.out.println("25: Check-in Guest");
                System.out.println("26: Check-out Guest");
                break;

            default:
                System.out.println("Invalid employee role.");
                return;
        }

        int option = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.print("Enter option> ");
            try {
                option = sc.nextInt();
                sc.nextLine(); // Consume newline
                validInput = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer option.");
                sc.nextLine(); // Clear the invalid input
            }
        }

        processOption(option);
    }

    private void processOption(int option) {
        switch (option) {
            case 1:
                doLogout();
                break;

            // System Administrator options (use cases 3-6)
            case 3:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.SYSTEMADMINISTRATOR) {
                    createNewEmployee();
                }
                break;
            case 4:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.SYSTEMADMINISTRATOR) {
                    viewAllEmployees();
                }
                break;
            case 5:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.SYSTEMADMINISTRATOR) {
                    createNewPartner();
                }
                break;
            case 6:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.SYSTEMADMINISTRATOR) {
                    viewAllPartners();
                }
                break;

            // Operation Manager options (use cases 7-16)
            case 7:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.OPERATIONMANAGER) {
                    createNewRoomType();
                }
                break;
            case 8:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.OPERATIONMANAGER) {
                    viewRoomTypeDetails();
                }
                break;
            case 9:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.OPERATIONMANAGER) {
                    updateRoomType();
                }
                break;
            case 10:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.OPERATIONMANAGER) {
                    deleteRoomType();
                }
                break;
            case 11:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.OPERATIONMANAGER) {
                    viewAllRoomTypes();
                }
                break;
            case 12:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.OPERATIONMANAGER) {
                    createNewRoom();
                }
                break;
            case 13:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.OPERATIONMANAGER) {
                    updateRoom();
                }
                break;
            case 14:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.OPERATIONMANAGER) {
                    deleteRoom();
                }
                break;
            case 15:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.OPERATIONMANAGER) {
                    viewAllRooms();
                }
                break;
            case 16:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.OPERATIONMANAGER) {
                    viewRoomAllocationExceptionReport();
                }
                break;

            case 17:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.SALESMANAGER) {
                    createNewRoomRate();
                }
                break;
            case 18:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.SALESMANAGER) {
                    viewRoomRateDetails();
                }
                break;
            case 19:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.SALESMANAGER) {
                    updateRoomRate();
                }
                break;
            case 20:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.SALESMANAGER) {
                    deleteRoomRate();
                }
                break;
            case 21:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.SALESMANAGER) {
                    viewAllRoomRates();
                }
                break;
            case 22:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.SYSTEMADMINISTRATOR) {
                    allocateRoomManually();
                }
                break;

            case 23:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.GUESTRELATIONOFFICER) {
                    walkInSearchRoom();
                }
                break;
            case 24:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.GUESTRELATIONOFFICER) {
                    walkInReserveRoom();
                }
                break;
            case 25:
                if (currentEmployee.getEmployeeEnum() == EmployeeEnum.GUESTRELATIONOFFICER) {
                    checkInGuest();
                }
                break;
            case 26:
                System.out.println("Exiting...");
                System.exit(0);
                break;

            default:
                System.out.println("Invalid option, please try again.");
                break;
        }
    }

    private void createNewEmployee() {
        try {
            System.out.println("*** Create New Employee ***");
            System.out.print("Enter name> ");
            String name = sc.nextLine().trim();
            System.out.print("Enter username> ");
            String username = sc.nextLine().trim();
            System.out.print("Enter password> ");
            String password = sc.nextLine().trim();
            System.out.print("Enter role (SYSTEMADMINISTRATOR, OPERATIONMANAGER, SALESMANAGER, GUESTRELATIONOFFICER)> ");
            String roleInput = sc.nextLine().trim();
            EmployeeEnum role = EmployeeEnum.valueOf(roleInput.toUpperCase());

            Employee newEmployee = systemAdministratorSessionBean.createEmployee(name, username, password, role);
            System.out.println("Employee created successfully with ID: " + newEmployee.getEmployeeId());
        } catch (Exception ex) {
            System.out.println("An error occurred: " + ex.getMessage());
        }
    }

    private void viewAllEmployees() {
        System.out.println("*** View All Employees ***");
        List<Employee> employees = employeeSessionBean.retrieveAllEmployees();
        for (Employee employee : employees) {
            System.out.println(employee);
        }
    }

    private void createNewPartner() {
        System.out.println("*** Create New Partner ***");
        System.out.print("Enter partner name> ");
        String partnerName = sc.nextLine().trim();
        System.out.print("Enter username> ");
        String username = sc.nextLine().trim();
        System.out.print("Enter password> ");
        String password = sc.nextLine().trim();

        try {
            Partner newPartner = systemAdministratorSessionBean.createPartner(partnerName, username, password);
            System.out.println("Partner created successfully with ID: " + newPartner.getPartnerId());
        } catch (Exception ex) {
            System.out.println("Failed to create partner: " + ex.getMessage());
        }
    }

    private void viewAllPartners() {
        System.out.println("*** View All Partners ***");
        try {
            List<Partner> partners = systemAdministratorSessionBean.retrieveAllPartners();
            for (Partner partner : partners) {
                System.out.println(partner.getPartnerId() + ". " + partner.getPartnerName());
            }
        } catch (Exception ex) {
            System.out.println("An error occurred while retrieving partners: " + ex.getMessage());
        }
    }

    private void createNewRoomType() {
        try {
            System.out.println("*** Create New Room Type ***");
            System.out.print("Enter room type name> ");
            String name = sc.nextLine().trim();

            System.out.print("Enter room type description> ");
            String description = sc.nextLine().trim();

            double size = getValidDouble("Enter room size (square meters)> ");
            sc.nextLine();

            System.out.print("Enter bed type> ");
            String bed = sc.nextLine().trim();

            int capacity = getValidInt("Enter max occupancy capacity> ");
            sc.nextLine();

            System.out.print("Enter amenities (comma-separated)> ");
            List<String> amenities = Arrays.asList(sc.nextLine().split(","));

            RoomTypeEnum category = getValidRoomTypeEnum();

            System.out.print("Enter next higher room type (or leave blank if none)> ");
            String nextHigherRoomType = sc.nextLine().trim();

            boolean available = getValidBoolean("Is room type available? (true/false)> ");

            RoomType newRoomType = operationManagerSessionBean.createRoomType(name, description, size, bed, capacity, amenities, category, available, nextHigherRoomType);

            // Call your business logic here to create the room type using the gathered inputs
            System.out.println("Room type created successfully!");

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private void viewRoomTypeDetails() {
        System.out.println("*** View Room Type Details ***");
        System.out.print("Enter room type name> ");
        String name = sc.nextLine().trim();

        try {
            RoomType roomType = operationManagerSessionBean.viewRoomTypeDetails(name);
            System.out.println("Room Type Details: " + roomType);
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("Room Type not found: " + ex.getMessage());
        }
    }

    private void updateRoomType() {
        System.out.println("*** Update Room Type ***");
        System.out.print("Enter room type name> ");
        String name = sc.nextLine().trim();

        try {
            RoomType roomType = operationManagerSessionBean.viewRoomTypeDetails(name);

            System.out.print("Enter new description (leave blank to keep current)> ");
            String description = sc.nextLine().trim();
            if (!description.isEmpty()) {
                roomType.setDescription(description);
            }

            System.out.print("Enter new size (0 to keep current)> ");
            double size = sc.nextDouble();
            if (size > 0) {
                roomType.setSize(size);
            }

            sc.nextLine(); // Consume newline
            System.out.print("Enter new bed type (leave blank to keep current)> ");
            String bed = sc.nextLine().trim();
            if (!bed.isEmpty()) {
                roomType.setBed(bed);
            }

            System.out.print("Enter new capacity (0 to keep current)> ");
            int capacity = sc.nextInt();
            if (capacity > 0) {
                roomType.setCapacity(capacity);
            }

            sc.nextLine(); // Consume newline
            System.out.print("Enter new amenities (comma-separated, leave blank to keep current)> ");
            String amenities = sc.nextLine().trim();
            if (!amenities.isEmpty()) {
                roomType.setAmenities(Arrays.asList(amenities.split(",")));
            }

            System.out.print("Enter new room type availability (true/false) > ");
            boolean availability = sc.nextBoolean();
            roomType.setAvailable(availability);

            System.out.print("Enter new next higher room type (or leave blank if none)> ");
            String nextHigherRoomType = sc.nextLine().trim();
            if (!nextHigherRoomType.isEmpty()) {
                roomType.setNextHigherRoomType(nextHigherRoomType);
            }

            operationManagerSessionBean.updateRoomType(roomType);
            System.out.println("Room Type updated successfully.");

        } catch (RoomTypeNotFoundException ex) {
            System.out.println("Room Type not found: " + ex.getMessage());
        }
    }

    private void deleteRoomType() {
        System.out.println("*** Delete Room Type ***");
        System.out.print("Enter room type name> ");
        String name = sc.nextLine().trim();

        try {
            boolean wasDeleted = operationManagerSessionBean.deleteRoomType(name);
            if (wasDeleted) {
                System.out.println("Room Type deleted successfully.");
            } else {
                System.out.println("Room Type is in use and has been marked as disabled.");
            }
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("Room Type not found: " + ex.getMessage());
        }
    }

    private void viewAllRoomTypes() {
        System.out.println("*** View All Room Types ***");
        List<RoomType> roomTypes = operationManagerSessionBean.retrieveListOfRoomTypes();
        roomTypes.forEach(roomType -> System.out.println("Room Type Name: " + roomType.getName()));
    }

    //private void viewAllRoomTypes() {
    //    System.out.println("*** View All Room Types ***");
    //    List<RoomType> roomTypes = operationManagerSessionBean.retrieveListOfRoomTypes();
    //    roomTypes.forEach(System.out::println);
    //}
    private void createNewRoom() {
        System.out.println("*** Create New Room ***");
        System.out.print("Enter room number> ");
        String roomNumber = sc.nextLine().trim();
        System.out.print("Enter room type name> ");
        String roomTypeName = sc.nextLine().trim();

        RoomType roomType;
        try {
            roomType = operationManagerSessionBean.viewRoomTypeDetails(roomTypeName);
            System.out.println("Selected Room Type: " + roomType.getName());
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("Room type not found: " + ex.getMessage());
            return;
        }

        System.out.print("Is room available? (true/false)> ");
        boolean isAvailable = sc.nextBoolean();
        sc.nextLine(); // Consume newline

        try {
            Room newRoom = operationManagerSessionBean.createRoom(roomNumber, isAvailable, roomType.getRoomTypeId());
            System.out.println("Room created successfully with ID: " + newRoom.getRoomId());
        } catch (RoomTypeIsDisabledException e) {
            System.out.println("Failed to create room: " + e.getMessage());
        } catch (Exception ex) {
            System.out.println("Failed to create room: " + ex.getMessage());
        }
    }

    private void updateRoom() {
        System.out.println("*** Update Room ***");
        System.out.print("Enter room number> ");
        String roomNumber = sc.nextLine().trim();
        System.out.print("Enter new room type name> ");
        String roomTypeName = sc.nextLine().trim();

        RoomType roomType;
        try {
            // Retrieve the RoomType based on the room type name
            roomType = operationManagerSessionBean.viewRoomTypeDetails(roomTypeName);
            System.out.println("Selected Room Type: " + roomType.getName());
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("Room type not found: " + ex.getMessage());
            return;
        }

        System.out.print("Is room available? (true/false)> ");
        boolean isAvailable = sc.nextBoolean();
        sc.nextLine(); // Consume newline

        try {
            // Retrieve the Room by room number and update its details
            Room roomToUpdate = operationManagerSessionBean.findRoomByNumber(roomNumber);
            Room updatedRoom = operationManagerSessionBean.updateRoom(roomToUpdate.getRoomId(), roomNumber, isAvailable, roomType.getRoomTypeId());

            System.out.println("Room updated successfully: " + updatedRoom.getRoomNumber() + " (" + updatedRoom.getRoomType().getName() + ")");
        } catch (RoomNotFoundException ex) {
            System.out.println("Room not found: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Failed to update room: " + ex.getMessage());
        }
    }

    private void deleteRoom() {
        System.out.println("*** Delete Room ***");
        System.out.print("Enter room number to delete> ");
        String roomNumber = sc.nextLine().trim();

        try {
            Room room = operationManagerSessionBean.findRoomByNumber(roomNumber); // Find the room by room number
            boolean wasDeleted = operationManagerSessionBean.deleteRoom(room.getRoomId()); // Delete or mark as unavailable based on allocation status

            if (wasDeleted) {
                System.out.println("Room with number " + roomNumber + " has been processed for deletion");
            } else {
                System.out.println("Room with number " + roomNumber + " has marked unavailable as its allocated currently");
            }

        } catch (RoomNotFoundException ex) {
            System.out.println("Room not found: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Failed to delete room: " + ex.getMessage());
        }
    }

    private void viewAllRooms() {
        System.out.println("*** View All Rooms ***");
        List<Room> rooms = operationManagerSessionBean.retrieveAllRooms();

        System.out.printf("%-15s%-20s%-10s%-15s\n", "Room Number", "Room Type", "Available", "Allocated");
        System.out.println("-------------------------------------------------------------");

        rooms.forEach(room -> {
            String roomNumber = room.getRoomNumber();
            String roomType = room.getRoomType().getName();
            boolean isAvailable = room.getIsAvailable();
            boolean isAllocated = room.getIsAllocated();

            System.out.printf("%-15s%-20s%-10s%-15s\n", roomNumber, roomType, isAvailable, isAllocated);
        });
    }

    private void viewRoomAllocationExceptionReport() {
        System.out.println("*** View Room Allocation Exception Report ***");

        List<ExceptionAllocationReport> reports = operationManagerSessionBean.retrieveRoomAllocationExceptionReports();

        if (reports.isEmpty()) {
            System.out.println("No room allocation exceptions found.");
        } else {
            System.out.printf("%-25s%-30s%-20s%-20s\n", "Exception Date", "Exception Type", "Room Type Requested", "Reservation Room");
            System.out.println("---------------------------------------------------------------------------------------------");

            reports.forEach(report -> {
                String exceptionDate = (report.getGeneratedAt() != null) ? report.getGeneratedAt().toString() : "N/A";
                String exceptionType = (report.getExceptionType() != null) ? report.getExceptionType().name() : "N/A";
                String roomTypeRequested = (report.getRoomTypeRequested() != null) ? report.getRoomTypeRequested() : "N/A";
                String reservationRoom = (report.getReservationRoom() != null && report.getReservationRoom().getRoom() != null)
                        ? report.getReservationRoom().getRoom().getRoomNumber()
                        : "N/A";

                System.out.printf("%-25s%-30s%-20s%-20s\n", exceptionDate, exceptionType, roomTypeRequested, reservationRoom);
            });
        }
    }

    private void createNewRoomRate() {
        System.out.println("*** Create New Room Rate ***");

        System.out.print("Enter room rate name> ");
        String name = sc.nextLine().trim();

        System.out.print("Enter room type name> ");
        String roomTypeName = sc.nextLine().trim();

        RoomType roomType;
        try {
            roomType = operationManagerSessionBean.findRoomTypeByName(roomTypeName);
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("RoomType not found: " + ex.getMessage());
            return; // Exit if room type is not found
        }

        System.out.print("Enter rate type (PUBLISHED, NORMAL, PEAK, PROMOTION, DISABLED)> ");
        String rateTypeInput = sc.nextLine().trim();
        RoomRateTypeEnum rateType;
        try {
            rateType = RoomRateTypeEnum.valueOf(rateTypeInput.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid rate type. Please try again.");
            return;
        }

        System.out.print("Enter rate per night> ");
        String rateInput = sc.nextLine().trim();
        BigDecimal ratePerNight;
        try {
            ratePerNight = new BigDecimal(rateInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid rate. Please enter a valid decimal number.");
            return;
        }

        System.out.print("Enter start date (yyyy-MM-dd) or leave blank for no start date> ");
        String startDateInput = sc.nextLine().trim();
        Date startDate = null;
        if (!startDateInput.isEmpty()) {
            try {
                startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateInput);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please enter date as yyyy-MM-dd.");
                return;
            }
        }

        System.out.print("Enter end date (yyyy-MM-dd) or leave blank for no end date> ");
        String endDateInput = sc.nextLine().trim();
        Date endDate = null;
        if (!endDateInput.isEmpty()) {
            try {
                endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateInput);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please enter date as yyyy-MM-dd.");
                return;
            }
        }

        try {
            RoomRate newRoomRate = salesManagerSessionBean.createRoomRate(name, rateType, ratePerNight, startDate, endDate, roomType.getRoomTypeId());
            System.out.println("Room Rate created successfully with ID: " + newRoomRate.getRoomRateId());
        } catch (Exception ex) {
            System.out.println("Failed to create room rate: " + ex.getMessage());
        }
    }

    private void viewRoomRateDetails() {
        System.out.println("*** View Room Rate Details ***");
        System.out.print("Enter room rate name> ");
        String name = sc.nextLine().trim();

        try {
            RoomRate roomRate = salesManagerSessionBean.findRoomRateByName(name);
            System.out.println("Room Rate Details:");
            System.out.println(roomRate);  // Calls the toString() method
        } catch (RoomRateNotFoundException ex) {
            System.out.println("Room Rate not found: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Failed to retrieve room rate details: " + ex.getMessage());
        }
    }

    private void updateRoomRate() {
        System.out.println("*** Update Room Rate ***");

        System.out.print("Enter room rate name> ");
        String rateName = sc.nextLine().trim();

        try {
            RoomRate existingRoomRate = salesManagerSessionBean.findRoomRateByName(rateName);
            System.out.println("Current Room Rate Details:");
            System.out.println(existingRoomRate);

            System.out.print("Enter new name (or press Enter to keep current: " + existingRoomRate.getName() + ")> ");
            String newName = sc.nextLine().trim();
            if (newName.isEmpty()) {
                newName = existingRoomRate.getName();
            }

            System.out.print("Enter rate type (PUBLISHED, NORMAL, PEAK, PROMOTION, DISABLED) or press Enter to keep current> ");
            String rateTypeInput = sc.nextLine().trim();
            RoomRateTypeEnum rateType = rateTypeInput.isEmpty() ? existingRoomRate.getRateType()
                    : RoomRateTypeEnum.valueOf(rateTypeInput.toUpperCase());

            System.out.print("Enter rate per night (or press Enter to keep current: " + existingRoomRate.getRatePerNight() + ")> ");
            String ratePerNightInput = sc.nextLine().trim();
            BigDecimal ratePerNight = ratePerNightInput.isEmpty() ? existingRoomRate.getRatePerNight() : new BigDecimal(ratePerNightInput);

            System.out.print("Enter start date (yyyy-MM-dd) or press Enter to keep current> ");
            String startDateInput = sc.nextLine().trim();
            Date startDate = startDateInput.isEmpty() ? existingRoomRate.getStartDate()
                    : new SimpleDateFormat("yyyy-MM-dd").parse(startDateInput);

            System.out.print("Enter end date (yyyy-MM-dd) or press Enter to keep current> ");
            String endDateInput = sc.nextLine().trim();
            Date endDate = endDateInput.isEmpty() ? existingRoomRate.getEndDate()
                    : new SimpleDateFormat("yyyy-MM-dd").parse(endDateInput);

            System.out.print("Enter room type name (or press Enter to keep current: " + existingRoomRate.getRoomType().getName() + ")> ");
            String roomTypeName = sc.nextLine().trim();
            if (roomTypeName.isEmpty()) {
                roomTypeName = existingRoomRate.getRoomType().getName();
            }

            RoomRate updatedRoomRate = salesManagerSessionBean.updateRoomRate(rateName, newName, rateType, ratePerNight, startDate, endDate, roomTypeName);
            System.out.println("Room Rate updated successfully. Updated details:");
            System.out.println(updatedRoomRate);

        } catch (RoomRateNotFoundException | RoomTypeNotFoundException ex) {
            System.out.println("Error updating room rate: " + ex.getMessage());
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please enter dates in yyyy-MM-dd format.");
        } catch (Exception ex) {
            System.out.println("An error occurred while updating room rate: " + ex.getMessage());
        }
    }

    private void deleteRoomRate() {
        System.out.println("*** Delete Room Rate ***");
        System.out.print("Enter room rate name> ");
        String rateName = sc.nextLine().trim();

        try {
            boolean isDeleted = salesManagerSessionBean.deleteRoomRate(rateName);

            if (isDeleted) {
                System.out.println("Room rate '" + rateName + "' deleted successfully.");
            } else {
                System.out.println("Room rate '" + rateName + "' is in use and has been disabled.");
            }
        } catch (RoomRateNotFoundException ex) {
            System.out.println("Room rate not found: " + ex.getMessage());
        }
    }

    private void viewAllRoomRates() {
        System.out.println("*** View All Room Rates ***");
        List<RoomRate> roomRates = salesManagerSessionBean.retrieveAllRoomRates();

        if (roomRates.isEmpty()) {
            System.out.println("No room rates found.");
        } else {
            System.out.printf("%-30s%-20s\n", "Room Rate Name", "Rate Per Night");
            System.out.println("--------------------------------------------------");

            roomRates.forEach(roomRate -> {
                System.out.printf("%-30s%-20s\n", roomRate.getName(), roomRate.getRatePerNight());
            });
        }
    }

    private void allocateRoomManually() {
        System.out.println("*** Manual Room Allocation ***");

        try {
            Date allocationDate = getValidDate("Enter date for room allocation");
            List<ExceptionAllocationReport> reports = roomAllocationSessionBean.allocateRoomsForDate(allocationDate);

            System.out.println("Room allocation completed for " + new SimpleDateFormat("yyyy-MM-dd").format(allocationDate));

            if (!reports.isEmpty()) {
                System.out.println("Exceptions occurred during allocation:");
                reports.forEach(report -> {
                    System.out.printf("Exception Type: %s, Room Type Requested: %s\n",
                            report.getExceptionType(),
                            report.getRoomTypeRequested());
                });
            } else {
                System.out.println("No allocation exceptions occurred.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while allocating rooms: " + e.getMessage());
        }
    }

    private void walkInSearchRoom() {
        System.out.println("*** Walk-in Search Room ***");

        try {
            System.out.print("Enter check-in date (yyyy-MM-dd)> ");
            String checkInDateInput = sc.nextLine().trim();
            Date checkInDate = new SimpleDateFormat("yyyy-MM-dd").parse(checkInDateInput);

            System.out.print("Enter check-out date (yyyy-MM-dd)> ");
            String checkOutDateInput = sc.nextLine().trim();
            Date checkOutDate = new SimpleDateFormat("yyyy-MM-dd").parse(checkOutDateInput);

            System.out.print("Enter number of rooms required> ");
            int numberOfRooms = sc.nextInt();
            sc.nextLine(); // consume newline

            List<RoomType> availableRoomTypes = guestRelationOfficerSessionBean.searchAvailableRooms(checkInDate, checkOutDate, numberOfRooms);

            if (availableRoomTypes.isEmpty()) {
                System.out.println("No available rooms found for the specified dates.");
            } else {
                System.out.printf("%-20s%-15s\n", "Room Type", "Total Cost");
                System.out.println("---------------------------------------");

                for (RoomType roomType : availableRoomTypes) {
                    try{
                     BigDecimal totalCost = paymentSessionBean.calculatePaymentForManagementClient(roomType.getName(), checkInDate, checkOutDate, numberOfRooms);
                     System.out.printf("%-20s%-15s\n", roomType.getName(), totalCost);
                
                    } catch (RoomRateNotFoundException ex) {
                        System.out.printf("%-20s%-15s\n", roomType.getName(), "NOT AVAILABLE");
                    }
                }
            }
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please enter the date in yyyy-MM-dd format.");
        } catch ( RoomTypeNotFoundException | ReservationUnavailableException ex) {
            System.out.println("An error occurred during room search: " + ex.getMessage());
        }
    }

// Helper method to retrieve the prevailing published rate for a room type
    private void walkInReserveRoom() {
        System.out.println("*** Walk-in Reserve Room ***");

        try {

            System.out.print("Enter Guest's First Name> ");
            String firstName = sc.nextLine();
            System.out.print("Enter Guest's Last Name> ");
            String lastName = sc.nextLine();
            System.out.print("Enter Guest's phone number: ");
            String phoneNumber = sc.nextLine();
            System.out.print("Enter Guest's email: ");
            String email = sc.nextLine();
            Guest guest;
            if (!guestRelationOfficerSessionBean.checkIfGuestExists(email)) {
                guest = guestRelationOfficerSessionBean.createNewGuest(firstName, lastName, phoneNumber, email);
            } else {
                guest = guestRelationOfficerSessionBean.retrieveGuestByEmail(email);
            }

            System.out.print("Enter check-in date (yyyy-MM-dd)> ");
            String checkInDateInput = sc.nextLine().trim();
            Date checkInDate = new SimpleDateFormat("yyyy-MM-dd").parse(checkInDateInput);

            System.out.print("Enter check-out date (yyyy-MM-dd)> ");
            String checkOutDateInput = sc.nextLine().trim();
            Date checkOutDate = new SimpleDateFormat("yyyy-MM-dd").parse(checkOutDateInput);

            System.out.print("Enter number of rooms to reserve> ");
            int numberOfRooms = sc.nextInt();
            sc.nextLine(); // consume newline
            List<RoomType> availableRoomTypes = guestRelationOfficerSessionBean.searchAvailableRooms(checkInDate, checkOutDate, numberOfRooms);

            if (availableRoomTypes.isEmpty()) {
                System.out.println("No available rooms found for the specified dates.");
            } else {
                System.out.printf("%-20s%-15s\n", "Room Type", "Total Cost");
                System.out.println("---------------------------------------");

                for (RoomType roomType : availableRoomTypes) {
                    try{
                     BigDecimal totalCost = paymentSessionBean.calculatePaymentForManagementClient(roomType.getName(), checkInDate, checkOutDate, numberOfRooms);
                     System.out.printf("%-20s%-15s\n", roomType.getName(), totalCost);
                
                    } catch (RoomRateNotFoundException ex) {
                        System.out.printf("%-20s%-15s\n", roomType.getName(), "NOT AVAILABLE");
                    }
                } 
            }

            System.out.print("Enter room type name (from search results)> ");
            String roomTypeName = sc.nextLine().trim();

            BigDecimal totalCost = paymentSessionBean.calculatePaymentForManagementClient(roomTypeName, checkInDate, checkOutDate, numberOfRooms);
            // Reserve the room
            Reservation reservation = guestRelationOfficerSessionBean.walkInReserveRoom(guest.getGuestId(), roomTypeName, checkInDate, checkOutDate, numberOfRooms, totalCost);

            System.out.println("Reservation successful! Reservation ID: " + reservation.getReservationId()
                    + ", Total Amount: " + totalCost);

            // Check for same-day check-in after 2 a.m.
            if (isSameDay(checkInDate, new Date()) && isAfter2AM(new Date())) {
                System.out.println("Allocating rooms immediately as it is a same-day check-in after 2 a.m.");
                roomAllocationSessionBean.allocateRoomsForDate(checkInDate);
            }

        } catch (ParseException e) {
            System.out.println("Invalid date format. Please enter the date in yyyy-MM-dd format.");
        } catch (RoomRateNotFoundException ex) {
            System.out.println("Room type not found: " + ex.getMessage());
        } catch (RoomNotAvailableException ex) {
            System.out.println("Unable to reserve room: " + ex.getMessage());
        } catch (RoomTypeNotFoundException | ReservationUnavailableException ex) {
            System.out.println("An error occurred during room search: " + ex.getMessage());
        }
    }

    private void checkInGuest() {
        System.out.println("*** Check-in Guest ***");
        System.out.print("Enter reservation ID> ");
        Long reservationId = sc.nextLong();
        sc.nextLine(); // consume newline

        try {
            // Attempt to check in the guest using the session bean method
            guestRelationOfficerSessionBean.checkInGuest(reservationId);

            // Confirm successful check-in
            System.out.println("Guest has been checked in successfully.");
        } catch (ReservationNotFoundException ex) {
            System.out.println("Reservation not found: " + ex.getMessage());
        } catch (GuestCheckInException ex) {
            System.out.println("Error during check-in: " + ex.getMessage());

            // Handle room allocation exception
            if (ex.getMessage().contains("Manual handling required")) {
                System.out.println("Room allocation exception detected. Please review and manually allocate rooms.");

            }
        }
    }

    private void checkOutGuest() {
        System.out.println("*** Check-Out Guest ***");
        System.out.print("Enter room number> ");
        String roomNumber = sc.nextLine().trim();

        try {
            guestRelationOfficerSessionBean.checkOutGuestByRoomNumber(roomNumber);
            System.out.println("Guest successfully checked out from room " + roomNumber + ".");
        } catch (ReservationNotFoundException | GuestCheckOutException ex) {
            System.out.println("Error during check-out: " + ex.getMessage());
        }
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean isAfter2AM(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour >= 2;
    }

    private double getValidDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return sc.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                sc.nextLine(); // Clear the invalid input
            }
        }
    }

    private int getValidInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                sc.nextLine(); // Clear the invalid input
            }
        }
    }

    private RoomTypeEnum getValidRoomTypeEnum() {
        while (true) {
            try {
                System.out.print("Enter room category (DELUXE, PREMIER, FAMILY, JUNIOR, GRAND)> ");
                return RoomTypeEnum.valueOf(sc.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid category. Please enter a valid room category.");
            }
        }
    }

    private boolean getValidBoolean(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim().toLowerCase();
            if (input.equals("true") || input.equals("false")) {
                return Boolean.parseBoolean(input);
            } else {
                System.out.println("Invalid input. Please enter 'true' or 'false'.");
            }
        }
    }

    private Date getValidDate(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + " (yyyy-MM-dd)> ");
                String input = sc.nextLine().trim();
                return new SimpleDateFormat("yyyy-MM-dd").parse(input);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please enter the date in yyyy-MM-dd format.");
            }
        }
    }

    private String getValidEmail(String prompt) {
        while (true) {
            System.out.print(prompt);
            String email = sc.nextLine().trim();
            if (email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return email;
            } else {
                System.out.println("Invalid email format. Please try again.");
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Main app = new Main();
        app.runApp();
    }

}
