package ru.hse.avrogen.client;

import io.smallrye.mutiny.Uni;
import ru.hse.avrogen.dto.GetSchemaInfoDto;
import ru.hse.avrogen.dto.PostSchemaResponseDto;

import java.util.List;

public interface SchemaRegistryClient {
    // Todo: pack to DTOs.
    // Lists all subject names as strings.
    Uni<List<String>> getSubjects();
    Uni<List<Integer>> getSchemaVersionsBySubject(String subjectName);

    Uni<GetSchemaInfoDto> getSchemaByVersion(String subjectName, String version);

    // Returns result for creating a specific version under the subject.
    // Used to differentiate for frontend
    Uni<PostSchemaResponseDto> createSchema(String subjectName, String newSchema);

    // Deletes all the versions of a specified schema.
    Uni<List<Integer>> deleteSubject(String subjectName);

    // Deletes a specific version within the subject.
    Uni<Integer> deleteVersion(String subjectId, String version);
}
