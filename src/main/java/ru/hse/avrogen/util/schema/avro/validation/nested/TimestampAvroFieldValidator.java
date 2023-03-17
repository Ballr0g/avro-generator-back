package ru.hse.avrogen.util.schema.avro.validation.nested;

import org.apache.avro.Schema;
import ru.hse.avrogen.dto.FieldRequirementsDto;
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
    protected Optional<Schema.Type> getRequiredSchemaType() {
        return Optional.empty();
    }

    @Override
    protected Optional<String> getRequiredName() {
        return Optional.empty();
    }

    @Override
    protected List<FieldRequirementsDto> getSchemaSpecificConstraintViolations(Schema schema) {
        // Requirement #1: timestamp is a LogicalType with timestamp-millis value.
        final var millisLogicalType = schema.getLogicalType();
        if (Objects.isNull(millisLogicalType)) {
            // Todo: return the error telling that the field is not a LogicalType.
            return Collections.emptyList();
        }

        // Requirement #2: the LogicalType is named timestamp-millis.
        if (!Objects.equals(millisLogicalType.getName(), TIMESTAMP_LOGICAL_FIELD_NAME)) {
            // Todo: return the error telling that the field is not properly named.
            return Collections.emptyList();
        }

        return Collections.emptyList();
    }


}
