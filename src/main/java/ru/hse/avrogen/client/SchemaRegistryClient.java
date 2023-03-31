package ru.hse.avrogen.client;

import io.smallrye.mutiny.Uni;
import ru.hse.avrogen.dto.GetSchemaInfoDto;
import ru.hse.avrogen.dto.PostSchemaResponseDto;
import ru.hse.avrogen.dto.responses.DeleteSchemaVersionResponseDto;
import ru.hse.avrogen.dto.responses.DeleteSubjectResponseDto;
import ru.hse.avrogen.dto.responses.GetSchemaVersionsResponseDto;
import ru.hse.avrogen.dto.responses.GetSubjectsResponseDto;

public interface SchemaRegistryClient {
    // Lists all subject names as strings.
    Uni<GetSubjectsResponseDto> getSubjects();
    Uni<GetSchemaVersionsResponseDto> getSchemaVersionsBySubject(String subjectName);

    Uni<GetSchemaInfoDto> getSchemaByVersion(String subjectName, String version);

    // Returns result for creating a specific version under the subject.
    // Used to differentiate for frontend
    Uni<PostSchemaResponseDto> createSchema(String subjectName, String newSchema);

    // Deletes all the versions of a specified schema.
    Uni<DeleteSubjectResponseDto> deleteSubject(String subjectName);

    // Deletes a specific version within the subject.
    Uni<DeleteSchemaVersionResponseDto> deleteVersion(String subjectId, String version);
}
