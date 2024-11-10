/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package exception;

/**
 *
 * @author Witt
 */
public class InputNotValidException extends Exception {

    /**
     * Creates a new instance of <code>InputNotValidException</code> without
     * detail message.
     */
    public InputNotValidException() {
    }

    /**
     * Constructs an instance of <code>InputNotValidException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InputNotValidException(String msg) {
        super(msg);
    }
}
