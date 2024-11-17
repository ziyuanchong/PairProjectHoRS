/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package exception;

/**
 *
 * @author ziyuanchong
 */
public class GuestNotFoundException extends Exception{

    /**
     * Creates a new instance of <code>GuestNotFoundException</code> without
     * detail message.
     */
    public GuestNotFoundException() {
    }

    /**
     * Constructs an instance of <code>GuestNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public GuestNotFoundException(String msg) {
        super(msg);
    }
}
