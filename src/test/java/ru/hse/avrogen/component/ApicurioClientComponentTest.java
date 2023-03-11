package ru.hse.avrogen.component;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import ru.hse.avrogen.infrastructure.ApicurioRegistryTestResource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
@QuarkusTestResource(value = ApicurioRegistryTestResource.class, restrictToAnnotatedClass = true)
public class ApicurioClientComponentTest {
    @Test
    public void testGetSchemasEndpoint() {
        given()
                .when().get("/avroGenerator/v1/getSubjects")
                .then()
                .statusCode(200)
                .body(containsString("[]"));
    }
}
