package ru.hse.avrogen.util.exceptions;

import javax.ws.rs.core.Response;

public class AvroGeneratorRuntimeException extends RuntimeException {
    private final Response.Status statusCode;
    public AvroGeneratorRuntimeException(Response.Status statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public AvroGeneratorRuntimeException(Response.Status statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public Response.Status getStatusCode() {
        return statusCode;
    }
}
