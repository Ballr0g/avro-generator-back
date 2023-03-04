package ru.hse.avrogen.client;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

// Mutiny wrappers provided for Vert.X
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.apache.avro.Schema;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import ru.hse.avrogen.util.exceptions.ApicurioClientException;

import java.util.Arrays;
import java.util.List;

import static ru.hse.avrogen.constants.registry.ConfluentSchemaRegistryApiConstants.*;

@ApplicationScoped
public class ApicurioSchemaRegistryClient implements SchemaRegistryClient {
    private final Logger logger;
    private final WebClient client;
    private final UriBuilder apicurioUriBuilder;

    @Inject
    public ApicurioSchemaRegistryClient(Logger logger, Vertx vertx,
                                        @ConfigProperty(name = "schema.registry.url") String schemaRegistryUrl) {
        this.logger = logger;
        this.client = WebClient.create(vertx);

        apicurioUriBuilder = UriBuilder.fromUri(schemaRegistryUrl + "/subjects");
    }

    public Uni<List<String>> getSubjects() {
        // .request - relative URL
        // .requestAbs - absolute path
        // var path = apicurioUriBuilder.build(SUBJECTS_URL).toString();
        return client
            .getAbs(apicurioUriBuilder.build(SUBJECTS_URL).toString())
            .send()
            .flatMap(this::mapGetSubjectsResult);
    }

    public Uni<Void> createSchema(String subjectName, Schema newSchema) {

        return client
            .postAbs(apicurioUriBuilder.build(String.format(SCHEMA_CREATION_URL, subjectName)).toString())
            .sendBuffer(Buffer.buffer(newSchema.toString()))
            .flatMap(this::mapPostSchemaResponse);
    }

    private <T> Uni<Void> mapPostSchemaResponse(HttpResponse<? super T> response) {
        logger.info(response.bodyAsString());
        if (response.statusCode() == 200) {

        }
        return Uni.createFrom().voidItem();
    }

    private <T> Uni<List<String>> mapGetSubjectsResult(HttpResponse<? super T> response) {
        if (response.statusCode() == 200) {
            var result = Arrays.asList(response.bodyAsJson(String[].class));
            logger.info("Successfully received subject info from the Schema Registry.");
            return Uni.createFrom().item(result);
        }

        logger.warn("Error: unable to retrieve the subjects from " + apicurioUriBuilder.build().toString());
        final var errorMessage = String.format("Request to %s returned %d, cause: %s",
                SUBJECTS_URL, response.statusCode(), response.statusMessage());

        return Uni.createFrom().failure(new ApicurioClientException(errorMessage));
    }
}