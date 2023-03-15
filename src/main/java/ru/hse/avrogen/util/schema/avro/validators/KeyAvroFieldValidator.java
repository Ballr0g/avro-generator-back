package ru.hse.avrogen.util.schema.avro.validators;

import org.apache.avro.Schema;
import ru.hse.avrogen.dto.FieldRequirementsDto;

import java.util.List;
import java.util.Optional;

public class KeyAvroFieldValidator extends AvroFieldValidatorBase {
    public KeyAvroFieldValidator(List<String> requiredFields) {
        super(requiredFields);
    }

    @Override
    protected Optional<String> getRequiredName() {
        return Optional.empty();
    }

    @Override
    protected Optional<Schema.Type> getRequiredSchemaType() {
        return Optional.of(Schema.Type.RECORD);
    }

    @Override
    protected List<FieldRequirementsDto> getFieldSpecificConstraintViolations(Schema schema) {
        return null;
    }
}
