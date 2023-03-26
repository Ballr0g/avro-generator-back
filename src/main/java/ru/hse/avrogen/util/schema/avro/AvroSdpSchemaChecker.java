package ru.hse.avrogen.util.schema.avro;

import org.apache.avro.Schema;
import org.apache.avro.SchemaParseException;
import ru.hse.avrogen.dto.SchemaRequirementViolationDto;
import ru.hse.avrogen.util.errors.AvroSdpViolationType;
import ru.hse.avrogen.util.errors.AvroValidatorViolation;
import ru.hse.avrogen.util.schema.avro.validation.KeyAvroFieldValidator;
import ru.hse.avrogen.util.schema.avro.validation.PayloadAvroFieldValidator;
import ru.hse.avrogen.util.schema.avro.validation.SdpSchemaAvroFieldValidator;
import ru.hse.avrogen.util.schema.avro.validation.SystemInfoAvroFieldValidator;
import ru.hse.avrogen.util.schema.avro.validation.nested.OperationAvroFieldValidator;
import ru.hse.avrogen.util.schema.avro.validation.nested.TimestampAvroFieldValidator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

@ApplicationScoped
public class AvroSdpSchemaChecker {
    @Inject
    SdpSchemaAvroFieldValidator sdpValidator;
    @Inject
    SystemInfoAvroFieldValidator systemInfoValidator;
    @Inject
    TimestampAvroFieldValidator timestampValidator;
    @Inject
    OperationAvroFieldValidator operationValidator;
    @Inject
    KeyAvroFieldValidator keyValidator;
    @Inject
    PayloadAvroFieldValidator payloadValidator;

    public List<SchemaRequirementViolationDto> validateSchema(String jsonSchema) {
        return validateSchemaWithCheck(jsonSchema, this::validateSdpFormat);
    }

    // previous is Schema because it is already supposedly parsed by the GET method.
    public List<SchemaRequirementViolationDto> validateSchemaUpdate(Schema previous, String current) {
        return validateSchemaWithCheck(current, currentSchema -> validateNoKeyChanges(previous, currentSchema));
    }

    private List<SchemaRequirementViolationDto> validateSchemaWithCheck(String jsonSchema,
                                                                        Function<Schema, List<SchemaRequirementViolationDto>> mapper) {
        Schema schema;
        try {
            var avroSchemaParser = new Schema.Parser();
            schema = avroSchemaParser.parse(jsonSchema);
        } catch (SchemaParseException parseException) {
            return List.of(new SchemaRequirementViolationDto(
                    null,
                    AvroValidatorViolation.AVRO_SYNTAX_VIOLATION,
                    AvroSdpViolationType.ILLEGAL_STRUCTURE,
                    parseException.getMessage()
            ));
        }

        return mapper.apply(schema);
    }

    private List<SchemaRequirementViolationDto> validateSdpFormat(Schema schema) {
        // Validate core structure: no nesting is done if core structure is invalid.
        final var rootSdpViolations = sdpValidator.validateSchema(schema);
        if (!rootSdpViolations.isEmpty()) {
            return rootSdpViolations;
        }

        // Nested fields of root schema can be validated independently.
        final var systemInfoSchema = schema.getField(SdpSchemaAvroFieldValidator.SYSTEM_INFO_REQUIRED_FIELD_NAME).schema();
        final var systemInfoViolations = systemInfoValidator.validateSchema(systemInfoSchema);

        final var keySchema = schema.getField(SdpSchemaAvroFieldValidator.KEY_REQUIRED_FIELD_NAME).schema();
        final var keyViolations = keyValidator.validateSchema(keySchema);

        final var payloadSchema = schema.getField(SdpSchemaAvroFieldValidator.PAYLOAD_REQUIRED_FIELD_NAME).schema();
        final var payloadViolations = payloadValidator.validateSchema(payloadSchema);
        if (!(systemInfoViolations.isEmpty() && keyViolations.isEmpty() && payloadViolations.isEmpty())) {
            return combineValidationErrors(systemInfoViolations, keyViolations, payloadViolations);
        }

        // Nested fields of system_info are checked last.
        final var timestampSchema = systemInfoSchema.getField(SystemInfoAvroFieldValidator.TIMESTAMP_REQUIRED_FIELD_NAME)
                .schema();
        final var timestampViolations = timestampValidator.validateSchema(timestampSchema);

        final var operationSchema = systemInfoSchema.getField(SystemInfoAvroFieldValidator.OPERATION_REQUIRED_FIELD_NAME)
                .schema();
        final var operationViolations = operationValidator.validateSchema(operationSchema);
        if (!(timestampViolations.isEmpty() && operationViolations.isEmpty())) {
            return combineValidationErrors(timestampViolations, operationViolations);
        }

        return Collections.emptyList();
    }

    private List<SchemaRequirementViolationDto> validateNoKeyChanges(Schema previous, Schema current) {
        final var previousFields = new HashSet<>(previous.getFields());
        final var currentFields = new HashSet<>(current.getFields());
        if (previousFields.containsAll(currentFields) && currentFields.containsAll(previousFields)) {

        }


        return Collections.emptyList();
    }

    @SafeVarargs
    private List<SchemaRequirementViolationDto> combineValidationErrors(List<SchemaRequirementViolationDto>... violations) {
        return Stream.of(violations)
                .flatMap(Collection::stream)
                .toList();
    }
}
