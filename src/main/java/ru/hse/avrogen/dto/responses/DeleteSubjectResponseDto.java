package ru.hse.avrogen.dto.responses;

import ru.hse.avrogen.util.enums.SchemaPresence;

import java.util.List;

public record DeleteSubjectResponseDto(SchemaPresence schemaPresence, List<Integer> deletedVersions) {
}
