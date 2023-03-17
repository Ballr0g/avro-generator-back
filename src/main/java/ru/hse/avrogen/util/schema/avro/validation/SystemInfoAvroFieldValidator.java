package ru.hse.avrogen.util.schema.avro.validation;

import org.apache.avro.Schema;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SystemInfoAvroFieldValidator extends AvroFieldValidatorBase {
    private static final String TIMESTAMP_FIELD_NAME = "timestamp";
    private static final String OPERATION_FIELD_NAME = "operation";

    private static final List<String> SYSTEM_INFO_SCHEMA_REQUIRED_FIELDS = List.of(
            TIMESTAMP_FIELD_NAME,
            OPERATION_FIELD_NAME
    );
    private static final List<Schema.Type> ALLOWED_SYSTEM_INFO_SCHEMA_TYPES = List.of(
            Schema.Type.RECORD
    );

    public SystemInfoAvroFieldValidator() {
        super(SYSTEM_INFO_SCHEMA_REQUIRED_FIELDS);
    }

    @Override
    protected Optional<String> getRequiredName() {
        return Optional.empty();
    }

    @Override
    protected List<Schema.Type> getAllowedSchemaTypes() {
        return ALLOWED_SYSTEM_INFO_SCHEMA_TYPES;
    }
}
