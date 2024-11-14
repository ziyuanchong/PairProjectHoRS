/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package exception;

/**
 *
 * @author Witt
 */
public class CustomerNotLoggedInException extends Exception {

    /**
     * Creates a new instance of <code>CustomerNotLoggedInException</code>
     * without detail message.
     */
    public CustomerNotLoggedInException() {
    }

    /**
     * Constructs an instance of <code>CustomerNotLoggedInException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CustomerNotLoggedInException(String msg) {
        super(msg);
    }
}
