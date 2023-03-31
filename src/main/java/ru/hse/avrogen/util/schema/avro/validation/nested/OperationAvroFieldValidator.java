package ru.hse.avrogen.util.schema.avro.validation.nested;

import org.apache.avro.Schema;
import ru.hse.avrogen.dto.SchemaRequirementViolationDto;
import ru.hse.avrogen.util.errors.AvroSdpViolationType;
import ru.hse.avrogen.util.errors.AvroValidatorViolation;
import ru.hse.avrogen.util.schema.avro.validation.AvroFieldValidatorBase;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class OperationAvroFieldValidator extends AvroFieldValidatorBase {
    private static final Set<String> SUPPORTED_OPERATION_ENUM_VALUES = Set.of("I", "U", "D");
    private static final List<Schema.Type> ALLOWED_OPERATION_SCHEMA_TYPES = List.of(
            Schema.Type.ENUM
    );

    public OperationAvroFieldValidator() {
        super(Collections.emptyList());
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
    protected List<SchemaRequirementViolationDto> getSchemaSpecificConstraintViolations(Schema schema) {
        // Requirement: enum values are {I, U, D}
        final var operationsEnumValues = schema.getEnumSymbols();
        if (!operationsEnumMatchesValues(operationsEnumValues)) {
            return List.of(new SchemaRequirementViolationDto(
                    schema.toString(),
                    AvroValidatorViolation.SDP_FORMAT_VIOLATION,
                    AvroSdpViolationType.ILLEGAL_STRUCTURE,
                    String.format(
                            "Expected %s to contain values {%s}, got: {%s}",
                            schema.getFullName(),
                            String.join(", ", SUPPORTED_OPERATION_ENUM_VALUES),
                            String.join(", ", operationsEnumValues)
                    )
            ));
        }

        return Collections.emptyList();
    }

    private boolean operationsEnumMatchesValues(List<String> enumValues) {
        return enumValues.size() == SUPPORTED_OPERATION_ENUM_VALUES.size()
                && SUPPORTED_OPERATION_ENUM_VALUES.containsAll(enumValues);
    }
}
