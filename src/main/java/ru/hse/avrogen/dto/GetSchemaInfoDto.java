package ru.hse.avrogen.dto;

import java.util.List;

public record GetSchemaInfoDto(int id, String subject, int version, String schema, List<String> references) {
}
