package ru.hse.avrogen.util.schema.avro.validation;

import org.apache.avro.Schema;
import ru.hse.avrogen.dto.FieldRequirementsDto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PayloadAvroFieldValidator extends AvroFieldValidatorBase {
    private static final int OPTIONAL_RECORD_FIELDS_COUNT = 2;
    private static final String NESTED_STRUCTURE_IS_KEY_FIELDS = "is_key";
    private static final List<String> NESTED_STRUCTURE_REQUIRED_FIELDS = List.of(
            NESTED_STRUCTURE_IS_KEY_FIELDS
    );

    public PayloadAvroFieldValidator() {
        super(Collections.emptyList());
    }

    @Override
    protected Optional<String> getRequiredName() {
        return Optional.empty();
    }

    @Override
    protected Optional<Schema.Type> getRequiredSchemaType() {
        return Optional.of(Schema.Type.UNION);
    }

    @Override
    protected List<FieldRequirementsDto> getSchemaSpecificConstraintViolations(Schema schema) {
        // Requirement #1: the schema is an optional record (either null or record).
        final var payloadUnionTypes = schema.getTypes();
        if (!schemaIsOptionalRecord(payloadUnionTypes)) {
            // Todo: same as type requirements violation.
            throw new IllegalStateException("Schema type mismatch.");
        }

        // Todo: Requirement #2: nested fields are either primitives or structures with is_key: true.
        return null;
    }

    private boolean schemaIsOptionalRecord(List<Schema> unionSchemas) {
        return unionSchemas.size() == OPTIONAL_RECORD_FIELDS_COUNT
                && unionSchemas.get(0).getType() == Schema.Type.NULL
                && unionSchemas.get(1).getType() == Schema.Type.RECORD;
    }
}
