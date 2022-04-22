package com.example.mongodb_spring_boot_demo.api;

public class GenericWriteResponse {

    private String responseText;
    private Exception exception;

    public GenericWriteResponse(String responseText) {
        this.responseText = responseText;
    }

    public GenericWriteResponse(String responseText, Exception exception) {
        this.responseText = responseText;
        this.exception = exception;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "GenericResponse{" +
                "responseText='" + responseText + '\'' +
                ", exception=" + exception +
                '}';
    }
}
