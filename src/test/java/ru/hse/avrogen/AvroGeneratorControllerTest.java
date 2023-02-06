package ru.hse.avrogen;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class AvroGeneratorControllerTest {

    // Currently this is a dummy test for making sure the GitHub CI works.
    @Test
    public void testGetSchemasEndpoint() {
        given()
                .when().get("/avroGenerator/v1/getSchemas")
                .then()
                .statusCode(200)
                .body(containsString("Dummy string result"));
    }

}