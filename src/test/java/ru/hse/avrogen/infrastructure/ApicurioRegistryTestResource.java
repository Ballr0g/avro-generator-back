package ru.hse.avrogen.infrastructure;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

public class ApicurioRegistryTestResource implements QuarkusTestResourceLifecycleManager {
    protected GenericContainer<?> apicurioContainer = new GenericContainer<>(DockerImageName
            .parse("apicurio/apicurio-registry-mem:2.4.1.Final"))
            .withExposedPorts(8080);

    @Override
    public Map<String, String> start() {
        apicurioContainer.start();

        Map<String, String> parameters = new HashMap<>();

        parameters.put("schema.registry.url", String.format("http://%s:%s/apis/ccompat/v6",
                apicurioContainer.getHost(), apicurioContainer.getMappedPort(8080)));

        parameters.putAll(additionalParameters());

        return parameters;
    }

    public Map<String, String> additionalParameters() {
        return Map.of();
    }

    @Override
    public void stop() {
        apicurioContainer.stop();
    }
}
