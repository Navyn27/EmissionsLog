package com.navyn.emissionlog.modules.userManual.exceptions;

public class UserManualNotFoundException extends RuntimeException {
    public UserManualNotFoundException() {
        super("User manual not found");
    }

    public UserManualNotFoundException(String message) {
        super(message);
    }

    public UserManualNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

