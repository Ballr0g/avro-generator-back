package ru.hse.avrogen.util.schema.avro;

import org.apache.avro.Schema;

import java.util.List;

public interface AvroSchemaValidationStrategy {
    boolean tryValidateSchema(Schema schema);

    // Todo: FieldRequirement type for fine-grained requirements and nested requirements (type, fields).
    List<String> getMandatoryFields();
}
