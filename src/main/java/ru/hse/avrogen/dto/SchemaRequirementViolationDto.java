package ru.hse.avrogen.dto;

import ru.hse.avrogen.util.errors.AvroSdpViolationType;
import ru.hse.avrogen.util.errors.AvroValidatorViolation;

public record SchemaRequirementViolationDto(String incorrectSchema,
                                            AvroValidatorViolation violationType,
                                            AvroSdpViolationType sdpCause,
                                            String description) {

}
