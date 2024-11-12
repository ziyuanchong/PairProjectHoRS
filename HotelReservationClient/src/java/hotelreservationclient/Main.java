/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package hotelreservationclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.PaymentSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import entity.Customer;
import entity.Reservation;
import entity.RoomType;
import exception.CustomerExistException;
import exception.CustomerWrongCredentialsException;
import exception.InputNotValidException;
import exception.ReservationNotFoundException;
import exception.ReservationUnavailableException;
import exception.RoomRateNotFoundException;
import exception.RoomTypeNotFoundException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private static PaymentSessionBeanRemote paymentSessionBean;

    @EJB
    private static ReservationSessionBeanRemote reservationSessionBean;

    @EJB
    private static CustomerSessionBeanRemote customerSessionBean;

    
    public static void main(String[] args) throws CustomerWrongCredentialsException, InputNotValidException, CustomerExistException {
        Scanner sc = new Scanner(System.in);
        Integer response = 1;
        System.out.println("*** Welcome to Merlion Hotel's HoRs Reservation Client ***\n");
        System.out.println("1: Login");
        System.out.println("2: Register");
        System.out.println("3: Search For Available Hotel Rooms");
        while (response >= 1 && response <= 3) {
            System.out.println("Enter the integer of your intended action: ");
            response = sc.nextInt();
            sc.nextLine();
            if (response == 1) {
                System.out.println("Enter email: ");
                String email = sc.nextLine();
                System.out.println("Enter password: ");
                String password = sc.nextLine();
                try {
                    Customer customer = login(email, password);
                    System.out.println("Welcome Back " + customer.getFirstName());
                    System.out.println("1: Reserve Hotel Room");
                    System.out.println("2: View My Reservation Details");
                    System.out.println("3: View All My Reservations");
                    int action = 4;
                    while (action <= 3 && action >= 1) {
                        System.out.println("Please Key in the integer of your intended action: ");
                        action = sc.nextInt();
                        sc.nextLine();
                        if (action == 1) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            dateFormat.setLenient(false); // Disable lenient parsing to ensure strict format
                            try {
                                System.out.println("Enter check-in date in MM/dd/yyyy: ");
                                Date checkInDate = dateFormat.parse(sc.nextLine());
                                System.out.println("Enter check-out date in MM/dd/yyyy: ");
                                Date checkOutDate = dateFormat.parse(sc.nextLine());
                                System.out.println("Enter the number of rooms required: ");
                                int numberOfRooms = sc.nextInt();
                                sc.nextLine();
                                try {
                                    Reservation reservation = makeReservation(customer.getGuestId(), checkInDate, checkOutDate, numberOfRooms);
                                    System.out.println("Reservation successfully created");
                                } catch (ReservationUnavailableException ex) {
                                    System.out.println("An error has occurred while creating the Reservation: " + ex.getMessage() + "!\n");
                                } catch (RoomTypeNotFoundException ex2) {
                                    System.out.println("An error has occurred while creating the Reservation: " + ex2.getMessage() + "!\n");
                                } catch (RoomRateNotFoundException ex3) {
                                    System.out.println("An error has occurred while creating the Reservation: " + ex3.getMessage() + "!\n");
                                }
                            } catch (ParseException ex) {
                                throw new InputNotValidException("The dates you have entered are in the wrong format, please try again");
                            }
                        } else if (action == 2) {
                            System.out.println("Enter your the reservationId of the reservation details you want to view: ");
                            Long reservationId = Long.parseLong(sc.nextLine());
                            try {
                                Reservation r = customerSessionBean.retrieveReservationDetails(reservationId, customer.getGuestId());
                                System.out.printf("%8s%20s%20s%15s%20s%20s\n", "Reservation ID", "Checkin Date", "CheckOut Name", "Number Of Rooms", "Total Amount Paid");
                                System.out.printf("%8s%20s%20s%15s%20s%20s\n", r.getReservationId(), r.getStartDate(), r.getEndDate(), r.getNumberOfRooms(), r.getTotalAmount());
                            } catch (ReservationNotFoundException ex) {
                                System.out.println("Error fetching reservation details: " + ex.getMessage());
                            }
                        } else if (action == 3) {
                            List<Reservation> listOfReservations = customerSessionBean.retrieveListOfReservationByCustomerId(customer.getGuestId());
                            if (listOfReservations.isEmpty()) {
                                System.out.println("This customer has not made any reservations");
                            } else {
                                System.out.println("*** View List Of Reservations: ***\n");
                                System.out.printf("%8s%20s%20s%15s%20s%20s\n", "Reservation ID", "Checkin Date", "CheckOut Name", "Number Of Rooms", "Total Amount Paid");
                                for (Reservation r : listOfReservations) {
                                    System.out.printf("%8s%20s%20s%15s%20s%20s\n", r.getReservationId(), r.getStartDate(), r.getEndDate(), r.getNumberOfRooms(), r.getTotalAmount());
                                }
                            }
                        }
                    }
                } catch (CustomerWrongCredentialsException e) {
                    System.out.println("The credentials entered are not in the database");
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
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    dateFormat.setLenient(false); // Disable lenient parsing to ensure strict format
                    System.out.println("Enter check-in date in MM/dd/yyyy: ");
                    Date checkInDate = dateFormat.parse(sc.nextLine());
                    System.out.println("Enter check-out date in MM/dd/yyyy: ");
                    Date checkOutDate = dateFormat.parse(sc.nextLine());
                    System.out.println("Enter the number of rooms required: ");
                    int numberOfRooms = sc.nextInt();
                    sc.nextLine();
                    List<RoomType> roomTypes = reservationSessionBean.retrieveListOfAvailableRoomType(checkInDate, checkOutDate, numberOfRooms);
                    for (RoomType rt : roomTypes) {
                        BigDecimal amount = paymentSessionBean.calculatePaymentForManagementClient(rt.getName(), checkInDate, checkOutDate, numberOfRooms);
                        System.out.println(rt.getName() + " (Total Amount :$" + amount + ")");
                    }
                } catch (ParseException ex) {
                    throw new InputNotValidException("The dates you have entered are in the wrong format, please try again");
                } catch (ReservationUnavailableException ex) {
                    System.out.println("An error has occurred while fetching the rooms: " + ex.getMessage() + "!\n");
                } catch (RoomTypeNotFoundException ex2) {
                    System.out.println("An error has occurred while fetching the rooms: " + ex2.getMessage() + "!\n");
                } catch (RoomRateNotFoundException ex3) {
                    System.out.println("An error has occurred while fetching the rooms: " + ex3.getMessage() + "!\n");
                }
            }
        }
    }

    private static Customer login(String email, String password) throws CustomerWrongCredentialsException {
        return customerSessionBean.login(email, password);
    }

    private static Reservation makeReservation(Long customerId, Date startDate, Date endDate, int numberOfRooms) throws ReservationUnavailableException, RoomTypeNotFoundException, RoomRateNotFoundException {

        Scanner sc = new Scanner(System.in);
        List<RoomType> roomTypes = reservationSessionBean.retrieveListOfAvailableRoomType(startDate, endDate, numberOfRooms);
        System.out.println("*** The Available Room Types Are As Follows: ***\n");
        for (RoomType rt : roomTypes) {
            BigDecimal amount = paymentSessionBean.calculatePaymentForManagementClient(rt.getName(), startDate, endDate, numberOfRooms);
            System.out.println(rt.getName() + " (Total Amount :$" + amount + ")");
        }
        System.out.println("Please input the name of the RoomType that you have chosen");
        String chosenRoomType = sc.nextLine();
        Reservation newReservation = reservationSessionBean.createReservation(customerId, chosenRoomType, endDate, endDate, numberOfRooms,
                paymentSessionBean.calculatePaymentForManagementClient(chosenRoomType, startDate, endDate, numberOfRooms)); //idk how to store the chosen amount, so ill jus recalculate the chosen one
        return newReservation;
    }
}
