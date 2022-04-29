package com.example.mongodb_spring_boot_demo.api;

public class GenericReadResponse<T> {

    private String operationSuccessStatus;
    private T data;
    private String exceptionMessage;

    public GenericReadResponse() {}

    public GenericReadResponse(String operationSuccessStatus, T data) {
        this.operationSuccessStatus = operationSuccessStatus;
        this.data = data;
    }

    public GenericReadResponse(String operationSuccessStatus, T data, String exceptionMessage) {
        this(operationSuccessStatus, data);
        this.exceptionMessage = exceptionMessage;
    }

    public String getOperationSuccessStatus() {
        return operationSuccessStatus;
    }

    public void setOperationSuccessStatus(String operationSuccessStatus) {
        this.operationSuccessStatus = operationSuccessStatus;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

}
