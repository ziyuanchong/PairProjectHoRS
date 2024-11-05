/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package exception;

/**
 *
 * @author ziyuanchong
 */
public class GuestExistException extends Exception{

    /**
     * Creates a new instance of <code>GuestExistException</code> without detail
     * message.
     */
    public GuestExistException() {
    }

    /**
     * Constructs an instance of <code>GuestExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public GuestExistException(String msg) {
        super(msg);
    }
}
