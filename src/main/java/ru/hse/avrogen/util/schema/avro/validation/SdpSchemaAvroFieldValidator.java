package ru.hse.avrogen.util.schema.avro.validation;

import org.apache.avro.Schema;
import ru.hse.avrogen.dto.SchemaRequirementViolationDto;
import ru.hse.avrogen.util.errors.AvroSdpViolationType;
import ru.hse.avrogen.util.errors.AvroValidatorViolation;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

@ApplicationScoped
public class SdpSchemaAvroFieldValidator extends AvroFieldValidatorBase {
    public static final String SYSTEM_INFO_REQUIRED_FIELD_NAME = "system_info";
    public static final String KEY_REQUIRED_FIELD_NAME = "key";
    public static final String PAYLOAD_REQUIRED_FIELD_NAME = "payload";
    private static final int NAMESPACE_SPLIT_BY_DOT_LENGTH = 2;
    private static final List<String> SDP_SCHEMA_REQUIRED_FIELDS = List.of(
            SYSTEM_INFO_REQUIRED_FIELD_NAME,
            KEY_REQUIRED_FIELD_NAME,
            PAYLOAD_REQUIRED_FIELD_NAME
    );
    private static final List<Schema.Type> ALLOWED_SDP_SCHEMA_TYPES = List.of(
            Schema.Type.RECORD
    );

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
    protected List<SchemaRequirementViolationDto> getSchemaSpecificConstraintViolations(Schema schema) {
        var requirementViolationsList = new ArrayList<SchemaRequirementViolationDto>();

        // Requirement #1: schema doc presence check.
        if (Objects.isNull(schema.getDoc())) {
            requirementViolationsList.add(new SchemaRequirementViolationDto(
                    schema,
                    AvroValidatorViolation.SDP_FORMAT_VIOLATION,
                    AvroSdpViolationType.MISSING_REQUIRED_FIELD,
                    String.format(
                            "Missing %s required field: doc",
                            schema.getFullName()
                    )
            ));
        }

        // Requirement #2: schema namespace presence check.
        final var sdpSchemaNamespace = schema.getNamespace();
        if (Objects.isNull(sdpSchemaNamespace)) {
            requirementViolationsList.add(new SchemaRequirementViolationDto(
                    schema,
                    AvroValidatorViolation.SDP_FORMAT_VIOLATION,
                    AvroSdpViolationType.MISSING_REQUIRED_FIELD,
                    String.format(
                            "Missing %s required field: namespace",
                            schema.getFullName()
                    )
            ));
            return requirementViolationsList;
        }

        // Requirement #3: schema namespace format check: {system}.{domain}
        if (sdpSchemaNamespace.split("\\.").length != NAMESPACE_SPLIT_BY_DOT_LENGTH) {
            requirementViolationsList.add(new SchemaRequirementViolationDto(
                    schema,
                    AvroValidatorViolation.SDP_FORMAT_VIOLATION,
                    AvroSdpViolationType.ILLEGAL_NAMING,
                    String.format(
                            "%s namespace format violation - expected: {system}.{domain}, got: %s",
                            schema.getFullName(),
                            sdpSchemaNamespace
                    )
            ));
        }

        return requirementViolationsList;
    }
}
