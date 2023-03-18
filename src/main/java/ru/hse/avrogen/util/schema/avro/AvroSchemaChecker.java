package ru.hse.avrogen.util.schema.avro;

import org.apache.avro.Schema;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AvroSchemaChecker {
//    @Inject
//    AvroSchemaValidationStrategy schemaValidationStrategy;

    public boolean tryParseJsonToAvro(String jsonSchema) {
        // Todo: strategy-based validation approach.
        var avroSchemaParser = new Schema.Parser();
        var schema = avroSchemaParser.parse(jsonSchema);
        return schemaContainsKey(schema);
    }

    public boolean schemaContainsKey(Schema schema) {
        try {
            var field = schema.getField("key");
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
