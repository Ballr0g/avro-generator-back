package ru.hse.avrogen.dto.responses;

import ru.hse.avrogen.dto.SchemaRequirementViolationDto;

import java.util.List;

public record PostIncorrectSchemaResponseDto(List<SchemaRequirementViolationDto> schemaViolations) {
}
