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

    // Solutions:
    // 1) @BeforeEach clear test subject ID for each test (using direct container calls).
    // 2) Unique subjectId for each test.
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
        testInput.put("schema", "{\"type\":\"record\",\"name\":\"onecolumn\",\"namespace\":\"system.domain\",\"doc\":\"testSDPsubject\",\"fields\":[{\"name\":\"system_info\",\"type\":{\"type\":\"record\",\"name\":\"system_record\",\"fields\":[{\"name\":\"timestamp\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}},{\"name\":\"operation\",\"type\":{\"type\":\"enum\",\"name\":\"operations\",\"symbols\":[\"I\",\"U\",\"D\"]}}]}},{\"name\":\"key\",\"type\":{\"type\":\"record\",\"name\":\"key_record\",\"fields\":[{\"name\":\"id\",\"type\":\"int\",\"doc\":\"unique id\",\"comment\":\"unique id\"}]}},{\"name\":\"payload\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"payload_record\",\"fields\":[{\"name\":\"val\",\"type\":\"string\"}]}]}]}");

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
