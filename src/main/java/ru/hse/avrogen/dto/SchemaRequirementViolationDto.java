package ru.hse.avrogen.dto;

import org.apache.avro.Schema;
import ru.hse.avrogen.util.errors.AvroSdpViolationType;
import ru.hse.avrogen.util.errors.AvroValidatorViolation;

public record SchemaRequirementViolationDto(Schema incorrectSchema,
                                            AvroValidatorViolation violationType,
                                            AvroSdpViolationType sdpCause,
                                            String description) {

}
