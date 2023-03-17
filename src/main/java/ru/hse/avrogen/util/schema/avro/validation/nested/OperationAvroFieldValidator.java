package ru.hse.avrogen.util.schema.avro.validation.nested;

import org.apache.avro.Schema;
import ru.hse.avrogen.dto.FieldRequirementsDto;
import ru.hse.avrogen.util.schema.avro.validation.AvroFieldValidatorBase;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class OperationAvroFieldValidator extends AvroFieldValidatorBase {
    private static final Set<String> SUPPORTED_OPERATION_ENUM_VALUES = Set.of("I", "U", "D");
    private static final List<Schema.Type> ALLOWED_OPERATION_SCHEMA_TYPES = List.of(
            Schema.Type.ENUM
    );

    public OperationAvroFieldValidator(List<String> requiredFields) {
        super(requiredFields);
    }

    @Override
    protected List<Schema.Type> getAllowedSchemaTypes() {
        return ALLOWED_OPERATION_SCHEMA_TYPES;
    }

    @Override
    protected Optional<String> getRequiredName() {
        return Optional.empty();
    }

    @Override
    protected List<FieldRequirementsDto> getSchemaSpecificConstraintViolations(Schema schema) {
        // Requirement: enum values are {I, U, D}
        final var operationsEnumValues = schema.getEnumSymbols();
        if (!operationsEnumMatchesValues(operationsEnumValues)) {
            // Todo: return the error about mismatching enum values.
            return Collections.emptyList();
        }

        return Collections.emptyList();
    }

    private boolean operationsEnumMatchesValues(List<String> enumValues) {
        return enumValues.size() == SUPPORTED_OPERATION_ENUM_VALUES.size()
                && SUPPORTED_OPERATION_ENUM_VALUES.containsAll(enumValues);
    }
}
