package ru.hse.avrogen.util.schema.avro.validation;

import org.apache.avro.Schema;
import ru.hse.avrogen.dto.SchemaRequirementViolationDto;
import ru.hse.avrogen.util.errors.AvroSdpViolationType;
import ru.hse.avrogen.util.errors.AvroValidatorViolation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PayloadAvroFieldValidator extends AvroFieldValidatorBase {
    private static final int RECORD_TYPES_COUNT = 1;
    private static final int OPTIONAL_RECORD_TYPES_COUNT = 2;
    private static final String NESTED_STRUCTURE_IS_KEY_FIELDS = "is_key";
    private static final List<String> NESTED_STRUCTURE_REQUIRED_FIELDS = List.of(
            NESTED_STRUCTURE_IS_KEY_FIELDS
    );
    private static final Map<Schema.Type, String> ALLOWED_PAYLOAD_SCHEMA_TYPES_TO_NAMES = Map.of(
            Schema.Type.UNION, "Null or Record",
            Schema.Type.RECORD, "Record"
    );

    public PayloadAvroFieldValidator() {
        super(Collections.emptyList());
    }

    @Override
    protected Optional<String> getRequiredName() {
        return Optional.empty();
    }

    @Override
    protected List<Schema.Type> getAllowedSchemaTypes() {
        return ALLOWED_PAYLOAD_SCHEMA_TYPES_TO_NAMES.keySet().stream().toList();
    }

    @Override
    protected List<SchemaRequirementViolationDto> getSchemaSpecificConstraintViolations(Schema schema) {
        // Requirement #1: the schema is an optional record (either null or record) or record.
        final var payloadUnionTypes = schema.getTypes();
        var schemaIsOptionalRecord = isSchemaOptionalRecord(payloadUnionTypes);
        if (!(schemaIsOptionalRecord || isSchemaRecord(payloadUnionTypes))) {
            return List.of(new SchemaRequirementViolationDto(
                    schema,
                    AvroValidatorViolation.SDP_FORMAT_VIOLATION,
                    AvroSdpViolationType.SCHEMA_TYPE_MISMATCH,
                    String.format(
                            "Expected %s type: %s, got: %s",
                            schema.getName(),
                            String.join(" | ", ALLOWED_PAYLOAD_SCHEMA_TYPES_TO_NAMES.values()),
                            String.join(" | ", mapSchemaTypesToNames(payloadUnionTypes))
                    )
            ));
        }

        final var nestedSchema = schemaIsOptionalRecord ? payloadUnionTypes.get(1) : payloadUnionTypes.get(0);
        // Todo: Requirement #2: nested fields are either primitives or structures with is_key: true.
        return null;
    }

    private Optional<SchemaRequirementViolationDto> getNestedPayloadIsKeyViolations(Schema nestedPayloadSchema) {
        var nestedRecordFields = nestedPayloadSchema.getFields()
                .stream()
                .filter(field -> field.schema().getType() == Schema.Type.RECORD)
                .toList();

        if (!nestedRecordFields.isEmpty()) {
            // Todo: error on missing is_key fields.
            return Optional.empty();
        }
        return Optional.empty();
    }

    private boolean isSchemaRecord(List<Schema> unionSchemas) {
        return unionSchemas.size() == RECORD_TYPES_COUNT
                && unionSchemas.get(0).getType() == Schema.Type.RECORD;
    }
    private boolean isSchemaOptionalRecord(List<Schema> unionSchemas) {
        return unionSchemas.size() == OPTIONAL_RECORD_TYPES_COUNT
                && unionSchemas.get(0).getType() == Schema.Type.NULL
                && unionSchemas.get(1).getType() == Schema.Type.RECORD;
    }

    // Todo: move to SchemaUtils.
    private List<String> mapSchemaTypesToNames(List<Schema> schemaTypes) {
        return schemaTypes
                .stream()
                .map(schema -> schema.getType().getName())
                .toList();
    }
}
