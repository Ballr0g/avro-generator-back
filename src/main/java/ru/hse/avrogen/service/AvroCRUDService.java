package ru.hse.avrogen.service;

import io.smallrye.mutiny.Uni;
import ru.hse.avrogen.client.SchemaRegistryClient;
import ru.hse.avrogen.dto.GetSchemaInfoDto;
import ru.hse.avrogen.dto.PostSchemaResponseDto;
import ru.hse.avrogen.util.exceptions.validation.AvroGeneratorCheckerException;
import ru.hse.avrogen.util.schema.avro.AvroSdpSchemaChecker;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class AvroCRUDService {

    @Inject
    SchemaRegistryClient apicurioSchemaRegistryClient;
    @Inject
    AvroSdpSchemaChecker schemaChecker;

    public Uni<GetSchemaInfoDto> getAvroSchemas(String subjectName, String schemaVersion) {
        return apicurioSchemaRegistryClient.getSchemaByVersion(subjectName, schemaVersion);
    }

    public Uni<List<String>> getSubjects() {
        return apicurioSchemaRegistryClient.getSubjects();
    }

    public Uni<List<Integer>> getSchemasBySubject(String subjectName) {
        return apicurioSchemaRegistryClient.getSchemaVersionsBySubject(subjectName);
    }

    public Uni<PostSchemaResponseDto> createSchema(String subjectName, String schema) {
        final var schemaFormatViolations = schemaChecker.validateSchema(schema);
        if (!schemaFormatViolations.isEmpty()) {
            return Uni.createFrom().failure(new AvroGeneratorCheckerException(
                    schemaFormatViolations,
                    "Incorrect schema format"
            ));
        }

        // SchemaDiscoveryService: check if schema already exists.
        return apicurioSchemaRegistryClient.createSchema(subjectName, schema);
    }

    public Uni<List<Integer>> deleteSubject(String subjectName) {
        return apicurioSchemaRegistryClient.deleteSubject(subjectName);
    }

    public Uni<Integer> deleteSchemaVersion(String subjectName, String schemaVersion) {
        // Validator: check schema version format.
        return apicurioSchemaRegistryClient.deleteVersion(subjectName, schemaVersion);
    }
}
