package ru.hse.avrogen.service;

import io.smallrye.mutiny.Uni;
import ru.hse.avrogen.client.SchemaRegistryClient;
import ru.hse.avrogen.dto.PostSchemaResponseDto;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class AvroCRUDService {

    @Inject
    SchemaRegistryClient apicurioSchemaRegistryClient;

    public Uni<List<String>> getAvroSchemas() {
        return Uni.createFrom().item(List.of("Dummy string result"));
    }

    public Uni<List<String>> getSubjects() {
        return apicurioSchemaRegistryClient.getSubjects();
    }

    public Uni<List<Integer>> getSchemasBySubject(String subjectName) {
        return apicurioSchemaRegistryClient.getSchemaVersionsBySubject(subjectName);
    }

    public Uni<PostSchemaResponseDto> createSchema(String subjectName, String schema) {
        // Parser: parse schema format.
        return apicurioSchemaRegistryClient.createSchema(subjectName, schema);
    }

    public Uni<List<Integer>> deleteSubject(String subjectName) {
        return apicurioSchemaRegistryClient.deleteSubject(subjectName);
    }
}
