/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package exception;

/**
 *
 * @author ziyuanchong
 */
public class RoomNotAvailableException extends Exception{

    /**
     * Creates a new instance of <code>RoomNotAvailableException</code> without
     * detail message.
     */
    public RoomNotAvailableException() {
    }

    /**
     * Constructs an instance of <code>RoomNotAvailableException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public RoomNotAvailableException(String msg) {
        super(msg);
    }
}
