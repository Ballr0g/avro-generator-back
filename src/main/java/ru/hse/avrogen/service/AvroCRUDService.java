package ru.hse.avrogen.service;

import io.smallrye.mutiny.Uni;
import ru.hse.avrogen.client.ApicurioSRClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class AvroCRUDService {

    @Inject
    ApicurioSRClient apicurioSRClient;

    public Uni<List<String>> getAvroSchemas() {
        return Uni.createFrom().item(List.of("Dummy string result"));
    }

    public Uni<List<String>> getSubjects() {
        return apicurioSRClient.getSubjects();
    }
}
