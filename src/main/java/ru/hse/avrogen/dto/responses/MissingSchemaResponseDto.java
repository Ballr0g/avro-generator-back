package ru.hse.avrogen.dto.responses;

import ru.hse.avrogen.util.enums.SchemaPresence;

import javax.ws.rs.core.Response;

public record MissingSchemaResponseDto(Response.Status statusCode, SchemaPresence schemaPresence,
                                       Integer errorCode, String cause, String originalCause) {
}
