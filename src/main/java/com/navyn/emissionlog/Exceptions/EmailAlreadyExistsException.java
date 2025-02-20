package com.navyn.emissionlog.Exceptions;

public class EmailAlreadyExistsException extends Throwable {
    Exception exception = new Exception("Email already in use");
}
