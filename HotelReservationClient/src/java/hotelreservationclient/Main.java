/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package hotelreservationclient;

import HelperClass.RoomTypeAvailability;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.PaymentSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomAllocationSessionBeanRemote;
import entity.Customer;
import entity.Reservation;
import entity.RoomType;
import exception.CustomerAlreadyLoggedInException;
import exception.CustomerExistException;
import exception.CustomerNotFoundException;
import exception.CustomerNotLoggedInException;
import exception.CustomerWrongCredentialsException;
import exception.InputNotValidException;
import exception.ReservationNotFoundException;
import exception.ReservationUnavailableException;
import exception.RoomRateNotFoundException;
import exception.RoomTypeNotFoundException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;

/**
 *
 * @author ziyuanchong
 */
public class Main {

    @EJB
    private static RoomAllocationSessionBeanRemote roomAllocationSessionBean;

    @EJB
    private static PaymentSessionBeanRemote paymentSessionBean;

    @EJB
    private static ReservationSessionBeanRemote reservationSessionBean;

    @EJB
    private static CustomerSessionBeanRemote customerSessionBean;

    public static void main(String[] args) throws InputNotValidException {
        Scanner sc = new Scanner(System.in);
        Integer response = 1;
        while (response >= 1 && response <= 3) {
            System.out.println("*** Welcome to Merlion Hotel's HoRs Reservation Client ***\n");
            System.out.println("1: Login");
            System.out.println("2: Register");
            System.out.println("3: Search For Available Hotel Rooms");
            System.out.println("4: Exit");
            System.out.println("Enter the integer of your intended action: ");
            response = sc.nextInt();
            sc.nextLine();
            if (response == 1) {
                System.out.println("Enter email: ");
                String email = sc.nextLine();
                System.out.println("Enter password: ");
                String password = sc.nextLine();
                System.out.println("\n");
                try {
                    Customer customer = login(email, password);
                    System.out.println("Welcome Back " + customer.getFirstName());
                    int action = 1;
                    while (action <= 4 && action >= 1) {
                        System.out.println("1: Search Hotel Room");
                        System.out.println("2: Reserve Hotel Room");
                        System.out.println("3: View My Reservation Details");
                        System.out.println("4: View All My Reservations");
                        System.out.println("5: Log Out");
                        System.out.println("Please key in the integer of your intended action: ");
                        action = sc.nextInt();
                        sc.nextLine();
                        if (action == 1) {
                            try {
                                System.out.println("Enter check-in date in yyyy-MM-dd: ");
                                String checkInDateInput = sc.nextLine();
                                Date checkInDate = new SimpleDateFormat("yyyy-MM-dd").parse(checkInDateInput);
                                System.out.println("Enter check-out date in yyyy-MM-dd: ");
                                String checkOutDateInput = sc.nextLine();
                                Date checkOutDate = new SimpleDateFormat("yyyy-MM-dd").parse(checkOutDateInput);
                                List<RoomTypeAvailability> availableRoomTypes = reservationSessionBean.retrieveRoomTypeAvailability(checkInDate, checkOutDate);
                                if (availableRoomTypes.isEmpty()) {
                                    System.out.println("No available rooms found for the specified dates.");
                                } else {
                                    System.out.printf("%-15s %-15s %-25s\n", "Room Type", "Rate Per Room", "Number Of Available Rooms");
                                    System.out.println("-------------------------------------------------------------------------------------");

                                    for (RoomTypeAvailability rta : availableRoomTypes) {
                                        try {
                                            RoomType roomType = rta.getRoomType();
                                            int numberOfAvailableRooms = rta.getAvailableRooms();
                                            BigDecimal totalCost = paymentSessionBean.calculatePaymentForReservationClient(roomType.getName(), checkInDate, checkOutDate, 0);
                                            System.out.printf("%-20s%-15s%-25s\n", roomType.getName(), totalCost, rta.getAvailableRooms());
                                        } catch (RoomRateNotFoundException ex) {
                                        }
                                    }
                                }

                            } catch (ParseException ex) {
                                throw new InputNotValidException("The dates you have entered are in the wrong format, please try again");
                            } catch (RoomTypeNotFoundException ex2) {
                                System.out.println("An error has occurred while fetching the rooms: " + ex2.getMessage() + "!\n");
                            }
                        } else if (action == 2) {
                            try {
                                System.out.println("Enter check-in date in yyyy-MM-dd: ");
                                String checkInDateInput = sc.nextLine();
                                Date checkInDate = new SimpleDateFormat("yyyy-MM-dd").parse(checkInDateInput);
                                System.out.println("Enter check-out date in yyyy-MM-dd: ");
                                String checkOutDateInput = sc.nextLine();
                                Date checkOutDate = new SimpleDateFormat("yyyy-MM-dd").parse(checkOutDateInput);
                                try {
                                    Reservation reservation = makeReservation(customer.getGuestId(), checkInDate, checkOutDate, 0);
                                    System.out.println("Reservation successfully created");
                                } catch (ReservationUnavailableException ex) {
                                    System.out.println("An error has occurred while creating the Reservation: " + ex.getMessage() + "!\n");
                                } catch (RoomTypeNotFoundException ex2) {
                                    System.out.println("An error has occurred while creating the Reservation: " + ex2.getMessage() + "!\n");
                                } catch (RoomRateNotFoundException ex3) {
                                    System.out.println("An error has occurred while creating the Reservation: " + ex3.getMessage() + "!\n");
                                }
                            } catch (ParseException ex) {
                                System.out.println("The dates you have entered are in the wrong format, please try again");
                            }
                        } else if (action == 3) {
                            System.out.println("Enter the reservationId of the reservation details you want to view: ");
                            Long reservationId = Long.parseLong(sc.nextLine());
                            try {
                                Reservation r = customerSessionBean.retrieveReservationDetails(reservationId, customer.getGuestId());
                                System.out.println("*** View Reservation: ***\n");
                                System.out.printf("%-15s%-20s%-20s%-20s%-20s\n", "Reservation ID", "Checkin Date", "Checkout Date", "Number Of Rooms", "Total Amount Paid");
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                String startDate = dateFormat.format(r.getStartDate());
                                String endDate = dateFormat.format(r.getEndDate());
                                System.out.printf("%-15s%-20s%-20s%-20d%-20.2f\n", r.getReservationId(), startDate, endDate, r.getNumberOfRooms(), r.getTotalAmount());
                            } catch (ReservationNotFoundException ex) {
                                System.out.println("Error fetching reservation details: " + ex.getMessage());
                            }
                        } else if (action == 4) {
                            List<Reservation> listOfReservations = customerSessionBean.retrieveListOfReservationByCustomerId(customer.getGuestId());
                            if (listOfReservations.isEmpty()) {
                                System.out.println("This customer has not made any reservations");
                            } else {
                                System.out.println("*** View List Of Reservations: ***\n");
                                System.out.printf("%-15s%-20s\n", "Reservation ID", "Room Type Name"); // Adjusted header
                                for (Reservation r : listOfReservations) {
                                    System.out.printf("%-15s%-20s\n", r.getReservationId(), r.getRoomType().getName());
                                }
                            }
                        } else if (action == 5) {
                            customerSessionBean.logout(customer.getGuestId());
                        }
                    }
                    System.out.println("\n");
                } catch (CustomerNotLoggedInException e2) {
                    System.out.println(e2.getMessage());
                } catch (CustomerNotFoundException e3) {
                    System.out.println(e3.getMessage());
                } catch (CustomerAlreadyLoggedInException e4) {
                    System.out.println(e4.getMessage());
                } catch (CustomerWrongCredentialsException e5) {
                    System.out.println(e5.getMessage());
                }
            } else if (response == 2) {
                System.out.println("Enter your First Name: ");
                String firstName = sc.nextLine();
                System.out.println("Enter your Last Name: ");
                String lastName = sc.nextLine();
                System.out.println("Enter your phone number: ");
                String phoneNumber = sc.nextLine();
                System.out.println("Enter your email: ");
                String email = sc.nextLine();
                System.out.println("Enter your password: ");
                String password = sc.nextLine();
                Customer customer = new Customer(firstName, lastName, phoneNumber, email, false, password);
                try {
                    customerSessionBean.createNewCustomer(customer);
                    System.out.println("Customer successfully created");
                } catch (CustomerExistException ex) {
                    System.out.println("An Error occurred while creating customer: " + ex.getMessage());
                }
            } else if (response == 3) {
                try {
                    System.out.println("Enter check-in date in yyyy-MM-dd: ");
                    String checkInDateInput = sc.nextLine();
                    Date checkInDate = new SimpleDateFormat("yyyy-MM-dd").parse(checkInDateInput);
                    System.out.println("Enter check-out date in yyyy-MM-dd: ");
                    String checkOutDateInput = sc.nextLine();
                    Date checkOutDate = new SimpleDateFormat("yyyy-MM-dd").parse(checkOutDateInput);
                    List<RoomTypeAvailability> availableRoomTypes = reservationSessionBean.retrieveRoomTypeAvailability(checkInDate, checkOutDate);
                    if (availableRoomTypes.isEmpty()) {
                        System.out.println("No available rooms found for the specified dates.");
                    } else {
                        System.out.printf("%-15s %-15s %-25s\n", "Room Type", "Rate Per Room", "Number Of Available Rooms");
                        System.out.println("-------------------------------------------------------------------------------------");

                        for (RoomTypeAvailability rta : availableRoomTypes) {
                            try {
                                RoomType roomType = rta.getRoomType();
                                int numberOfAvailableRooms = rta.getAvailableRooms();
                                BigDecimal totalCost = paymentSessionBean.calculatePaymentForReservationClient(roomType.getName(), checkInDate, checkOutDate, 0);
                                System.out.printf("%-20s%-15s%-25s\n", roomType.getName(), totalCost, rta.getAvailableRooms());
                            } catch (RoomRateNotFoundException ex) {
                            }
                        }
                    }
                } catch (ParseException ex) {
                    throw new InputNotValidException("The dates you have entered are in the wrong format, please try again");
                } catch (RoomTypeNotFoundException ex) {
                    System.out.println("An error has occurred while fetching the rooms: " + ex.getMessage() + "!\n");
                }
            }
        }
    }

