package ru.hse.avrogen.util.schema.avro.validation;

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
        if (Objects.isNull(schema)) {
            throw new IllegalArgumentException("The schema provided for parsing was null");
        }

        if (requiredTypeMatches(schema.getType())) {
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

        final var nestedFieldConstraintRequirements = getSchemaSpecificConstraintViolations(schema);
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
     * </p>y
     * @param requiredFields List of required fields for the schema.
     * @param schema The schema checked for required field presence.
     * @return The list containing names of missing required fields. Empty if the check is successful.
     */
    protected List<String> getMissingRequiredFields(List<String> requiredFields, Schema schema) {
        var schemaFields = schema.getFields();
        return requiredFields
                .stream()
                .filter(
                        field -> !schemaFields
                                .stream()
                                .map(Schema.Field::name)
                                .collect(Collectors.toSet())
                                .contains(field)
                )
                .toList();
    }

    /**
     * <p>
     * This method represents the 1st check performed by validateSchema method - the schema element type check.
     * </p>
     * <p>
     * It checks for mandatory type of the schema. Empty optional means no type
     * requirements - the check will be skipped.
     * </p>
     *
     * @return An optional with required Avro type of the schema or empty if there are no constraints set for it.
     */
    protected abstract List<Schema.Type> getAllowedSchemaTypes();

    /**
     * <p>
     * This method represents the 2nd check performed by validateSchema method - the schema name check.
     * </p>
     * <p>
     * It checks for mandatory naming of the schema. Empty optional means no field name
     * requirements - the check will be skipped.
     * </p>
     *
     * @return An optional with required name for the schema or empty if there are no constraints set for it.
     */
    protected abstract Optional<String> getRequiredName();

    /**
     * <p>
     * This method represents the 3rd check performed by validateSchema method - the mandatory fields check.
     * </p>
     * <p>
     * It checks for required fields of the schema. The field names are taken from the requiredFields field.
     * </p>
     *
     * @param schema The schema checked for required fields.
     * @return An optional with required name for the schema or empty if there are no constraints set for it.
     */
    protected List<String> getMissingRequiredFields(Schema schema) {
        return getMissingRequiredFields(this.requiredFields, schema);
    }

    /**
     * <p>
     * This method represents the final check performed by validateSchema method and is meant to be customizable.
     * </p>
     * <p>
     * This method is meant to be overridden in case more sophisticated checks are necessary. It is supposed to
     * collect a list of errors during validation.
     * </p>
     *
     * @param schema The schema checked for extra constraints.
     * @return A list containing objects describing every error occurred during validation.
     */
    protected List<FieldRequirementsDto> getSchemaSpecificConstraintViolations(Schema schema) {
        return Collections.emptyList();
    }

    protected boolean requiredTypeMatches(Schema.Type schemaType) {
        final var allowedSchemaTypes = getAllowedSchemaTypes();
        return Objects.isNull(allowedSchemaTypes)
                || allowedSchemaTypes.isEmpty()
                || allowedSchemaTypes.contains(schemaType);
    }
}
