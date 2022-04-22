package com.example.mongodb_spring_boot_demo.api;

public enum StatusMessage {
    SUCCESS("success"),
    NO_CUSTOMER_FOUND("No customer was found with that id");


    private final String message;

    StatusMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
