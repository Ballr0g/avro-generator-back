package ru.hse.avrogen.dto.responses;

import java.util.List;

public record GetSchemaVersionsResponseDto(List<Integer> schemaVersions) {
}
