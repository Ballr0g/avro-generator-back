package ru.hse.avrogen.component;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hse.avrogen.dto.GetSchemaInfoDto;
import ru.hse.avrogen.dto.PostSchemaResponseDto;
import ru.hse.avrogen.dto.responses.*;
import ru.hse.avrogen.infrastructure.ApicurioRegistryTestResource;
import ru.hse.avrogen.util.enums.SchemaPresence;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import java.util.Collections;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(value = ApicurioRegistryTestResource.class, restrictToAnnotatedClass = true)
public class ApicurioClientComponentTest {
    private final String TEST_CORRECT_INPUT_SCHEMA = "{\"type\":\"record\",\"name\":\"onecolumn\",\"namespace\":\"system.domain\",\"doc\":\"testSDPsubject\",\"fields\":[{\"name\":\"system_info\",\"type\":{\"type\":\"record\",\"name\":\"system_record\",\"fields\":[{\"name\":\"timestamp\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}},{\"name\":\"operation\",\"type\":{\"type\":\"enum\",\"name\":\"operations\",\"symbols\":[\"I\",\"U\",\"D\"]}}]}},{\"name\":\"key\",\"type\":{\"type\":\"record\",\"name\":\"key_record\",\"fields\":[{\"name\":\"id\",\"type\":\"int\",\"doc\":\"unique id\",\"comment\":\"unique id\"}]}},{\"name\":\"payload\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"payload_record\",\"fields\":[{\"name\":\"val\",\"type\":\"string\"}]}]}]}";

    private static final String TEST_SUBJECT_NAME = "666";
    private static final String TEST_SCHEMA_VERSION = "latest";
    private static final String TEST_SCHEMA_VERSION_NON_EXISTENT = "100";

    @BeforeEach
    void ClearTestContainer() {
        delete(String.format("/avroGenerator/v1/deleteSubject/%s", TEST_SUBJECT_NAME));
    }


    @Test
    public void testGetSubjectsEndpoint() {
        var getResponse = given()
                .when()
                .get("/avroGenerator/v1/getSubjects")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .response()
                .as(GetSubjectsResponseDto.class);

        assertNotNull(getResponse.subjects());
        assertTrue(getResponse.subjects().isEmpty());
    }

    @Test
    void testPostNewCorrectSchema() {
        JsonObject testInput = new JsonObject();
        testInput.put("subjectName", TEST_SUBJECT_NAME);
        testInput.put("schema", TEST_CORRECT_INPUT_SCHEMA);
        given()
                .when()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(testInput.toString())
                .post("/avroGenerator/v1/createSchema")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .response()
                .as(PostSchemaResponseDto.class);
    }

    @Test
    void testPostNewInvalidFormatSchema() {
        JsonObject testInput = new JsonObject();
        testInput.put("subjectName", TEST_SUBJECT_NAME);
        testInput.put("schema", "{\"type\":\"record\",\"name\":\"onecolumn\",\"namespace\":\"system.domain\",\"doc\":\"test SDP subject\",\"fields\":[{\"name\":\"system_info\",\"type\":{\"type\":\"record\",\"name\":\"system_record\",\"fields\":[{\"name\":\"timestamp\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}}]}},{\"name\":\"key\",\"type\":{\"type\":\"record\",\"name\":\"key_record\",\"fields\":[{\"name\":\"id\",\"type\":\"int\",\"doc\":\"unique id\",\"comment\":\"unique id\"}]}},{\"name\":\"payload\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"payload_record\",\"fields\":[{\"name\":\"val\",\"type\":\"string\"}]}]}]}");

        var postResult = given()
                .when()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(testInput.toString())
                .post("/avroGenerator/v1/createSchema")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .extract()
                .response()
                .as(PostIncorrectSchemaResponseDto.class);

        var violations = postResult.schemaViolations();
        assertFalse(violations.isEmpty());
        assertEquals("Missing system.domain.system_record required fields: operation",
                violations.get(0).description()
        );
    }

    @Test
    void testGetSchemaNoSubject() {
        var getResponse = given()
                .when()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .get(String.format("/avroGenerator/v1/getSchema/%s/%s", TEST_SUBJECT_NAME, TEST_SCHEMA_VERSION))
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .extract()
                .response()
                .as(MissingSchemaResponseDto.class);

        assertEquals(40401, getResponse.errorCode());
        assertEquals(SchemaPresence.NO_SUBJECT, getResponse.schemaPresence());
        assertEquals(Response.Status.NOT_FOUND, getResponse.statusCode());
    }

