/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package exception;

/**
 *
 * @author ziyuanchong
 */
public class GuestCheckOutException extends Exception{

    /**
     * Creates a new instance of <code>GuestCheckOutException</code> without
     * detail message.
     */
    public GuestCheckOutException() {
    }

    /**
     * Constructs an instance of <code>GuestCheckOutException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public GuestCheckOutException(String msg) {
        super(msg);
    }
}
