package ru.hse.avrogen.dto.responses;

import javax.ws.rs.core.Response;

public record FallbackInternalErrorResponseDto(Response.Status originalOperationStatus, Integer errorCode,
                                               String cause, String originalCause) {
}
