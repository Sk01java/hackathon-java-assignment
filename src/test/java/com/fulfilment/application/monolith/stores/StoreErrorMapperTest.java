package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for StoreResource.ErrorMapper exception handling.
 * Verifies proper error response formatting and HTTP status codes.
 */
@QuarkusTest
@DisplayName("Store ErrorMapper Tests")
class StoreErrorMapperTest {

  @Test
  @DisplayName("WebApplicationException with 404 status")
  void testWebApplicationException404() {
    given()
        .when()
        .get("/store/999999")
        .then()
        .statusCode(404)
        .body("error", containsString("does not exist"))
        .body("code", equalTo(404))
        .body("exceptionType", notNullValue());
  }

  @Test
  @DisplayName("WebApplicationException with 422 status")
  void testWebApplicationException422() {
    Store s = new Store("ErrorTest");
    s.id = 999L;
    s.quantityProductsInStock = 50;

    given()
        .contentType("application/json")
        .body(s)
        .when()
        .post("/store")
        .then()
        .statusCode(422)
        .body("error", containsString("Id was invalidly set"))
        .body("code", equalTo(422))
        .body("exceptionType", notNullValue());
  }

  @Test
  @DisplayName("Error response includes exception type")
  void testErrorResponseIncludesExceptionType() {
    given()
        .when()
        .get("/store/invalid")
        .then()
        .statusCode(404)
        .body("exceptionType", notNullValue());
  }

  @Test
  @DisplayName("Error response includes error message")
  void testErrorResponseIncludesMessage() {
    given()
        .when()
        .get("/store/999999")
        .then()
        .statusCode(404)
        .body("error", notNullValue());
  }

  @Test
  @DisplayName("Error response includes code")
  void testErrorResponseIncludesCode() {
    given()
        .when()
        .get("/store/999999")
        .then()
        .statusCode(404)
        .body("code", equalTo(404));
  }

  @Test
  @DisplayName("Error response is JSON format")
  void testErrorResponseJsonFormat() {
    given()
        .when()
        .get("/store/999999")
        .then()
        .statusCode(404)
        .contentType("application/json");
  }

  @Test
  @DisplayName("WebApplicationException with 400 status")
  void testWebApplicationException400() {
    given()
        .contentType("application/json")
        .body("{invalid json")
        .when()
        .post("/store")
        .then()
        .statusCode(400)
        .body("code", equalTo(400));
  }

  @Test
  @DisplayName("Store name validation error 422")
  void testStoreNameValidationError() {
    Store s = new Store(null);
    s.quantityProductsInStock = 50;

    given()
        .contentType("application/json")
        .body(s)
        .when()
        .put("/store/1")
        .then()
        .statusCode(422);
  }

  @Test
  @DisplayName("Error mapper preserves exception message")
  void testErrorMapperPreservesMessage() {
    given()
        .when()
        .get("/store/999999")
        .then()
        .statusCode(404)
        .body("error", containsString("999999"));
  }

  @Test
  @DisplayName("Multiple error codes correctly mapped")
  void testMultipleErrorCodesMapped() {
    // Test 404
    given()
        .when()
        .get("/store/999999")
        .then()
        .statusCode(404);

    // Test 201 (success)
    Store s = new Store("Success");
    s.quantityProductsInStock = 50;

    given()
        .contentType("application/json")
        .body(s)
        .when()
        .post("/store")
        .then()
        .statusCode(201);
  }
}

