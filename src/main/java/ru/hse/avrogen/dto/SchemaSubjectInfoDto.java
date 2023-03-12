package ru.hse.avrogen.dto;

// schemaVersion is a String: the specification requires it to be a value in [1,2^31-1] or the string “latest”.
public record SchemaSubjectInfoDto(String subjectName, String schemaVersion) {
}
