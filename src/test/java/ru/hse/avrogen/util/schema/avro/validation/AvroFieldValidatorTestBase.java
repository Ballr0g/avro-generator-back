package ru.hse.avrogen.util.schema.avro.validation;

import io.quarkus.test.junit.QuarkusTest;
import org.apache.avro.Schema;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@QuarkusTest
public class AvroFieldValidatorTestBase {
    public static final String TEST_SCHEMAS_ROOT_RESOURCE_FOLDER = "testSchemas/";

    protected Schema.Parser schemaParser;

    @Inject
    protected Logger logger;

    @BeforeEach
    void initSchemaParser() {
        // The parser saves schemas with the same name as "defined", making tests stateful without resets.
        schemaParser = new Schema.Parser();
    }

    protected Schema getSchemaForResourceFile(String testSourceFilePath) {
        var testResource = getClass().getClassLoader().getResource(testSourceFilePath);
        if (Objects.isNull(testResource)) {
            throw new IllegalStateException(String.format("Could not find test resource: %s", testSourceFilePath));
        }

        try {
            var testSourceFile = new File(testResource.getFile());
            return schemaParser.parse(testSourceFile);
        } catch (IOException e) {
            throw new IllegalStateException("An error occurred on parsing Avro schema from file", e);
        }
    }
}
