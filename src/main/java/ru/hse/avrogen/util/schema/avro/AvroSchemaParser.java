package ru.hse.avrogen.util.schema.avro;

import io.smallrye.mutiny.Uni;
import org.apache.avro.Schema;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AvroSchemaParser {
    private final Schema.Parser avroSchemaParser;

    public AvroSchemaParser() {
        avroSchemaParser = new Schema.Parser();
    }

    public Uni<Schema> parseJsonToAvro(String jsonSchema) {
        Schema schema = avroSchemaParser.parse(jsonSchema);
        return Uni.createFrom().item(schema);
    }
}
