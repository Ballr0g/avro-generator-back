package ru.hse.avrogen.util.schema.avro.validators;

import org.apache.avro.Schema;
import ru.hse.avrogen.dto.FieldRequirementsDto;

import java.util.List;
import java.util.Optional;

public class PayloadAvroFieldValidator extends AvroFieldValidatorBase {
    public PayloadAvroFieldValidator(List<String> requiredFields) {
        super(requiredFields);
    }

    @Override
    protected Optional<String> getRequiredName() {
        return Optional.empty();
    }

    @Override
    protected Optional<Schema.Type> getRequiredSchemaType() {
        return Optional.empty();
    }

    @Override
    protected List<FieldRequirementsDto> getFieldSpecificConstraintViolations(Schema schema) {
        return null;
    }
}
