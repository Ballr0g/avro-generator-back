package ru.hse.avrogen.client;

import io.smallrye.mutiny.Uni;
import org.apache.avro.Schema;

import java.util.List;

public interface SchemaRegistryClient {
    Uni<List<String>> getSubjects();

    Uni<Void> createSchema(String subjectName, Schema newSchema);
}
