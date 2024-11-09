/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package exception;

/**
 *
 * @author ziyuanchong
 */
public class GuestCheckInException extends Exception{

    /**
     * Creates a new instance of <code>GuestCheckInException</code> without
     * detail message.
     */
    public GuestCheckInException() {
    }

    /**
     * Constructs an instance of <code>GuestCheckInException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public GuestCheckInException(String msg) {
        super(msg);
    }
}
