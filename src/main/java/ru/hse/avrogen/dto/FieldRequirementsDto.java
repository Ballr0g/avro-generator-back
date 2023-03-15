package ru.hse.avrogen.dto;

import org.apache.avro.Schema;

import java.util.List;

public record FieldRequirementsDto(Schema.Type fieldType, String fieldName,
                                   List<String> requiredFieldNames, List<FieldRequirementsDto> nestedRequirements) {
}
