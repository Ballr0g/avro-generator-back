package ru.hse.avrogen.util.schema.avro.validation;

import org.apache.avro.Schema;
import ru.hse.avrogen.dto.SchemaRequirementViolationDto;
import ru.hse.avrogen.util.errors.AvroSdpViolationType;
import ru.hse.avrogen.util.errors.AvroValidatorViolation;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class KeyAvroFieldValidator extends AvroFieldValidatorBase {
    private static final Set<Schema.Type> PRIMITIVE_AVRO_TYPES = Set.of(
            Schema.Type.NULL, Schema.Type.BOOLEAN,
            Schema.Type.INT, Schema.Type.LONG, Schema.Type.FLOAT,
            Schema.Type.DOUBLE, Schema.Type.BYTES, Schema.Type.STRING
    );
    private static final List<Schema.Type> ALLOWED_KEY_SCHEMA_TYPES = List.of(
            Schema.Type.RECORD
    );

    public KeyAvroFieldValidator() {
        super(Collections.emptyList());
    }

    @Override
    protected Optional<String> getRequiredName() {
        return Optional.empty();
    }

    @Override
    protected List<Schema.Type> getAllowedSchemaTypes() {
        return ALLOWED_KEY_SCHEMA_TYPES;
    }

    @Override
    protected List<SchemaRequirementViolationDto> getSchemaSpecificConstraintViolations(Schema schema) {
        // Requirement #1: the schema has at least 1 field.
        final var keyFields = schema.getFields();
        if (keyFields.isEmpty()) {
            return List.of(new SchemaRequirementViolationDto(
                    schema,
                    AvroValidatorViolation.SDP_FORMAT_VIOLATION,
                    AvroSdpViolationType.ILLEGAL_STRUCTURE,
                    String.format(
                            "%s must contain at least one field",
                            schema.getFullName()
                    )
            ));
        }

        // Requirement #2: All schema fields are primitives.
        final var nonPrimitiveKeyFields = getNonPrimitiveKeyFields(keyFields);
        if (!nonPrimitiveKeyFields.isEmpty()) {
            return List.of(new SchemaRequirementViolationDto(
                    schema,
                    AvroValidatorViolation.SDP_FORMAT_VIOLATION,
                    AvroSdpViolationType.ILLEGAL_STRUCTURE,
                    String.format(
                            "%s key fields must be primitive types, found non primitives: %s",
                            schema.getFullName(),
                            String.join(", ", mapFieldsToNames(nonPrimitiveKeyFields))
                    )
            ));
        }

        return Collections.emptyList();
    }

    private List<Schema.Field> getNonPrimitiveKeyFields(List<Schema.Field> schemaFields) {
        return schemaFields
                .stream()
                .filter(field -> !PRIMITIVE_AVRO_TYPES.contains(field.schema().getType()))
                .toList();
    }

    private List<String> mapFieldsToNames(List<Schema.Field> schemaFields) {
        return schemaFields
                .stream()
                .map(Schema.Field::name)
                .toList();
    }
}
