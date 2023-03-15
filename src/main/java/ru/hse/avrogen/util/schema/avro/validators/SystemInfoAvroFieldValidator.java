package ru.hse.avrogen.util.schema.avro.validators;

import org.apache.avro.Schema;
import ru.hse.avrogen.dto.FieldRequirementsDto;

import java.util.List;
import java.util.Optional;

public class SystemInfoAvroFieldValidator extends AvroFieldValidatorBase {
    private static final String SYSTEM_INFO_TYPE_NAME = "system_info";

    private static final List<String> SYSTEM_INFO_SCHEMA_REQUIRED_FIELDS = List.of("timestamp", "operation");

    public SystemInfoAvroFieldValidator() {
        super(SYSTEM_INFO_SCHEMA_REQUIRED_FIELDS);
    }

    @Override
    protected Optional<String> getRequiredName() {
        return Optional.of(SYSTEM_INFO_TYPE_NAME);
    }

    @Override
    protected Optional<Schema.Type> getRequiredSchemaType() {
        return Optional.of(Schema.Type.RECORD);
    }

    @Override
    protected List<FieldRequirementsDto> getFieldSpecificConstraintViolations(Schema schema) {
        return null;
    }

    @Override
    protected List<String> getMissingRequiredFields(Schema schema) {
        // Todo: handle nesting.
        return super.getMissingRequiredFields(schema);
    }
}
