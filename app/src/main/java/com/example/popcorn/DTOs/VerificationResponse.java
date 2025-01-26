package com.example.popcorn.DTOs;

public class VerificationResponse {
    private boolean isEmailVerified;
    private String message;

    public VerificationResponse(boolean isEmailVerified, String message) {
        this.isEmailVerified = isEmailVerified;
        this.message = message;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
