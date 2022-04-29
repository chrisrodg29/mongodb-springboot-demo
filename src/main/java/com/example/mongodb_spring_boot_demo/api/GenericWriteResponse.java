package com.example.mongodb_spring_boot_demo.api;

public class GenericWriteResponse {

    private String responseText;
    private String exceptionMessage;

    public GenericWriteResponse(String responseText) {
        this.responseText = responseText;
    }

    public GenericWriteResponse(String responseText, String exceptionMessage) {
        this.responseText = responseText;
        this.exceptionMessage = exceptionMessage;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public String toString() {
        return "GenericResponse{" +
                "responseText='" + responseText + '\'' +
                ", exceptionMessage=" + exceptionMessage +
                '}';
    }
}
