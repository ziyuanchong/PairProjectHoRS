/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package exception;

/**
 *
 * @author ziyuanchong
 */
public class EmployeeNotLoggedInException extends Exception{

    /**
     * Creates a new instance of <code>EmployeeNotLoggedInException</code>
     * without detail message.
     */
    public EmployeeNotLoggedInException() {
    }

    /**
     * Constructs an instance of <code>EmployeeNotLoggedInException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public EmployeeNotLoggedInException(String msg) {
        super(msg);
    }
}