    private static Customer login(String email, String password) throws CustomerWrongCredentialsException, CustomerAlreadyLoggedInException {
        return customerSessionBean.login(email, password);
    }

    private static Reservation makeReservation(Long customerId, Date startDate, Date endDate, int numberOfRooms) throws ReservationUnavailableException, RoomTypeNotFoundException, RoomRateNotFoundException {

        Scanner sc = new Scanner(System.in);
        List<RoomTypeAvailability> availableRoomTypes = reservationSessionBean.retrieveRoomTypeAvailability(startDate, endDate);
        if (availableRoomTypes.isEmpty()) {
            System.out.println("No available rooms found for the specified dates.");
        } else {
            System.out.printf("%-15s %-15s %-25s\n", "Room Type", "Rate Per Room", "Number Of Available Rooms");
            System.out.println("-------------------------------------------------------------------------------------");

            for (RoomTypeAvailability rta : availableRoomTypes) {
                try {
                    RoomType roomType = rta.getRoomType();
                    int numberOfAvailableRooms = rta.getAvailableRooms();
                    BigDecimal totalCost = paymentSessionBean.calculatePaymentForReservationClient(roomType.getName(), startDate, endDate, numberOfRooms);
                    System.out.printf("%-20s%-15s-%25s\n", roomType.getName(), totalCost, rta.getAvailableRooms());
                } catch (RoomRateNotFoundException ex) {
                }
            }
        }
        System.out.println("Enter the number of rooms required: ");
        int number = sc.nextInt();
        sc.nextLine();
        System.out.println("Please input the name of the RoomType that you have chosen");
        String chosenRoomType = sc.nextLine();
        Reservation newReservation = reservationSessionBean.createNewReservation(customerId, chosenRoomType, startDate, endDate, number,
                paymentSessionBean.calculatePaymentForReservationClient(chosenRoomType, startDate, endDate, 0).multiply(BigDecimal.valueOf(number))); //idk how to store the chosen amount, so ill jus recalculate the chosen one
        if (isSameDay(startDate, new Date()) && isAfter2AM(new Date())) {
            System.out.println("Allocating rooms immediately as it is a same-day check-in after 2 a.m.");
            roomAllocationSessionBean.allocateRoomsForDate(startDate);
        }
        return newReservation;
    }

    private static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private static boolean isAfter2AM(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour >= 2;
    }
}
