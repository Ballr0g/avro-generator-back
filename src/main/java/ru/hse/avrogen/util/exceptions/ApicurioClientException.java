package ru.hse.avrogen.util.exceptions;

import io.smallrye.mutiny.Uni;

import javax.ws.rs.core.Response;

public class ApicurioClientException extends AvroGeneratorRuntimeException {
    private final int errorCode;

    public static <T> Uni<T> createUni500ClientException(int errorCode, String message) {
        return createUniClientException(Response.Status.INTERNAL_SERVER_ERROR, errorCode, message);
    }

    public static <T> Uni<T> createUniClientException(Response.Status status, int errorCode, String message) {
        return Uni.createFrom().failure(
                new ApicurioClientException(
                        status,
                        errorCode,
                        message
                ));
    }

    public ApicurioClientException(Response.Status statusCode, int errorCode, String message) {
        super(statusCode, message);
        this.errorCode = errorCode;
    }

    public ApicurioClientException(Response.Status statusCode, int errorCode, String message, Throwable cause) {
        super(statusCode, message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
