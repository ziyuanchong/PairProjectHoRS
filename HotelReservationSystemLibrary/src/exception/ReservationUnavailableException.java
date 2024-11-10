/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package exception;

/**
 *
 * @author Witt
 */
public class ReservationUnavailableException extends Exception {

    /**
     * Creates a new instance of <code>ReservationUnavailableException</code>
     * without detail message.
     */
    public ReservationUnavailableException() {
    }

    /**
     * Constructs an instance of <code>ReservationUnavailableException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ReservationUnavailableException(String msg) {
        super(msg);
    }
}
