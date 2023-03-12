package ru.hse.avrogen.constants.registry;

public final class ConfluentSchemaRegistryApiConstants {
    public static final String SUBJECTS_URL = "/subjects";
    public static final String SCHEMA_CREATION_URL = "/subjects/{subjectName}/versions";
    public static final String SCHEMA_VERSIONS_URL = SCHEMA_CREATION_URL;
    public static final String SUBJECTS_DELETION_URL = "/subjects/{subjectName}";
    public static final String SCHEMAS_DELETION_URL = "/subjects/{subjectName}/versions/{version}";

    private ConfluentSchemaRegistryApiConstants() {}
}
