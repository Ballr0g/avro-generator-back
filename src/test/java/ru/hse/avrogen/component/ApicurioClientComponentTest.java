package ru.hse.avrogen.component;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hse.avrogen.dto.PostSchemaResponseDto;
import ru.hse.avrogen.dto.responses.PostIncorrectSchemaResponseDto;
import ru.hse.avrogen.infrastructure.ApicurioRegistryTestResource;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(value = ApicurioRegistryTestResource.class, restrictToAnnotatedClass = true)
public class ApicurioClientComponentTest {

    private static final String TEST_SUBJECT_NAME = "666";

    @BeforeEach
    void ClearTestContainer() {
        delete(String.format("/avroGenerator/v1/deleteSubject/%s", TEST_SUBJECT_NAME));
    }


    @Test
    public void testGetSubjectsEndpoint() {
        given()
                .when()
                .get("/avroGenerator/v1/getSubjects")
                .then()
                .statusCode(200)
                .body(containsString("[]"));
    }

    @Test
    void testPostNewCorrectSchema() {
        JsonObject testInput = new JsonObject();
        testInput.put("subjectName", TEST_SUBJECT_NAME);
        testInput.put("schema", "{\"type\":\"record\",\"name\":\"onecolumn\",\"namespace\":\"system.domain\",\"doc\":\"testSDPsubject\",\"fields\":[{\"name\":\"system_info\",\"type\":{\"type\":\"record\",\"name\":\"system_record\",\"fields\":[{\"name\":\"timestamp\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}},{\"name\":\"operation\",\"type\":{\"type\":\"enum\",\"name\":\"operations\",\"symbols\":[\"I\",\"U\",\"D\"]}}]}},{\"name\":\"key\",\"type\":{\"type\":\"record\",\"name\":\"key_record\",\"fields\":[{\"name\":\"id\",\"type\":\"int\",\"doc\":\"unique id\",\"comment\":\"unique id\"}]}},{\"name\":\"payload\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"payload_record\",\"fields\":[{\"name\":\"val\",\"type\":\"string\"}]}]}]}");

        var postResult = given()
                .when()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(testInput.toString())
                .post("/avroGenerator/v1/createSchema")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .response()
                .as(PostSchemaResponseDto.class);

        assertEquals(1, postResult.id());
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
}
