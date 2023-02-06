package ru.hse.avrogen.service;

import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class AvroCRUDService {
    public Uni<List<String>> getAvroSchemas() {
        return Uni.createFrom().item(List.of("Dummy string result"));
    }
}
