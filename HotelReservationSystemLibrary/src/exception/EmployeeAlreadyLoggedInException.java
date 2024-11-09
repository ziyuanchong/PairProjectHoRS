/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package exception;

/**
 *
 * @author ziyuanchong
 */
public class EmployeeAlreadyLoggedInException extends Exception{

    /**
     * Creates a new instance of <code>EmployeeAlreadyLoggedInException</code>
     * without detail message.
     */
    public EmployeeAlreadyLoggedInException() {
    }

    /**
     * Constructs an instance of <code>EmployeeAlreadyLoggedInException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public EmployeeAlreadyLoggedInException(String msg) {
        super(msg);
    }
}
