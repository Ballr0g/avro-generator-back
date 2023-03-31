package ru.hse.avrogen.client;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

// Mutiny wrappers provided for Vert.X
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import ru.hse.avrogen.dto.GetSchemaInfoDto;
import ru.hse.avrogen.dto.PostSchemaResponseDto;
import ru.hse.avrogen.dto.responses.DeleteSchemaVersionResponseDto;
import ru.hse.avrogen.dto.responses.DeleteSubjectResponseDto;
import ru.hse.avrogen.dto.responses.GetSchemaVersionsResponseDto;
import ru.hse.avrogen.dto.responses.GetSubjectsResponseDto;
import ru.hse.avrogen.util.enums.SchemaPresence;
import ru.hse.avrogen.util.exceptions.ApicurioClientException;
import ru.hse.avrogen.util.exceptions.SchemaVersionNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static ru.hse.avrogen.constants.registry.ConfluentSchemaRegistryApiConstants.*;

@ApplicationScoped
public class ApicurioSchemaRegistryClient implements SchemaRegistryClient {
    private final Logger logger;
    private final WebClient client;
    private final UriBuilder getSubjectsUriBuilder;
    private final UriBuilder getSchemasBySubjectUriBuilder;
    private final UriBuilder getSchemaOfVersionUriBuilder;
    private final UriBuilder postNewSchemaUriBuilder;
    private final UriBuilder deleteSubjectUriBuilder;
    private final UriBuilder deleteSchemaUriBuilder;

    @Inject
    public ApicurioSchemaRegistryClient(Logger logger, Vertx vertx,
                                        @ConfigProperty(name = "schema.registry.url") String schemaRegistryUrl) {
        this.logger = logger;
        this.client = WebClient.create(vertx);

        getSubjectsUriBuilder = UriBuilder.fromUri(schemaRegistryUrl + SUBJECTS_URL);
        getSchemasBySubjectUriBuilder = UriBuilder.fromUri(schemaRegistryUrl + SCHEMA_VERSIONS_URL);
        getSchemaOfVersionUriBuilder = UriBuilder.fromUri(schemaRegistryUrl + SCHEMA_GET_URL);
        postNewSchemaUriBuilder = UriBuilder.fromUri(schemaRegistryUrl + SCHEMA_CREATION_URL);
        deleteSubjectUriBuilder = UriBuilder.fromUri(schemaRegistryUrl + SUBJECTS_DELETION_URL);
        deleteSchemaUriBuilder = UriBuilder.fromUri(schemaRegistryUrl + SCHEMAS_DELETION_URL);
    }

    @Override
    public Uni<GetSubjectsResponseDto> getSubjects() {
        return client
                .getAbs(getSubjectsUriBuilder.build().toString())
                .send()
                .flatMap(this::mapGetSubjectsResult);
    }

    @Override
    public Uni<GetSchemaVersionsResponseDto> getSchemaVersionsBySubject(String subjectName) {
        return client
                .getAbs(getSchemasBySubjectUriBuilder.build(subjectName).toString())
                .send()
                .flatMap(response -> mapGetSchemaVersionsResult(response, subjectName));
    }

    @Override
    public Uni<GetSchemaInfoDto> getSchemaByVersion(String subjectName, String version) {
        return client
                .getAbs(getSchemaOfVersionUriBuilder.build(subjectName, version).toString())
                .send()
                .flatMap(response -> mapGetSchemaResult(response, subjectName, version));
    }

    @Override
    public Uni<PostSchemaResponseDto> createSchema(String subjectName, String newSchema) {
        return client
                .postAbs(postNewSchemaUriBuilder.build(subjectName).toString())
                .sendJson(newSchema)
                .flatMap(response -> mapPostSchemaResponse(response, subjectName));
    }

