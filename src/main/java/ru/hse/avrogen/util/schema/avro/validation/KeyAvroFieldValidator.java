package ru.hse.avrogen.util.schema.avro.validation;

import org.apache.avro.Schema;
import ru.hse.avrogen.dto.FieldRequirementsDto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class KeyAvroFieldValidator extends AvroFieldValidatorBase {
    private static final Set<Schema.Type> PRIMITIVE_AVRO_TYPES = Set.of(
            Schema.Type.NULL, Schema.Type.BOOLEAN,
            Schema.Type.INT, Schema.Type.LONG, Schema.Type.FLOAT,
            Schema.Type.DOUBLE, Schema.Type.BYTES, Schema.Type.STRING
    );

    public KeyAvroFieldValidator() {
        super(Collections.emptyList());
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
    protected List<FieldRequirementsDto> getSchemaSpecificConstraintViolations(Schema schema) {
        // Requirement #1: the schema has at least 1 field.
        final var keyFields = schema.getFields();
        if (keyFields.isEmpty()) {
            // Todo: return the error telling that key cannot be empty.
            return Collections.emptyList();
        }

        // Requirement #2: All schema fields are primitives.
        if (!allSchemaFieldsPrimitives(keyFields)) {
            // Todo: return the error telling that key cannot be empty.
            return Collections.emptyList();
        }

        return Collections.emptyList();
    }

    private boolean allSchemaFieldsPrimitives(List<Schema.Field> schemaFields) {
        return schemaFields
                .stream()
                .allMatch(field -> PRIMITIVE_AVRO_TYPES.contains(field.schema().getType()));
    }
}
