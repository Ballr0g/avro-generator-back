package ru.hse.avrogen.util.schema.avro.validation;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.hse.avrogen.util.errors.AvroSdpViolationType;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class KeyAvroFieldValidatorTest extends AvroFieldValidatorTestBase {
    private static final String TEST_KEY_SCHEMAS_SUBFOLDER =
            TEST_SCHEMAS_ROOT_RESOURCE_FOLDER + "keys/";
    private static final String TEST_VALID_SCHEMA_SINGLE_FIELD =
            TEST_KEY_SCHEMAS_SUBFOLDER + "validKeySingleField.avro";
    private static final String TEST_VALID_SCHEMA_MULTIPLE_FIELDS =
            TEST_KEY_SCHEMAS_SUBFOLDER + "validKeyMultipleFields.avro";
    private static final String TEST_INVALID_SCHEMA_TYPE_MISMATCH =
            TEST_KEY_SCHEMAS_SUBFOLDER + "invalidKeyTypeMismatch.avro";
    private static final String TEST_INVALID_SCHEMA_NO_FIELDS =
            TEST_KEY_SCHEMAS_SUBFOLDER + "invalidKeyNoFields.avro";
    private static final String TEST_INVALID_SCHEMA_NON_PRIMITIVE_FIELD =
            TEST_KEY_SCHEMAS_SUBFOLDER + "invalidKeyNonPrimitiveField.avro";
    private static final String TEST_INVALID_SCHEMA_NON_PRIMITIVE_FIELDS =
            TEST_KEY_SCHEMAS_SUBFOLDER + "invalidKeyNonPrimitiveFields.avro";

    @Inject
    KeyAvroFieldValidator keyAvroFieldValidator;

    @ParameterizedTest
    @ValueSource(strings = { TEST_VALID_SCHEMA_SINGLE_FIELD, TEST_VALID_SCHEMA_MULTIPLE_FIELDS })
    @DisplayName("Test valid key schema")
    void validKeySchemaValidateSuccessTest(String resourceFilePath) {
        var keySchema = getSchemaForResourceFile(resourceFilePath);
        var validationErrors = keyAvroFieldValidator.validateSchema(keySchema);

        assertTrue(validationErrors.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = TEST_INVALID_SCHEMA_TYPE_MISMATCH)
    @DisplayName("Test invalid key schema type")
    void invalidKeySchemaTypeMismatchFailureTest(String resourceFilePath) {
        assertSingleSystemInfoFormatViolation(resourceFilePath, AvroSdpViolationType.SCHEMA_TYPE_MISMATCH);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            TEST_INVALID_SCHEMA_NO_FIELDS,
            TEST_INVALID_SCHEMA_NON_PRIMITIVE_FIELD,
            TEST_INVALID_SCHEMA_NON_PRIMITIVE_FIELDS
    })
    @DisplayName("Test invalid key schema fields")
    void invalidKeySchemaNoFieldsFailureTest(String resourceFilePath) {
        assertSingleSystemInfoFormatViolation(resourceFilePath, AvroSdpViolationType.ILLEGAL_STRUCTURE);
    }

    private void assertSingleSystemInfoFormatViolation(String resourceFilePath, AvroSdpViolationType violationType) {
        assertSingleSdpFormatViolation(keyAvroFieldValidator, resourceFilePath, violationType);
    }
}