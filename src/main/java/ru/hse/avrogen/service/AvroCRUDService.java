package ru.hse.avrogen.service;

import io.smallrye.mutiny.Uni;
import ru.hse.avrogen.client.ApicurioSchemaRegistryClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class AvroCRUDService {

    @Inject
    ApicurioSchemaRegistryClient apicurioSchemaRegistryClient;

    public Uni<List<String>> getAvroSchemas() {
        return Uni.createFrom().item(List.of("Dummy string result"));
    }

    public Uni<List<String>> getSubjects() {
        return apicurioSchemaRegistryClient.getSubjects();
    }
}
