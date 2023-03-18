package ru.hse.avrogen.util.schema.avro.validation;

import org.apache.avro.Schema;
import ru.hse.avrogen.dto.SchemaRequirementViolationDto;
import ru.hse.avrogen.util.errors.AvroSdpViolationType;
import ru.hse.avrogen.util.errors.AvroValidatorViolation;

import java.util.*;
import java.util.regex.Pattern;

public class SdpSchemaAvroFieldValidator extends AvroFieldValidatorBase {
    private static final String SYSTEM_INFO_REQUIRED_FIELD_NAME = "system_info";
    private static final String KEY_REQUIRED_FIELD_NAME = "key";
    private static final String PAYLOAD_REQUIRED_FIELD_NAME = "payload";
    private static final int NAMESPACE_SPLIT_BY_DOT_LENGTH = 2;
    private static final List<String> SDP_SCHEMA_REQUIRED_FIELDS = List.of(
            SYSTEM_INFO_REQUIRED_FIELD_NAME,
            KEY_REQUIRED_FIELD_NAME,
            PAYLOAD_REQUIRED_FIELD_NAME
    );
    private static final List<Schema.Type> ALLOWED_SDP_SCHEMA_TYPES = List.of(
            Schema.Type.RECORD
    );

    // Todo: more meaningful pattern
    private static final Pattern SCHEMA_NAMESPACE_FORMAT = Pattern.compile("^\\d+(\\.\\d+)*$");

    public SdpSchemaAvroFieldValidator() {
        super(SDP_SCHEMA_REQUIRED_FIELDS);
    }

    @Override
    protected Optional<String> getRequiredName() {
        return Optional.empty();
    }

    @Override
    protected List<Schema.Type> getAllowedSchemaTypes() {
        return ALLOWED_SDP_SCHEMA_TYPES;
    }

    @Override
    protected List<SchemaRequirementViolationDto> getSchemaSpecificConstraintViolations(Schema schema) {
        var requirementViolationsList = new ArrayList<SchemaRequirementViolationDto>();

        // Requirement #1: schema doc presence check.
        final var requiredSchemaDoc = schema.getDoc();
        if (Objects.isNull(requiredSchemaDoc) || requiredSchemaDoc.isBlank()) {
            requirementViolationsList.add(new SchemaRequirementViolationDto(
                    schema,
                    AvroValidatorViolation.SDP_FORMAT_VIOLATION,
                    AvroSdpViolationType.ILLEGAL_NAMING,
                    "SDP schema required doc field missing or empty"
            ));
        }

        // Requirement #2: schema namespace format check: {system}.{domain}
        final var sdpSchemaNamespace = schema.getNamespace();
        if (sdpSchemaNamespace.split("\\.").length != NAMESPACE_SPLIT_BY_DOT_LENGTH) {
            requirementViolationsList.add(new SchemaRequirementViolationDto(
                    schema,
                    AvroValidatorViolation.SDP_FORMAT_VIOLATION,
                    AvroSdpViolationType.ILLEGAL_NAMING,
                    String.format("SDP schema namespace format violation - expected: {system}.{domain}, got: %s",
                            sdpSchemaNamespace)
            ));
        }

        return requirementViolationsList;
    }
}
