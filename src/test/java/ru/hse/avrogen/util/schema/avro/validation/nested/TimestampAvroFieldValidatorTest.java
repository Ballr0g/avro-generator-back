package ru.hse.avrogen.util.schema.avro.validation.nested;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.hse.avrogen.util.errors.AvroSdpViolationType;
import ru.hse.avrogen.util.errors.AvroValidatorViolation;
import ru.hse.avrogen.util.schema.avro.validation.AvroFieldValidatorTestBase;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class TimestampAvroFieldValidatorTest extends AvroFieldValidatorTestBase {
    private static final String TEST_OPERATIONS_SCHEMAS_SUBFOLDER =
            TEST_SCHEMAS_ROOT_RESOURCE_FOLDER + "timestamps/";
    private static final String TEST_VALID_SCHEMA =
            TEST_OPERATIONS_SCHEMAS_SUBFOLDER + "validTimestamp.avro";
    private static final String TEST_INVALID_SCHEMA_PRIMITIVE =
            TEST_OPERATIONS_SCHEMAS_SUBFOLDER + "invalidTimestampPrimitive.avro";
    private static final String TEST_INVALID_SCHEMA_RECORD =
            TEST_OPERATIONS_SCHEMAS_SUBFOLDER + "invalidTimestampRecord.avro";
    private static final String TEST_INVALID_SCHEMA_LOGICAL_MICROS =
            TEST_OPERATIONS_SCHEMAS_SUBFOLDER + "invalidTimestampMicros.avro";
    private static final String TEST_INVALID_SCHEMA_LOGICAL_DECIMAL =
            TEST_OPERATIONS_SCHEMAS_SUBFOLDER + "invalidTimestampDecimal.avro";

    @Inject
    TimestampAvroFieldValidator timestampAvroFieldValidator;

    @ParameterizedTest
    @ValueSource(strings = TEST_VALID_SCHEMA)
    @DisplayName("Test valid timestamp schema")
    void validTimestampSchemaValidateSuccessTest(String resourceFilePath) {
        var operationsSchema = getSchemaForResourceFile(resourceFilePath);
        var validationErrors = timestampAvroFieldValidator.validateSchema(operationsSchema);

        assertTrue(validationErrors.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {TEST_INVALID_SCHEMA_PRIMITIVE, TEST_INVALID_SCHEMA_RECORD})
    @DisplayName("Test timestamp not a logicalType")
    void invalidTimestampSchemaNonLogicalTypeFailsTest(String resourceFilePath) {
        var operationsSchema = getSchemaForResourceFile(resourceFilePath);
        var validationErrors = timestampAvroFieldValidator.validateSchema(operationsSchema);

        assertFalse(validationErrors.isEmpty());
        assertEquals(1, validationErrors.size());

        var error = validationErrors.get(0);
        logger.info(error.description());
        assertEquals(AvroValidatorViolation.SDP_FORMAT_VIOLATION, error.violationType());
        assertEquals(AvroSdpViolationType.ILLEGAL_STRUCTURE, error.sdpCause());
    }

    @ParameterizedTest
    @ValueSource(strings = {TEST_INVALID_SCHEMA_LOGICAL_MICROS, TEST_INVALID_SCHEMA_LOGICAL_DECIMAL})
    @DisplayName("Test timestamp invalid logicalType value")
    void invalidTimestampSchemaIncorrectLogicalTypeFailsTest(String resourceFilePath) {
        var operationsSchema = getSchemaForResourceFile(resourceFilePath);
        var validationErrors = timestampAvroFieldValidator.validateSchema(operationsSchema);

        assertFalse(validationErrors.isEmpty());
        assertEquals(1, validationErrors.size());

        var error = validationErrors.get(0);
        logger.info(error.description());
        assertEquals(AvroValidatorViolation.SDP_FORMAT_VIOLATION, error.violationType());
        assertEquals(AvroSdpViolationType.ILLEGAL_NAMING, error.sdpCause());
    }
}