package ru.hse.avrogen.client;

import io.smallrye.mutiny.Uni;
import ru.hse.avrogen.dto.PostSchemaResponseDto;

import java.util.List;

public interface SchemaRegistryClient {
    // Lists all subject names as strings.
    Uni<List<String>> getSubjects();
    Uni<List<Integer>> getSchemaVersionsBySubject(String subjectName);

    Uni<List<Integer>> getSchemaVersion(String subjectName, Long version);

    // Returns result for creating a specific version under the subject.
    // Used to differenciate for frontend
    Uni<PostSchemaResponseDto> createSchema(String subjectName, String newSchema);

    // Deletes all the versions of a specified schema.
    Uni<List<Integer>> deleteSubject(String subjectName);

    // Deletes a specific version within the subject.
    Uni<Integer> deleteVersion(String subjectId, Long version);
}
