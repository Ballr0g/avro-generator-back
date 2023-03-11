package ru.hse.avrogen.component;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import ru.hse.avrogen.dto.PostSchemaResponseDto;
import ru.hse.avrogen.infrastructure.ApicurioRegistryTestResource;

import javax.ws.rs.core.HttpHeaders;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(value = ApicurioRegistryTestResource.class, restrictToAnnotatedClass = true)
public class ApicurioClientComponentTest {
    @Test
    public void testGetSubjectsEndpoint() {
        given()
                .when()
                .get("/avroGenerator/v1/getSubjects")
                .then()
                .statusCode(200)
                .body(startsWith("["), endsWith("]"));
    }

    @Test
    public void testPostNewSchemaEndpoint() {
        JsonObject testInput = new JsonObject();
        testInput.put("subjectName", "666");
        testInput.put("schema", "{ \"type\": \"record\", \"name\": \"test\", \"fields\": [ { \"type\": \"string\", \"name\": \"field\" } ] }");

        var postResult = given()
                .when()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(testInput.toString())
                .post("/avroGenerator/v1/createSchema")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .as(PostSchemaResponseDto.class);

        assertEquals(1, postResult.id());
    }
}
