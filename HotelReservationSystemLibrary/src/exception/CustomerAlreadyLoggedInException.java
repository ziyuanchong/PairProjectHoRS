/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package exception;

/**
 *
 * @author Witt
 */
public class CustomerAlreadyLoggedInException extends Exception {

    /**
     * Creates a new instance of <code>CustomerAlreadyLoggedInException</code>
     * without detail message.
     */
    public CustomerAlreadyLoggedInException() {
    }

    /**
     * Constructs an instance of <code>CustomerAlreadyLoggedInException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public CustomerAlreadyLoggedInException(String msg) {
        super(msg);
    }
}
