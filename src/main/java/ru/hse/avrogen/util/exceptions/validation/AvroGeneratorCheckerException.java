package ru.hse.avrogen.util.exceptions.validation;

import ru.hse.avrogen.dto.SchemaRequirementViolationDto;
import ru.hse.avrogen.util.exceptions.AvroGeneratorRuntimeException;

import javax.ws.rs.core.Response;
import java.util.List;

public class AvroGeneratorCheckerException extends AvroGeneratorRuntimeException {
    private final List<SchemaRequirementViolationDto> schemaRequirementsViolations;

    public AvroGeneratorCheckerException(List<SchemaRequirementViolationDto> schemaFormatViolations, String message) {
        super(Response.Status.BAD_REQUEST, message);
        schemaRequirementsViolations = schemaFormatViolations;
    }

    public AvroGeneratorCheckerException(List<SchemaRequirementViolationDto> schemaFormatViolations,
                                         String message,
                                         Throwable cause) {
        super(Response.Status.BAD_REQUEST, message, cause);
        schemaRequirementsViolations = schemaFormatViolations;
    }

    public List<SchemaRequirementViolationDto> getSchemaRequirementsViolations() {
        return schemaRequirementsViolations;
    }
}