    @Override
    public Uni<DeleteSubjectResponseDto> deleteSubject(String subjectName) {
        return client
                .deleteAbs(deleteSubjectUriBuilder.build(subjectName).toString())
                .send()
                .flatMap(response -> mapDeleteSubjectResult(response, subjectName));
    }

    @Override
    public Uni<DeleteSchemaVersionResponseDto> deleteVersion(String subjectId, String version) {
        return client
                .deleteAbs(deleteSchemaUriBuilder.build(subjectId, version).toString())
                .send()
                .flatMap(response -> mapDeleteSchemaResult(response, subjectId, version));
    }

    private <T> Uni<PostSchemaResponseDto> mapPostSchemaResponse(HttpResponse<? super T> response, String subjectName) {
        if (response.statusCode() == 200) {
            final var creationResultDto = new PostSchemaResponseDto(response.bodyAsJsonObject().getInteger("id"));
            logger.info(String.format("Successfully created schema with id %d under subject %s",
                    creationResultDto.id(), subjectName));
            return Uni.createFrom().item(creationResultDto);
        }

        final var errorMessage = String.format("Error creating schema: %d, caused by: %s",
                response.statusCode(), response.statusMessage());
        return ApicurioClientException.createUni500ClientException(getRegistryResponseErrorCode(response), errorMessage);
    }

    private <T> Uni<GetSubjectsResponseDto> mapGetSubjectsResult(HttpResponse<? super T> response) {
        if (response.statusCode() == 200) {
            final var result = Arrays.asList(response.bodyAsJson(String[].class));
            logger.info("Successfully received subject info from the Schema Registry.");
            return Uni.createFrom().item(new GetSubjectsResponseDto(result));
        }

        logger.warn("Error: unable to retrieve the subjects from " + getSubjectsUriBuilder.build().toString());
        final var errorMessage = String.format("Request to %s returned %d, cause: %s",
                SUBJECTS_URL, response.statusCode(), response.statusMessage());

        return ApicurioClientException.createUni500ClientException(getRegistryResponseErrorCode(response), errorMessage);
    }

    private <T> Uni<GetSchemaVersionsResponseDto> mapGetSchemaVersionsResult(HttpResponse<? super T> response, String subjectName) {
        if (response.statusCode() == 200) {
            final var result = Arrays.asList(response.bodyAsJson(Integer[].class));
            logger.info(String.format("Successfully received versions for subject %s", subjectName));
            return Uni.createFrom().item(new GetSchemaVersionsResponseDto(result));
        }

        if (response.statusCode() == 404) {
            logger.info(String.format("No versions found, subject %s does not exist", subjectName));
            return Uni.createFrom().item(new GetSchemaVersionsResponseDto(Collections.emptyList()));
        }

        logger.warn("Error: unable to retrieve schema versions from "
                + getSchemasBySubjectUriBuilder.build(subjectName).toString());
        final var errorMessage = String.format("Error on retrieving schema versions: %d, cause by: %s",
                response.statusCode(), response.statusMessage());
        return ApicurioClientException.createUni500ClientException(getRegistryResponseErrorCode(response), errorMessage);
    }

    private <T> Uni<GetSchemaInfoDto> mapGetSchemaResult(HttpResponse<? super T> response,
                                                              String subjectName, String schemaVersion) {
        if (response.statusCode() == 200) {
            final var getSchemaResponseJson = response.bodyAsJsonObject();
            final var getSchemaResponse = new GetSchemaInfoDto(
                    getSchemaResponseJson.getInteger("id"),
                    getSchemaResponseJson.getString("subject"),
                    getSchemaResponseJson.getInteger("version"),
                    getSchemaResponseJson.getString("schema"),
                    Collections.emptyList()
            );
            logger.info(String.format("Successfully received schema version %s for subject %s",
                    schemaVersion, subjectName));
            return Uni.createFrom().item(getSchemaResponse);
        }

        if (response.statusCode() == 404) {
            logger.info(String.format("Schema version %s under subject %s does not exist", schemaVersion, subjectName));
            final var errorCode = getRegistryResponseErrorCode(response);
            return createFailedUniMissingSchema(response, errorCode);
        }

        logger.warn("Error: unable to get schema for request "
                + getSchemaOfVersionUriBuilder.build(subjectName, schemaVersion).toString());
        final var errorMessage = String.format("Error on retrieving schema version: %d, caused by: %s",
                response.statusCode(), response.statusMessage());
        return ApicurioClientException.createUni500ClientException(getRegistryResponseErrorCode(response), errorMessage);
    }

