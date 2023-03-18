package ru.hse.avrogen.util.schema.avro.validation.nested;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.hse.avrogen.util.errors.AvroSdpViolationType;
import ru.hse.avrogen.util.schema.avro.validation.AvroFieldValidatorTestBase;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class TimestampAvroFieldValidatorTest extends AvroFieldValidatorTestBase {
    private static final String TEST_TIMESTAMPS_SCHEMAS_SUBFOLDER =
            TEST_SCHEMAS_ROOT_RESOURCE_FOLDER + "timestamps/";
    private static final String TEST_VALID_SCHEMA =
            TEST_TIMESTAMPS_SCHEMAS_SUBFOLDER + "validTimestamp.avro";
    private static final String TEST_INVALID_SCHEMA_PRIMITIVE =
            TEST_TIMESTAMPS_SCHEMAS_SUBFOLDER + "invalidTimestampPrimitive.avro";
    private static final String TEST_INVALID_SCHEMA_RECORD =
            TEST_TIMESTAMPS_SCHEMAS_SUBFOLDER + "invalidTimestampRecord.avro";
    private static final String TEST_INVALID_SCHEMA_LOGICAL_MICROS =
            TEST_TIMESTAMPS_SCHEMAS_SUBFOLDER + "invalidTimestampMicros.avro";
    private static final String TEST_INVALID_SCHEMA_LOGICAL_DECIMAL =
            TEST_TIMESTAMPS_SCHEMAS_SUBFOLDER + "invalidTimestampDecimal.avro";

    @Inject
    TimestampAvroFieldValidator timestampAvroFieldValidator;

    @ParameterizedTest
    @ValueSource(strings = TEST_VALID_SCHEMA)
    @DisplayName("Test valid timestamp schema")
    void validTimestampSchemaValidateSuccessTest(String resourceFilePath) {
        var timestampSchema = getSchemaForResourceFile(resourceFilePath);
        var validationErrors = timestampAvroFieldValidator.validateSchema(timestampSchema);

        assertTrue(validationErrors.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {TEST_INVALID_SCHEMA_PRIMITIVE, TEST_INVALID_SCHEMA_RECORD})
    @DisplayName("Test timestamp not a logicalType")
    void invalidTimestampSchemaNonLogicalTypeFailsTest(String resourceFilePath) {
        assertSingleTimestampFormatViolation(resourceFilePath, AvroSdpViolationType.ILLEGAL_STRUCTURE);
    }

    @ParameterizedTest
    @ValueSource(strings = {TEST_INVALID_SCHEMA_LOGICAL_MICROS, TEST_INVALID_SCHEMA_LOGICAL_DECIMAL})
    @DisplayName("Test timestamp invalid logicalType value")
    void invalidTimestampSchemaIncorrectLogicalTypeFailsTest(String resourceFilePath) {
        assertSingleTimestampFormatViolation(resourceFilePath, AvroSdpViolationType.ILLEGAL_NAMING);
    }

    private void assertSingleTimestampFormatViolation(String resourceFilePath, AvroSdpViolationType violationType) {
        assertSingleSdpFormatViolation(timestampAvroFieldValidator, resourceFilePath, violationType);
    }
}