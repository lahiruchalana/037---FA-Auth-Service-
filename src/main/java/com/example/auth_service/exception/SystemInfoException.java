package com.cloudofgoods.auth.exception;

/**
 * Generified Exception class to handle all the System Info Level Exceptions
 */
public class SystemInfoException extends SystemRootException {

    /**
     * No Args Constructor
     */
    public SystemInfoException() {
    }

    /**
     * @param message : java.lang.String
     * Single Args Contractor which accepts a String exception message as the parameter
     */
    public SystemInfoException(String message) {
        super(message);
    }

    /**
     * @param message : java.lang.String
     * @param cause: java.lang.Throwable
     * Two Args Contractor which accepts a String exception message and a Throwable cause as the parameters
     */
    public SystemInfoException(String message, Throwable cause) {
        super(message, cause);
    }
}