    private <T> Uni<DeleteSubjectResponseDto> mapDeleteSubjectResult(HttpResponse<? super T> response, String subjectName) {
        if (response.statusCode() == 200) {
            final var result = Arrays.asList(response.bodyAsJson(Integer[].class));
            logger.info(String.format("Successfully deleted subject %s", subjectName));
            return Uni.createFrom().item(new DeleteSubjectResponseDto(SchemaPresence.PRESENT, result));
        }

        if (response.statusCode() == 404) {
            logger.info(String.format("Unable to delete subject %s because it does not exist", subjectName));
            final var errorCode = getRegistryResponseErrorCode(response);
            return createFailedUniMissingSchema(response, errorCode);
        }

        logger.warn("Error: unable to delete subject by calling "
                + deleteSubjectUriBuilder.build(subjectName).toString());
        final var errorMessage = String.format("Error on deleting subject: %d, caused by: %s",
                response.statusCode(), response.statusMessage());
        return ApicurioClientException.createUni500ClientException(getRegistryResponseErrorCode(response), errorMessage);
    }

    private <T> Uni<DeleteSchemaVersionResponseDto> mapDeleteSchemaResult(HttpResponse<? super T> response,
                                                         String subjectName, String schemaVersion) {
        if (response.statusCode() == 200) {
            final var deletedSchemaVersion = response.bodyAsJson(Integer.class);
            logger.info(String.format("Successfully deleted schema version %d", deletedSchemaVersion));
            return Uni.createFrom().item(new DeleteSchemaVersionResponseDto(deletedSchemaVersion));
        }

        if (response.statusCode() == 404) {
            logger.info(String.format("Deletion failed: schema version %s under subject %s does not exist",
                    schemaVersion, subjectName));
            final var errorCode = getRegistryResponseErrorCode(response);
            return createFailedUniMissingSchema(response, errorCode);
        }

        logger.warn("Error: unable to delete schema by calling "
                + deleteSchemaUriBuilder.build(subjectName, schemaVersion).toString());
        final var errorMessage = String.format("Error on deleting schema version: %d, caused by: %s",
                response.statusCode(), response.statusMessage());
        return ApicurioClientException.createUni500ClientException(getRegistryResponseErrorCode(response), errorMessage);
    }

    private static <T, R> Uni<T> createFailedUniMissingSchema(HttpResponse<? super R> response, int errorCode) {
        return Uni.createFrom().failure(new SchemaVersionNotFoundException(
                Response.Status.NOT_FOUND,
                mapErrorCodeToSchemaPresence(errorCode),
                errorCode,
                response.statusMessage()
        ));
    }

    private static <T> int getRegistryResponseErrorCode(HttpResponse<? super T> response) {
        final var responseJson = response.bodyAsJsonObject();
        if (Objects.isNull(responseJson)) {
            return 500;
        }

        return Objects.requireNonNullElse(responseJson.getInteger("error_code"), 500);
    }

    private static SchemaPresence mapErrorCodeToSchemaPresence(int errorCode) {
        return switch (errorCode) {
            case 200 -> SchemaPresence.PRESENT;
            case 40401 -> SchemaPresence.NO_SUBJECT;
            case 40402 -> SchemaPresence.NO_VERSION;
            default -> SchemaPresence.UNEXPECTED;
        };
    }
}