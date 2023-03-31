package ru.hse.avrogen.util.exceptions;

import ru.hse.avrogen.util.enums.SchemaPresence;

import javax.ws.rs.core.Response;

public class SchemaVersionNotFoundException extends ApicurioClientException {
    private final SchemaPresence schemaPresence;
    public SchemaVersionNotFoundException(Response.Status statusCode, SchemaPresence schemaPresence,
                                          int errorCode, String message) {
        super(statusCode, errorCode, message);
        this.schemaPresence = schemaPresence;
    }

    public SchemaVersionNotFoundException(Response.Status statusCode, SchemaPresence schemaPresence,
                                          int errorCode, String message, Throwable cause) {
        super(statusCode, errorCode, message, cause);
        this.schemaPresence = schemaPresence;
    }

    public SchemaPresence getSchemaPresence() {
        return schemaPresence;
    }
}
