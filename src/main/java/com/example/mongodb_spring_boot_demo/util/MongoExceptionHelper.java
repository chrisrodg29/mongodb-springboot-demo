package com.example.mongodb_spring_boot_demo.util;

import com.example.mongodb_spring_boot_demo.api.GenericReadResponse;
import com.example.mongodb_spring_boot_demo.api.GenericWriteResponse;
import com.mongodb.MongoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

public class MongoExceptionHelper {

    private MongoExceptionHelper() {}

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoExceptionHelper.class);

    public static final String SUCCESS = "success";
    public static final String GENERIC_READ_ERROR = "An error occurred while trying to read from the database";
    public static final String GENERIC_WRITE_ERROR = "An error occurred while trying to write to the database";

    public static <T> GenericReadResponse<T> safeRead(Supplier<T> readOperation,
                                                      String emptyResultMessage) {
        try {
            T retrievedValue = readOperation.get();
            String operationSuccessStatus = isEmpty(retrievedValue)
                    ? emptyResultMessage
                    : SUCCESS;
            return new GenericReadResponse<>(
                    operationSuccessStatus,
                    retrievedValue
            );
        } catch (MongoException e) {
            LOGGER.error(GENERIC_READ_ERROR, e);
            return new GenericReadResponse<>(
                    GENERIC_READ_ERROR,
                    null,
                    e
            );
        }
    }

    public static <T> GenericReadResponse<T> safeRead(Supplier<T> readOperation) {
        return safeRead(readOperation, SUCCESS);
    }

    public static GenericWriteResponse safeWrite(Supplier<String> writeOperation,
                                                 String errorMessage) {
        try {
            String result = writeOperation.get();
            return new GenericWriteResponse(result);
        } catch (MongoException e) {
            LOGGER.error(errorMessage, e);
            return new GenericWriteResponse(errorMessage, e);
        }
    }

    public static GenericWriteResponse safeWrite(Supplier<String> writeOperation) {
        return safeWrite(writeOperation, GENERIC_WRITE_ERROR);
    }

}
