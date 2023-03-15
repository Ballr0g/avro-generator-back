package ru.hse.avrogen.util.schema.avro.validators;

import org.apache.avro.Schema;
import ru.hse.avrogen.dto.FieldRequirementsDto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AvroFieldValidatorBase {
    protected final List<String> requiredFields;

    public AvroFieldValidatorBase(List<String> requiredFields) {
        this.requiredFields = requiredFields;
    }

    // Template method: validates against schema type, required fields and other constraints.
    // Idea: all schemas require type and name + have required fields.
    // Todo: meaningful FieldRequirementsDto returns for each case.
    public List<FieldRequirementsDto> validateSchema(Schema schema) {
        if (getRequiredSchemaType().isPresent() && schema.getType() != getRequiredSchemaType().get()) {
            throw new IllegalStateException("Schema type mismatch.");
        }

        if (getRequiredName().isPresent() && !Objects.equals(schema.getName(), getRequiredName().get())) {
            throw new IllegalStateException("Schema name mismatch.");
        }

        final var missingRequiredFields = getMissingRequiredFields(schema);
        if (!missingRequiredFields.isEmpty()) {
            throw new IllegalStateException("Missing required fields: "
                    + String.join(", ", missingRequiredFields));
        }

        final var nestedFieldConstraintRequirements = getFieldSpecificConstraintViolations(schema);
        if (!nestedFieldConstraintRequirements.isEmpty()) {
            throw new IllegalStateException("Nested constraint violations.");
        }

        return Collections.emptyList();
    }

    /**
     * Returns all the required fields that were not found in the given schema. Empty list means
     * all required fields being present in the schema.
     * <p>
     * Is overridable for a reason: some validators require checks on nested schema values.
     * </p>
     * @param schema The schema checked for required field presence.
     * @return The list containing names of missing required fields. Empty if the check is successful.
     */
    protected List<String> getMissingRequiredFields(Schema schema) {
        var schemaFields = schema.getFields();
        return requiredFields
                .stream()
                .filter(
                        field -> !schemaFields
                        .stream()
                        .map(Schema.Field::name)
                        .collect(Collectors.toSet())
                        .containsAll(requiredFields)
                )
                .toList();
    }

    protected abstract Optional<String> getRequiredName();

    protected abstract Optional<Schema.Type> getRequiredSchemaType();

    protected abstract List<FieldRequirementsDto> getFieldSpecificConstraintViolations(Schema schema);
}
