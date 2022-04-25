package com.example.mongodb_spring_boot_demo.api;

public class GenericReadResponse<T> {

    private String operationSuccessStatus;
    private T data;
    private Exception exception;

    public GenericReadResponse() {}

    public GenericReadResponse(String operationSuccessStatus, T data) {
        this.operationSuccessStatus = operationSuccessStatus;
        this.data = data;
    }

    public GenericReadResponse(String operationSuccessStatus, T data, Exception exception) {
        this(operationSuccessStatus, data);
        this.exception = exception;
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

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

}
