package com.navyn.emissionlog.Exceptions;

public class InvalidTokenException extends Exception{
    Exception exception = new Exception("Invalid token");
    public InvalidTokenException() {
        super();
    }
    public InvalidTokenException(String message) {
        super(message);
    }
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
    public InvalidTokenException(Throwable cause) {
        super(cause);
    }
}
