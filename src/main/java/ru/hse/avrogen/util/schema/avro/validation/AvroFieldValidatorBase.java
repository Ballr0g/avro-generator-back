package ru.hse.avrogen.util.schema.avro.validation;

import org.apache.avro.Schema;
import ru.hse.avrogen.dto.SchemaRequirementViolationDto;
import ru.hse.avrogen.util.errors.AvroSdpViolationType;
import ru.hse.avrogen.util.errors.AvroValidatorViolation;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AvroFieldValidatorBase {
    // Todo: SchemaRequirementViolationDto builder.
    protected final List<String> requiredFields;

    public AvroFieldValidatorBase(List<String> requiredFields) {
        this.requiredFields = requiredFields;
    }

    // Template method: validates against schema type, required fields and other constraints.
    // Idea: all schemas require type and name + have required fields.
    public List<SchemaRequirementViolationDto> validateSchema(Schema schema) throws IllegalArgumentException {
        if (Objects.isNull(schema)) {
            throw new IllegalArgumentException("The schema provided for parsing was null");
        }

        final var schemaType = schema.getType();
        if (!requiredTypeMatches(schemaType)) {
            return List.of(new SchemaRequirementViolationDto(
                    schema,
                    AvroValidatorViolation.SDP_FORMAT_VIOLATION,
                    AvroSdpViolationType.SCHEMA_TYPE_MISMATCH,
                    String.format(
                            "Expected %s type: %s, got: %s",
                            schema.getName(),
                            String.join(" | ", getAllowedTypeNames()),
                            schemaType
                    )
            ));
        }

        var violationsList = new ArrayList<SchemaRequirementViolationDto>();
        final var schemaName = schema.getName();
        if (getRequiredName().isPresent() && !Objects.equals(schemaName, getRequiredName().get())) {
            violationsList.add(new SchemaRequirementViolationDto(
                    schema,
                    AvroValidatorViolation.SDP_FORMAT_VIOLATION,
                    AvroSdpViolationType.ILLEGAL_NAMING,
                    String.format(
                            "Expected %s name: %s, got %s",
                            schema.getName(),
                            getRequiredName().get(),
                            schemaName)
                    )
            );
        }

        final var missingRequiredFields = getMissingRequiredFields(schema);
        if (!missingRequiredFields.isEmpty()) {
            violationsList.add(new SchemaRequirementViolationDto(
                            schema,
                            AvroValidatorViolation.SDP_FORMAT_VIOLATION,
                            AvroSdpViolationType.MISSING_REQUIRED_FIELD,
                            String.format(
                                    "Missing %s required fields: %s",
                                    schema.getName(),
                                    String.join(", ", missingRequiredFields)
                            )
                    )
            );
            return violationsList;
        }

        final var nestedFieldConstraintRequirements = getSchemaSpecificConstraintViolations(schema);
        if (!nestedFieldConstraintRequirements.isEmpty()) {
            violationsList.addAll(nestedFieldConstraintRequirements);
        }

        return violationsList;
    }

    /**
     * Returns all the required fields that were not found in the given schema. Empty list means
     * all required fields being present in the schema.
     * <p>
     * Is overridable for a reason: some validators require checks on nested schema values.
     * </p>y
     * @param requiredFieldNames List of required field names for the schema. No checks made if null of empty.
     * @param schema The schema checked for required field presence.
     * @return The list containing names of missing required fields. Empty if the check is successful.
     */
    protected List<String> getMissingRequiredFields(List<String> requiredFieldNames, Schema schema) {
        if (Objects.isNull(requiredFieldNames) || requiredFieldNames.isEmpty()) {
            return Collections.emptyList();
        }

        var schemaFields = schema.getFields();
        return requiredFieldNames
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
    protected List<SchemaRequirementViolationDto> getSchemaSpecificConstraintViolations(Schema schema) {
        return Collections.emptyList();
    }

    protected boolean requiredTypeMatches(Schema.Type schemaType) {
        final var allowedSchemaTypes = getAllowedSchemaTypes();
        return Objects.isNull(allowedSchemaTypes)
                || allowedSchemaTypes.isEmpty()
                || allowedSchemaTypes.contains(schemaType);
    }

    // Todo: move to SchemaUtils.
    private List<String> getAllowedTypeNames() {
        return getAllowedSchemaTypes()
                .stream()
                .map(Schema.Type::getName)
                .toList();
    }
}
