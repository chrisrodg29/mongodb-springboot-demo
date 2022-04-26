package com.example.mongodb_spring_boot_demo.healthcheck;

public class HealthCheckResponse {

    private String status;

    public HealthCheckResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
