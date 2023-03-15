package ru.hse.avrogen.util.schema.avro.validators;

import org.apache.avro.Schema;
import ru.hse.avrogen.dto.FieldRequirementsDto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class SdpSchemaAvroFieldValidator extends AvroFieldValidatorBase {
    // The "name" field is not included since it is mandatory by avro specification and is handled by parser.
    private static final List<String> SDP_SCHEMA_REQUIRED_FIELDS = List.of("namespace", "doc");

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
    protected Optional<Schema.Type> getRequiredSchemaType() {
        return Optional.of(Schema.Type.RECORD);
    }

    @Override
    protected List<FieldRequirementsDto> getFieldSpecificConstraintViolations(Schema schema) {
        // Todo: namespace check.
        if (!Pattern.matches(SCHEMA_NAMESPACE_FORMAT.pattern(), schema.getName())) {
            // Todo: append namespace format issue requirement broken info.
            return Collections.emptyList();
        }

        return Collections.emptyList();
    }
}
