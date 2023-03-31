package ru.hse.avrogen.mappers;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import ru.hse.avrogen.dto.responses.FallbackInternalErrorResponseDto;
import ru.hse.avrogen.dto.responses.MissingSchemaResponseDto;
import ru.hse.avrogen.dto.responses.PostIncorrectSchemaResponseDto;
import ru.hse.avrogen.util.exceptions.ApicurioClientException;
import ru.hse.avrogen.util.exceptions.SchemaVersionNotFoundException;
import ru.hse.avrogen.util.exceptions.validation.AvroGeneratorCheckerException;

import java.util.Objects;

public class AvroGeneratorExceptionMapper {
    @ServerExceptionMapper
    public RestResponse<PostIncorrectSchemaResponseDto> mapAvroFormatException(AvroGeneratorCheckerException ex) {
        return RestResponse.status(RestResponse.Status.BAD_REQUEST,
                new PostIncorrectSchemaResponseDto(ex.getSchemaRequirementsViolations()));
    }

    @ServerExceptionMapper
    public RestResponse<MissingSchemaResponseDto> mapMissingDeletedSchemaException(SchemaVersionNotFoundException ex) {
        return RestResponse.status(RestResponse.Status.NOT_FOUND,
                new MissingSchemaResponseDto(
                        ex.getStatusCode(),
                        ex.getSchemaPresence(),
                        ex.getErrorCode(),
                        ex.getMessage(),
                        getNestedCauseMessage(ex)
                ));
    }

    @ServerExceptionMapper
    public RestResponse<FallbackInternalErrorResponseDto> mapClientException(ApicurioClientException ex) {
        return RestResponse.status(RestResponse.Status.INTERNAL_SERVER_ERROR,
                new FallbackInternalErrorResponseDto(
                        ex.getStatusCode(),
                        ex.getErrorCode(),
                        ex.getMessage(),
                        getNestedCauseMessage(ex)
                ));
    }

    private <T extends Throwable> String getNestedCauseMessage(T ex) {
        final var originalCause = ex.getCause();
        return Objects.isNull(originalCause) ? "" : originalCause.getMessage();
    }
}
