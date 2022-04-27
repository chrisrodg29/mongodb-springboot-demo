package com.example.mongodb_spring_boot_demo.util;

import com.example.mongodb_spring_boot_demo.api.GenericReadResponse;
import com.example.mongodb_spring_boot_demo.api.GenericWriteResponse;
import com.mongodb.MongoException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.example.mongodb_spring_boot_demo.util.MongoExceptionHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MongoExceptionHelperTest {

    @Test
    void testSafeRead_DefaultWhenNullReturned() {
        GenericReadResponse<Object> result = safeRead(() -> null);

        assertEquals(SUCCESS, result.getOperationSuccessStatus());
        assertNull(result.getData());
    }

    @Test
    void testSafeRead_MessageWhenNullReturned() {
        String message = "the message";

        GenericReadResponse<Object> result = safeRead(() -> null, message);

        assertEquals(message, result.getOperationSuccessStatus());
        assertNull(result.getData());
    }

    @Test
    void testSafeRead_MessageWhenEmptyListReturned() {
        String message = "the message";
        List<Object> emptyList = new ArrayList<>();
        GenericReadResponse<List<Object>> result = safeRead(() -> emptyList, message);

        assertEquals(message, result.getOperationSuccessStatus());
        assertEquals(emptyList, result.getData());
    }

    @Test
    void testSafeRead_MessageWhenSomethingReturned() {
        String message = "the message";
        Object something = new Object();
        GenericReadResponse<Object> result = safeRead(() -> something, message);

        assertEquals(SUCCESS, result.getOperationSuccessStatus());
        assertEquals(something, result.getData());
    }

    @Test
    void testSafeRead_MongoExceptionThrown() {
        MongoException e = mock(MongoException.class);
        GenericReadResponse<Object> result = safeRead(() -> {
            throw e;
        });

        assertEquals(GENERIC_READ_ERROR, result.getOperationSuccessStatus());
        assertNull(result.getData());
        assertEquals(e, result.getException());
    }

    @Test
    void testSafeWrite_Success() {
        String resultMessage = "my message";

        GenericWriteResponse result = safeWrite(() -> resultMessage);

        assertEquals(resultMessage, result.getResponseText());
    }

    @Test
    void testSafeWrite_WriteFailWithProvidedErrorMessage() {
        MongoException e = mock(MongoException.class);
        String errorMessage = "my message";
        GenericWriteResponse result = safeWrite(() -> {
            throw e;
        }, errorMessage);

        assertEquals(errorMessage, result.getResponseText());
        assertEquals(e, result.getException());
    }

    @Test
    void testSafeWrite_WriteFailWithNoProvidedErrorMessage() {
        MongoException e = mock(MongoException.class);
        GenericWriteResponse result = safeWrite(() -> {
            throw e;
        });

        assertEquals(GENERIC_WRITE_ERROR, result.getResponseText());
        assertEquals(e, result.getException());
    }

}