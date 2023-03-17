package ru.hse.avrogen.util.schema.avro.validation;

import org.apache.avro.Schema;
import ru.hse.avrogen.dto.FieldRequirementsDto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class SdpSchemaAvroFieldValidator extends AvroFieldValidatorBase {
    private static final String SYSTEM_INFO_REQUIRED_FIELD_NAME = "system_info";
    private static final String KEY_REQUIRED_FIELD_NAME = "key";
    private static final String PAYLOAD_REQUIRED_FIELD_NAME = "payload";


    private static final List<String> SDP_SCHEMA_REQUIRED_FIELDS = List.of(
            SYSTEM_INFO_REQUIRED_FIELD_NAME,
            KEY_REQUIRED_FIELD_NAME,
            PAYLOAD_REQUIRED_FIELD_NAME
    );
    private static final List<Schema.Type> ALLOWED_SDP_SCHEMA_TYPES = List.of(
            Schema.Type.RECORD
    );

    // Todo: more meaningful pattern
    private static final Pattern SCHEMA_NAMESPACE_FORMAT = Pattern.compile("^\\d+(\\.\\d+)*$");

    public SdpSchemaAvroFieldValidator() {
        super(SDP_SCHEMA_REQUIRED_FIELDS);
    }

    @Override
    protected Optional<String> getRequiredName() {
        return Optional.empty();
    }

    @Override
    protected List<Schema.Type> getAllowedSchemaTypes() {
        return ALLOWED_SDP_SCHEMA_TYPES;
    }

    @Override
    protected List<FieldRequirementsDto> getSchemaSpecificConstraintViolations(Schema schema) {
        // Requirement #1: schema doc presence check.
        final var requiredSchemaDoc = schema.getDoc();
        if (Objects.isNull(requiredSchemaDoc) || requiredSchemaDoc.isBlank()) {
            // Todo: Add missing doc info.
            return Collections.emptyList();
        }

        // Requirement #2: schema namespace format check: {system}.{domain}
        if (/* !Pattern.matches(SCHEMA_NAMESPACE_FORMAT.pattern(), schema.getName())*/ schema.getName().split("\\.").length != 3) {
            // Todo: append namespace format issue requirement broken info.
            return Collections.emptyList();
        }

        return Collections.emptyList();
    }
}