    @Test
    void testGetSchemaNoSchema() {
        postSchema(TEST_SUBJECT_NAME, TEST_CORRECT_INPUT_SCHEMA);

        var getResponse = given()
                .when()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .get(String.format("/avroGenerator/v1/getSchema/%s/%s", TEST_SUBJECT_NAME, TEST_SCHEMA_VERSION_NON_EXISTENT))
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .extract()
                .response()
                .as(MissingSchemaResponseDto.class);

        assertEquals(40402, getResponse.errorCode());
        assertEquals(SchemaPresence.NO_VERSION, getResponse.schemaPresence());
        assertEquals(Response.Status.NOT_FOUND, getResponse.statusCode());
    }

    @Test
    void testGetSchemaSuccess() {
        postSchema(TEST_SUBJECT_NAME, TEST_CORRECT_INPUT_SCHEMA);

        var getResponse = given()
                .when()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .get(String.format("/avroGenerator/v1/getSchema/%s/%s", TEST_SUBJECT_NAME, TEST_SCHEMA_VERSION))
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .response()
                .as(GetSchemaInfoDto.class);

        assertEquals(TEST_SUBJECT_NAME, getResponse.subject());
        assertEquals(TEST_CORRECT_INPUT_SCHEMA, getResponse.schema());
        assertEquals(Collections.emptyList(), getResponse.references());
    }

    @Test
    void testGetSubjectVersionsSuccess() {
        postSchema(TEST_SUBJECT_NAME, TEST_CORRECT_INPUT_SCHEMA);

        var getResponse = given()
                .when()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .get(String.format("/avroGenerator/v1/getSchemaVersions/%s", TEST_SUBJECT_NAME))
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .response()
                .as(GetSchemaVersionsResponseDto.class);

        final var versions = getResponse.schemaVersions();
        assertNotNull(versions);
        assertFalse(versions.isEmpty());
    }

    @Test
    void testGetSubjectVersionsNoSubject() {
        var getResponse = given()
                .when()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .get(String.format("/avroGenerator/v1/getSchemaVersions/%s", TEST_SUBJECT_NAME))
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .response()
                .as(GetSchemaVersionsResponseDto.class);

        var versions = getResponse.schemaVersions();
        assertNotNull(versions);
        assertTrue(versions.isEmpty());
    }

    @Test
    void testDeleteSubjectMissingSubject() {
        var deleteResponse = given()
                .when()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .delete(String.format("/avroGenerator/v1/deleteSubject/%s", TEST_SUBJECT_NAME))
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .extract()
                .response()
                .as(MissingSchemaResponseDto.class);

        assertEquals(40401, deleteResponse.errorCode());
        assertEquals(SchemaPresence.NO_SUBJECT, deleteResponse.schemaPresence());
        assertEquals(Response.Status.NOT_FOUND, deleteResponse.statusCode());
    }

    @Test
    void testDeleteSubjectSuccess() {
        postSchema(TEST_SUBJECT_NAME, TEST_CORRECT_INPUT_SCHEMA);

        var deleteResponse = given()
                .when()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .delete(String.format("/avroGenerator/v1/deleteSubject/%s", TEST_SUBJECT_NAME))
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .response()
                .as(DeleteSubjectResponseDto.class);

        var deletedVersions = deleteResponse.deletedVersions();
        assertEquals(SchemaPresence.PRESENT, deleteResponse.schemaPresence());
        assertNotNull(deletedVersions);
        assertFalse(deletedVersions.isEmpty());
    }

    @Test
    void testDeleteSchemaSuccess() {
        postSchema(TEST_SUBJECT_NAME, TEST_CORRECT_INPUT_SCHEMA);

        var deleteResponse = given()
                .when()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .delete(String.format("/avroGenerator/v1/deleteSchema/%s/%s", TEST_SUBJECT_NAME, TEST_SCHEMA_VERSION))
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .response()
                .as(DeleteSchemaVersionResponseDto.class);

        assertNotNull(deleteResponse.deletedVersion());
    }

    @Test
    void testDeleteSchemaMissingSubject() {
        var deleteResponse = given()
                .when()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .delete(String.format("/avroGenerator/v1/deleteSchema/%s/%s", TEST_SUBJECT_NAME, TEST_SCHEMA_VERSION))
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .extract()
                .response()
                .as(MissingSchemaResponseDto.class);

        assertEquals(40401, deleteResponse.errorCode());
        assertEquals(SchemaPresence.NO_SUBJECT, deleteResponse.schemaPresence());
        assertEquals(Response.Status.NOT_FOUND, deleteResponse.statusCode());
    }

    private void postSchema(String subjectName, String schema) {
        JsonObject testInput = new JsonObject();
        testInput.put("subjectName", subjectName);
        testInput.put("schema", schema);

        given()
                .when()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(testInput.toString())
                .post("/avroGenerator/v1/createSchema");
    }
}
