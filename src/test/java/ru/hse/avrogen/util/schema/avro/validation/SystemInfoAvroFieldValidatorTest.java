package ru.hse.avrogen.util.schema.avro.validation;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.hse.avrogen.util.errors.AvroSdpViolationType;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class SystemInfoAvroFieldValidatorTest extends AvroFieldValidatorTestBase {
    private static final String TEST_SYSTEM_INFO_SCHEMAS_SUBFOLDER =
            TEST_SCHEMAS_ROOT_RESOURCE_FOLDER + "system_infos/";
    private static final String TEST_VALID_SCHEMA =
            TEST_SYSTEM_INFO_SCHEMAS_SUBFOLDER + "validSystemInfo.avro";
    private static final String TEST_VALID_SCHEMA_EXTRA_FIELDS =
            TEST_SYSTEM_INFO_SCHEMAS_SUBFOLDER + "validSystemInfoExtraFields.avro";
    private static final String TEST_INVALID_SCHEMA_TYPE_MISMATCH =
            TEST_SYSTEM_INFO_SCHEMAS_SUBFOLDER + "invalidSystemInfoTypeMismatch.avro";
    private static final String TEST_INVALID_SCHEMA_NO_OPERATION =
            TEST_SYSTEM_INFO_SCHEMAS_SUBFOLDER + "invalidSystemInfoNoOperation.avro";
    private static final String TEST_INVALID_SCHEMA_NO_TIMESTAMP =
            TEST_SYSTEM_INFO_SCHEMAS_SUBFOLDER + "invalidSystemInfoNoTimestamp.avro";
    private static final String TEST_INVALID_SCHEMA_NO_REQUIRED_FIELDS =
            TEST_SYSTEM_INFO_SCHEMAS_SUBFOLDER + "invalidSystemInfoNoRequiredFields.avro";

    @Inject
    SystemInfoAvroFieldValidator systemInfoAvroFieldValidator;

    @ParameterizedTest
    @ValueSource(strings = { TEST_VALID_SCHEMA, TEST_VALID_SCHEMA_EXTRA_FIELDS })
    @DisplayName("Test valid system_info schema")
    void validSystemInfoSchemaValidateSuccessTest(String resourceFilePath) {
        var operationsSchema = getSchemaForResourceFile(resourceFilePath);
        var validationErrors = systemInfoAvroFieldValidator.validateSchema(operationsSchema);

        assertTrue(validationErrors.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = TEST_INVALID_SCHEMA_TYPE_MISMATCH)
    @DisplayName("Test invalid operations schema with type mismatch")
    void invalidOperationSchemaIncorrectTypeRecordTest(String resourceFilePath) {
        assertSingleSystemInfoFormatViolation(resourceFilePath, AvroSdpViolationType.SCHEMA_TYPE_MISMATCH);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            TEST_INVALID_SCHEMA_NO_OPERATION,
            TEST_INVALID_SCHEMA_NO_TIMESTAMP,
            TEST_INVALID_SCHEMA_NO_REQUIRED_FIELDS
    })
    @DisplayName("Test system_info with missing required fields")
    void invalidSystemInfoSchemaMissingFieldFailsTest(String resourceFilePath) {
        assertSingleSystemInfoFormatViolation(resourceFilePath, AvroSdpViolationType.MISSING_REQUIRED_FIELD);
    }

    private void assertSingleSystemInfoFormatViolation(String resourceFilePath, AvroSdpViolationType violationType) {
        assertSingleSdpFormatViolation(systemInfoAvroFieldValidator, resourceFilePath, violationType);
    }
}