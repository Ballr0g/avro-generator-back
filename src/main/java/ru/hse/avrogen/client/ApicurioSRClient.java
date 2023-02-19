package ru.hse.avrogen.client;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

// Mutiny wrappers provided for Vert.X
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class ApicurioSRClient {
    private final Logger logger;
    private final WebClient client;
    private final UriBuilder getSubjects;


    @Inject
    public ApicurioSRClient(Logger logger, Vertx vertx,
                            @ConfigProperty(name = "schema.registry.url") String apicurioPath) {
        this.logger = logger;
        this.client = WebClient.create(vertx);

        getSubjects = UriBuilder.fromUri(apicurioPath + "/subjects");
    }

    public Uni<List<String>> getSubjects() {
        // .request - relative URL
        // .requestAbs - absolute path
        return client
            .requestAbs(HttpMethod.GET, getSubjects.build().toString())
            .send()
            .flatMap(httpResponse -> {
                if (httpResponse.statusCode() == 200) {
                    var result = Arrays.asList(httpResponse.bodyAsJson(String[].class));
                    return Uni.createFrom().item(result);
                } else {
                    return Uni.createFrom().failure(new RuntimeException("Error"));
                }
            });
    }
}