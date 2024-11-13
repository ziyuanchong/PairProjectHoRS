/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package exception;

/**
 *
 * @author Witt
 */
public class RoomTypeIsDisabledException extends Exception {

    /**
     * Creates a new instance of <code>RoomTypeIsDisabledException</code>
     * without detail message.
     */
    public RoomTypeIsDisabledException() {
    }

    /**
     * Constructs an instance of <code>RoomTypeIsDisabledException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public RoomTypeIsDisabledException(String msg) {
        super(msg);
    }
}
