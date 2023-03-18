package ru.hse.avrogen.util.schema.avro.validation.nested;

import org.apache.avro.Schema;
import ru.hse.avrogen.dto.SchemaRequirementViolationDto;
import ru.hse.avrogen.util.errors.AvroSdpViolationType;
import ru.hse.avrogen.util.errors.AvroValidatorViolation;
import ru.hse.avrogen.util.schema.avro.validation.AvroFieldValidatorBase;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TimestampAvroFieldValidator extends AvroFieldValidatorBase {
    private static final String LOGICAL_TYPE_FIELD_NAME = "logicalType";
    private static final String TIMESTAMP_LOGICAL_FIELD_NAME = "timestamp-millis";

    private static final List<String> TIMESTAMP_SCHEMA_REQUIRED_FIELDS = List.of(
            LOGICAL_TYPE_FIELD_NAME
    );

    public TimestampAvroFieldValidator() {
        super(TIMESTAMP_SCHEMA_REQUIRED_FIELDS);
    }

    @Override
    protected List<Schema.Type> getAllowedSchemaTypes() {
        return Collections.emptyList();
    }

    @Override
    protected Optional<String> getRequiredName() {
        return Optional.empty();
    }

    @Override
    protected List<SchemaRequirementViolationDto> getSchemaSpecificConstraintViolations(Schema schema) {
        // Requirement #1: timestamp is a LogicalType with timestamp-millis value.
        final var millisLogicalType = schema.getLogicalType();
        if (Objects.isNull(millisLogicalType)) {
            return List.of(new SchemaRequirementViolationDto(
                    schema,
                    AvroValidatorViolation.SDP_FORMAT_VIOLATION,
                    AvroSdpViolationType.ILLEGAL_STRUCTURE,
                    String.format(
                            "%s must be a logical type",
                            schema.getName()
                    )
            ));
        }

        // Requirement #2: the LogicalType is named timestamp-millis.
        final var millisLogicalTypeName = millisLogicalType.getName();
        if (!Objects.equals(millisLogicalTypeName, TIMESTAMP_LOGICAL_FIELD_NAME)) {
            return List.of(new SchemaRequirementViolationDto(
                    schema,
                    AvroValidatorViolation.SDP_FORMAT_VIOLATION,
                    AvroSdpViolationType.ILLEGAL_NAMING,
                    String.format(
                            "Expected %s name: %s, got: %s",
                            schema.getName(),
                            TIMESTAMP_LOGICAL_FIELD_NAME,
                            millisLogicalTypeName
                    )
            ));
        }

        return Collections.emptyList();
    }


}
