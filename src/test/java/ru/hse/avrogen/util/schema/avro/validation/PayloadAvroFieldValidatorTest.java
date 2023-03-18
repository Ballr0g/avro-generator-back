package ru.hse.avrogen.util.schema.avro.validation;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.hse.avrogen.util.errors.AvroSdpViolationType;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class PayloadAvroFieldValidatorTest extends AvroFieldValidatorTestBase {
    private static final String TEST_PAYLOAD_SCHEMAS_SUBFOLDER =
            TEST_SCHEMAS_ROOT_RESOURCE_FOLDER + "payloads/";
    private static final String TEST_VALID_SCHEMA_UNION =
            TEST_PAYLOAD_SCHEMAS_SUBFOLDER + "validPayloadUnion.avro";
    private static final String TEST_VALID_SCHEMA_RECORD =
            TEST_PAYLOAD_SCHEMAS_SUBFOLDER + "validPayloadRecord.avro";
    private static final String TEST_INVALID_SCHEMA_UNION_EXTRA_TYPES =
            TEST_PAYLOAD_SCHEMAS_SUBFOLDER + "invalidPayloadUnionExtraTypes.avro";

    @Inject
    PayloadAvroFieldValidator payloadAvroFieldValidator;

    @ParameterizedTest
    @ValueSource(strings = { TEST_VALID_SCHEMA_UNION, TEST_VALID_SCHEMA_RECORD })
    @DisplayName("Test valid payload schema")
    void validPayloadSchemaValidateSuccessTest(String resourceFilePath) {
        var payloadSchema = getSchemaForResourceFile(resourceFilePath);
        var validationErrors = payloadAvroFieldValidator.validateSchema(payloadSchema);

        assertTrue(validationErrors.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = { TEST_INVALID_SCHEMA_UNION_EXTRA_TYPES })
    @DisplayName("Test invalid payload schema with type mismatch")
    void invalidOperationSchemaIncorrectTypeRecordTest(String resourceFilePath) {
        assertSingleSystemInfoFormatViolation(resourceFilePath, AvroSdpViolationType.SCHEMA_TYPE_MISMATCH);
    }

    // Todo: non-trivial type mismatches: enum, multi-valued union

    private void assertSingleSystemInfoFormatViolation(String resourceFilePath, AvroSdpViolationType violationType) {
        assertSingleSdpFormatViolation(payloadAvroFieldValidator, resourceFilePath, violationType);
    }
}