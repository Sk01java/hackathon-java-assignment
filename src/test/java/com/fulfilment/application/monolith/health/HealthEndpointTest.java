package com.fulfilment.application.monolith.health;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class HealthEndpointTest {

  @Test
  void livenessEndpointReturnsUp() {
    given()
        .when()
        .get("/q/health/live")
        .then()
        .statusCode(200)
        .body("status", equalTo("UP"));
  }

  @Test
  void readinessEndpointReturnsUp() {
    given()
        .when()
        .get("/q/health/ready")
        .then()
        .statusCode(200)
        .body("status", equalTo("UP"));
  }

  @Test
  void aggregateHealthEndpointReturnsUp() {
    given()
        .when()
        .get("/q/health")
        .then()
        .statusCode(200)
        .body("status", equalTo("UP"));
  }
}

