/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package hotelreservationsystemclient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import ws.holiday.HolidayWebService_Service;
import ws.holiday.InvalidPartnerInfoException_Exception;
import ws.holiday.Partner;
import ws.holiday.Reservation;
import ws.holiday.ReservationUnavailableException_Exception;
import ws.holiday.RoomType;
import ws.holiday.RoomTypeNotFoundException_Exception;

/**
 *
 * @author ziyuanchong
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    // TODO code application logic here
    private static Partner currentPartner;
    private static HolidayWebService_Service service = new HolidayWebService_Service();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }

    private void run() {
        while (true) {
            if (currentPartner == null) {
                System.out.println("1: Login");
                System.out.println("2: Exit");
                System.out.print("Enter option> ");
                int option = sc.nextInt();
                sc.nextLine();  // consume newline

                if (option == 1) {
                    login();
                } else if (option == 2) {
                    System.out.println("Exiting...");
                    break;
                } else {
                    System.out.println("Invalid option. Please try again.");
                }
            } else {
                showMainMenu();
            }
        }
    }

    private void login() {
        System.out.print("Enter username> ");
        String username = sc.nextLine().trim();
        System.out.print("Enter password> ");
        String password = sc.nextLine().trim();

        try {
            currentPartner = service.getHolidayWebServicePort().loginPartner(username, password);
            System.out.println("Login successful as partner ID: " + currentPartner.getPartnerId());
        } catch (InvalidPartnerInfoException_Exception e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void showMainMenu() {
        System.out.println("\n*** Partner Menu ***");
        System.out.println("1: Search Available Room Types");
        System.out.println("2: Reserve Room");
        System.out.println("3: View Partner Reservation");
        System.out.println("4: View All Partner Reservations");
        System.out.println("5: Logout");

        System.out.print("Enter option> ");
        int option = sc.nextInt();
        sc.nextLine(); // consume newline

        switch (option) {
            case 1:
                searchAvailableRoomTypes();
                break;
            case 2:
                reserveRoom();
                break;
            case 3:
                viewPartnerReservation();
                break;
            case 4:
                viewAllPartnerReservations();
                break;
            case 5:
                currentPartner = null;
                System.out.println("Logged out successfully.");
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    private void searchAvailableRoomTypes() {
        try {
            System.out.print("Enter check-in date (yyyy-MM-dd)> ");
            String checkInDateStr = sc.nextLine();
            System.out.print("Enter check-out date (yyyy-MM-dd)> ");
            String checkOutDateStr = sc.nextLine();
            System.out.print("Enter number of rooms required> ");
            int numberOfRooms = sc.nextInt();
            sc.nextLine(); // consume newline

            List<RoomType> availableRoomTypes = service.getHolidayWebServicePort()
                    .searchAvailableRoomTypes(checkInDateStr, checkOutDateStr, numberOfRooms);

            if (availableRoomTypes.isEmpty()) {
                System.out.println("No available rooms found.");
            } else {
                System.out.println("Available Room Types:");
                for (RoomType roomType : availableRoomTypes) {
                    BigDecimal totalCost = service.getHolidayWebServicePort().calculateTotalCost(
                            roomType.getName(), checkInDateStr, checkOutDateStr, numberOfRooms);

                    System.out.println("Room Type: " + roomType.getName() + ", Total Cost: " + totalCost);
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }

    private void reserveRoom() {
        try {
            System.out.print("Enter room type name> ");
            String roomTypeName = sc.nextLine();
            System.out.print("Enter check-in date (yyyy-MM-dd)> ");
            String checkInDateStr = sc.nextLine();
            System.out.print("Enter check-out date (yyyy-MM-dd)> ");
            String checkOutDateStr = sc.nextLine();
            System.out.print("Enter number of rooms to reserve> ");
            int numberOfRooms = sc.nextInt();
            sc.nextLine(); // consume newline

            Reservation reservation = service.getHolidayWebServicePort()
                    .reserveRoom(currentPartner.getPartnerId(), roomTypeName, checkInDateStr, checkOutDateStr, numberOfRooms);

            System.out.println("Reservation successful. Reservation ID: " + reservation.getReservationId());
        } catch (RoomTypeNotFoundException_Exception | ReservationUnavailableException_Exception | ws.holiday.PartnerNotFoundException_Exception e) {
            System.out.println("Reservation failed: " + e.getMessage());
        }
    }

    private void viewPartnerReservation() {
        try {
            System.out.print("Enter reservation ID> ");
            long reservationId = sc.nextLong();
            sc.nextLine(); // consume newline

            Reservation reservation = service.getHolidayWebServicePort().viewPartnerReservation(reservationId);
            System.out.println("Reservation Details:");
            System.out.println("Reservation ID: " + reservation.getReservationId());
            System.out.println("Room Type: " + reservation.getRoomType().getName());
            System.out.println("Check-in Date: " + reservation.getStartDate());
            System.out.println("Check-out Date: " + reservation.getEndDate());
        } catch (ws.holiday.ReservationNotFoundException_Exception e) {
            System.out.println("Failed to retrieve reservation: " + e.getMessage());
        }
    }

    private void viewAllPartnerReservations() {
        List<Reservation> reservations = service.getHolidayWebServicePort().viewAllPartnerReservations(currentPartner.getUsername());
        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
        } else {
            reservations.forEach(reservation -> {
                System.out.println("Reservation ID: " + reservation.getReservationId() + ", Room Type: " + reservation.getRoomType().getName());
            });
        }
    }

}
