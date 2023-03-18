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
class OperationAvroFieldValidatorTest extends AvroFieldValidatorTestBase {
    private static final String TEST_OPERATIONS_SCHEMAS_SUBFOLDER =
            TEST_SCHEMAS_ROOT_RESOURCE_FOLDER + "operations/";
    private static final String TEST_VALID_SCHEMA =
            TEST_OPERATIONS_SCHEMAS_SUBFOLDER + "validOperation.avro";
    private static final String TEST_VALID_SCHEMA_REORDERED_SYMBOLS =
            TEST_OPERATIONS_SCHEMAS_SUBFOLDER + "validOperationReorderedSymbols.avro";
    private static final String TEST_INVALID_SCHEMA_EXTRA_SYMBOLS =
            TEST_OPERATIONS_SCHEMAS_SUBFOLDER + "invalidOperationExtraSymbols.avro";
    private static final String TEST_INVALID_SCHEMA_MISSING_SYMBOLS =
            TEST_OPERATIONS_SCHEMAS_SUBFOLDER + "invalidOperationMissingSymbols.avro";
    private static final String TEST_INVALID_SCHEMA_TYPE_MISMATCH =
            TEST_OPERATIONS_SCHEMAS_SUBFOLDER + "invalidOperationTypeMismatch.avro";
    @Inject
    OperationAvroFieldValidator operationAvroFieldValidator;

    @ParameterizedTest
    @ValueSource(strings = { TEST_VALID_SCHEMA, TEST_VALID_SCHEMA_REORDERED_SYMBOLS })
    @DisplayName("Test valid operations schema")
    void validOperationsSchemaValidateSuccessTest(String resourceFilePath) {
        var operationsSchema = getSchemaForResourceFile(resourceFilePath);
        var validationErrors = operationAvroFieldValidator.validateSchema(operationsSchema);

        assertTrue(validationErrors.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {TEST_INVALID_SCHEMA_EXTRA_SYMBOLS, TEST_INVALID_SCHEMA_MISSING_SYMBOLS})
    @DisplayName("Test invalid operations schema with incorrect enum values")
    void invalidOperationSchemaExtraSymbolsFailsTest(String resourceFilePath) {
        var operationsSchema = getSchemaForResourceFile(resourceFilePath);
        var validationErrors = operationAvroFieldValidator.validateSchema(operationsSchema);

        assertFalse(validationErrors.isEmpty());
        assertEquals(1, validationErrors.size());

        var error = validationErrors.get(0);
        logger.info(error.description());
        assertEquals(AvroSdpViolationType.ILLEGAL_STRUCTURE, error.sdpCause());
    }

    @ParameterizedTest
    @ValueSource(strings = TEST_INVALID_SCHEMA_TYPE_MISMATCH)
    @DisplayName("Test invalid operations schema with type mismatch")
    void invalidOperationSchemaIncorrectTypeRecordTest(String resourceFilePath) {
        var operationsSchema = getSchemaForResourceFile(resourceFilePath);
        var validationErrors = operationAvroFieldValidator.validateSchema(operationsSchema);

        assertFalse(validationErrors.isEmpty());
        assertEquals(1, validationErrors.size());

        var error = validationErrors.get(0);
        logger.info(error.description());
        assertEquals(AvroSdpViolationType.SCHEMA_TYPE_MISMATCH, error.sdpCause());
    }
}