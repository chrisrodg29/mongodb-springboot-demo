package com.example.mongodb_spring_boot_demo.healthcheck;

import org.bson.Document;

public class HealthCheckResponse {

    private String status;
    private Document healthCheckCommand;
    private Document response;
    private String exceptionMessage;

    public HealthCheckResponse(String status, Document healthCheckCommand, Document response) {
        this.status = status;
        this.healthCheckCommand = healthCheckCommand;
        this.response = response;
    }

    public HealthCheckResponse(String status, Document healthCheckCommand, String exceptionMessage) {
        this.status = status;
        this.healthCheckCommand = healthCheckCommand;
        this.exceptionMessage = exceptionMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Document getHealthCheckCommand() {
        return healthCheckCommand;
    }

    public void setHealthCheckCommand(Document healthCheckCommand) {
        this.healthCheckCommand = healthCheckCommand;
    }

    public Document getResponse() {
        return response;
    }

    public void setResponse(Document response) {
        this.response = response;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}
