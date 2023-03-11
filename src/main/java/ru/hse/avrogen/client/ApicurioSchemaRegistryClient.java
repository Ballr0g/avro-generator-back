package ru.hse.avrogen.client;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

// Mutiny wrappers provided for Vert.X
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.MultiMap;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.common.NotImplementedYet;
import ru.hse.avrogen.dto.PostSchemaResponseDto;
import ru.hse.avrogen.util.exceptions.ApicurioClientException;

import java.util.Arrays;
import java.util.List;

import static ru.hse.avrogen.constants.registry.ConfluentSchemaRegistryApiConstants.*;

@ApplicationScoped
public class ApicurioSchemaRegistryClient implements SchemaRegistryClient {
    private final Logger logger;
    private final WebClient client;
    private final UriBuilder getSubjectsUriBuilder;
    private final UriBuilder postNewSchemaUriBuilder;

    @Inject
    public ApicurioSchemaRegistryClient(Logger logger, Vertx vertx,
                                        @ConfigProperty(name = "schema.registry.url") String schemaRegistryUrl) {
        this.logger = logger;
        this.client = WebClient.create(vertx);

        getSubjectsUriBuilder = UriBuilder.fromUri(schemaRegistryUrl + SUBJECTS_URL);
        postNewSchemaUriBuilder = UriBuilder.fromUri(schemaRegistryUrl + SCHEMA_CREATION_URL);
    }

    public Uni<List<String>> getSubjects() {
        return client
            .getAbs(getSubjectsUriBuilder.build().toString())
            .send()
            .flatMap(this::mapGetSubjectsResult);
    }

    @Override
    public Uni<List<Integer>> getSchemasBySubject(String subjectName) {
        throw new NotImplementedYet();
    }

    @Override
    public Uni<List<Integer>> getSchemaVersion(String subjectName, Long version) {
        throw new NotImplementedYet();
    }

    public Uni<PostSchemaResponseDto> createSchema(String subjectName, String newSchema) {

        MultiMap form = MultiMap.caseInsensitiveMultiMap();
        form.set("schema", newSchema);

        return client
            .postAbs(postNewSchemaUriBuilder.build(subjectName).toString())
            .sendJson(newSchema)
            .flatMap(this::mapPostSchemaResponse);
    }

    @Override
    public Uni<List<Integer>> deleteSubject(String subjectName) {
        throw new NotImplementedYet();
    }

    @Override
    public Uni<Integer> deleteVersion(String subjectId, Long version) {
        throw new NotImplementedYet();
    }

    private <T> Uni<PostSchemaResponseDto> mapPostSchemaResponse(HttpResponse<? super T> response) {
        logger.info(response.bodyAsString());
        if (response.statusCode() == 200) {
            return Uni.createFrom().item(new PostSchemaResponseDto(response.bodyAsJsonObject().getInteger("id")));
        }

        var message = String.format("Error creating schema: %d, cause by: %s",
                response.statusCode(), response.statusMessage());
        return Uni.createFrom().failure(new ApicurioClientException(message));
    }

    private <T> Uni<List<String>> mapGetSubjectsResult(HttpResponse<? super T> response) {
        if (response.statusCode() == 200) {
            var result = Arrays.asList(response.bodyAsJson(String[].class));
            logger.info("Successfully received subject info from the Schema Registry.");
            return Uni.createFrom().item(result);
        }

        logger.warn("Error: unable to retrieve the subjects from " + getSubjectsUriBuilder.build().toString());
        final var errorMessage = String.format("Request to %s returned %d, cause: %s",
                SUBJECTS_URL, response.statusCode(), response.statusMessage());

        return Uni.createFrom().failure(new ApicurioClientException(errorMessage));
    }
}