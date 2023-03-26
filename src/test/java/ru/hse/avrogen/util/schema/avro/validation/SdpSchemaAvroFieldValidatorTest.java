package ru.hse.avrogen.util.schema.avro.validation;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.hse.avrogen.util.errors.AvroSdpViolationType;
import ru.hse.avrogen.util.errors.AvroValidatorViolation;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class SdpSchemaAvroFieldValidatorTest extends AvroFieldValidatorTestBase {
    private static final String TEST_SDP_SCHEMAS_SUBFOLDER =
            TEST_SCHEMAS_ROOT_RESOURCE_FOLDER + "sdp/";
    private static final String TEST_VALID_SCHEMA =
            TEST_SDP_SCHEMAS_SUBFOLDER + "validSdp.avro";
    private static final String TEST_INVALID_SCHEMA_NO_SYSTEM_INFO =
            TEST_SDP_SCHEMAS_SUBFOLDER + "invalidSdpNoSystemInfo.avro";
    private static final String TEST_INVALID_SCHEMA_NO_KEY =
            TEST_SDP_SCHEMAS_SUBFOLDER + "invalidSdpNoKey.avro";
    private static final String TEST_INVALID_SCHEMA_NO_PAYLOAD =
            TEST_SDP_SCHEMAS_SUBFOLDER + "invalidSdpNoPayload.avro";
    private static final String TEST_INVALID_SCHEMA_NO_DOC =
            TEST_SDP_SCHEMAS_SUBFOLDER + "invalidSdpNoDoc.avro";
    private static final String TEST_INVALID_SCHEMA_NO_NAMESPACE =
            TEST_SDP_SCHEMAS_SUBFOLDER + "invalidSdpNoNamespace.avro";
    private static final String TEST_INVALID_SCHEMA_NAMESPACE_LONG =
            TEST_SDP_SCHEMAS_SUBFOLDER + "invalidSdpNamespaceTooLong.avro";
    private static final String TEST_INVALID_SCHEMA_NAMESPACE_SHORT =
            TEST_SDP_SCHEMAS_SUBFOLDER + "invalidSdpNamespaceTooShort.avro";

    @Inject
    SdpSchemaAvroFieldValidator sdpSchemaAvroFieldValidator;

    @Test
    @DisplayName("Test null schema for validation")
    void nullSchemaFailure() {
        var validationErrors = sdpSchemaAvroFieldValidator.validateSchema(null);
        assertFalse(validationErrors.isEmpty());
        assertEquals(1, validationErrors.size());

        var error = validationErrors.get(0);
        logger.info(error.description());
        assertEquals(AvroValidatorViolation.AVRO_SYNTAX_VIOLATION, error.violationType());
        assertEquals(AvroSdpViolationType.ILLEGAL_STRUCTURE, error.sdpCause());
    }

    @ParameterizedTest
    @ValueSource(strings = {TEST_VALID_SCHEMA})
    @DisplayName("Test valid sdp schema")
    void validSdpSchemaValidateSuccessTest(String resourceFilePath) {
        var sdpSchema = getSchemaForResourceFile(resourceFilePath);
        var validationErrors = sdpSchemaAvroFieldValidator.validateSchema(sdpSchema);

        assertTrue(validationErrors.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            TEST_INVALID_SCHEMA_NO_SYSTEM_INFO,
            TEST_INVALID_SCHEMA_NO_KEY,
            TEST_INVALID_SCHEMA_NO_PAYLOAD,
            TEST_INVALID_SCHEMA_NO_NAMESPACE,
            TEST_INVALID_SCHEMA_NO_DOC
    })
    @DisplayName("Test sdp schema with missing required fields")
    void invalidSdpSchemaMissingFieldFailsTest(String resourceFilePath) {
        assertSingleSdpFormatViolation(resourceFilePath, AvroSdpViolationType.MISSING_REQUIRED_FIELD);
    }

    @ParameterizedTest
    @ValueSource(strings = { TEST_INVALID_SCHEMA_NAMESPACE_LONG, TEST_INVALID_SCHEMA_NAMESPACE_SHORT })
    @DisplayName("Test sdp schema incorrect namespace format")
    void invalidSdpSchemaIncorrectNamespaceFailsTest(String resourceFilePath) {
        assertSingleSdpFormatViolation(resourceFilePath, AvroSdpViolationType.ILLEGAL_NAMING);
    }

    private void assertSingleSdpFormatViolation(String resourceFilePath, AvroSdpViolationType violationType) {
        assertSingleSdpFormatViolation(sdpSchemaAvroFieldValidator, resourceFilePath, violationType);
    }
}