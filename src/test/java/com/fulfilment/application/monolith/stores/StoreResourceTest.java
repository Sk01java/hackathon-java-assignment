package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("StoreResource REST API Tests")
class StoreResourceTest {

  @BeforeEach
  @Transactional
  void setUp() {
    Store.deleteAll();
  }

  private Store store(String name, int qty) {
    Store store = new Store(name);
    store.quantityProductsInStock = qty;
    return store;
  }

  private long createStore(String name, int qty) {
    Number id =
        given()
            .contentType(ContentType.JSON)
            .body(store(name, qty))
            .when()
            .post("/store")
            .then()
            .statusCode(201)
            .extract()
            .path("id");
    return id.longValue();
  }

  @Test
  @DisplayName("GET returns empty")
  void testGetEmpty() {
    given().when().get("/store").then().statusCode(200).body("$", empty());
  }

  @Test
  @DisplayName("GET all sorted")
  void testGetAllSorted() {
    createStore("Beta", 10);
    createStore("Alpha", 20);
    given().when().get("/store").then().statusCode(200).body("$", hasSize(2)).body("[0].name", equalTo("Alpha"));
  }

  @Test
  @DisplayName("GET single")
  void testGetSingle() {
    long id = createStore("Test", 50);
    given().when().get("/store/" + id).then().statusCode(200).body("name", equalTo("Test"));
  }

  @Test
  @DisplayName("GET not found")
  void testGetNotFound() {
    given().when().get("/store/999999").then().statusCode(404);
  }

  @Test
  @DisplayName("POST create")
  void testCreate() {
    given().contentType(ContentType.JSON).body(store("New", 100)).when().post("/store").then().statusCode(201).body("id", notNullValue());
  }

  @Test
  @DisplayName("POST with id fails")
  void testCreateWithId() {
    Store payload = store("ID", 50);
    payload.id = 999L;
    given().contentType(ContentType.JSON).body(payload).when().post("/store").then().statusCode(422);
  }

  @Test
  @DisplayName("PUT update")
  void testUpdate() {
    long id = createStore("Orig", 10);
    given().contentType(ContentType.JSON).body(store("Upd", 50)).when().put("/store/" + id).then().statusCode(200).body("name", equalTo("Upd"));
  }

  @Test
  @DisplayName("PUT not found")
  void testUpdateNotFound() {
    given().contentType(ContentType.JSON).body(store("U", 50)).when().put("/store/999999").then().statusCode(404);
  }

  @Test
  @DisplayName("PUT no name")
  void testUpdateNoName() {
    long id = createStore("T", 30);
    Store payload = store(null, 50);
    given().contentType(ContentType.JSON).body(payload).when().put("/store/" + id).then().statusCode(422);
  }

  @Test
  @DisplayName("PATCH")
  void testPatch() {
    long id = createStore("O", 25);
    given().contentType(ContentType.JSON).body(store("P", 50)).when().patch("/store/" + id).then().statusCode(200).body("name", equalTo("P"));
  }

  @Test
  @DisplayName("PATCH not found")
  void testPatchNotFound() {
    given().contentType(ContentType.JSON).body(store("P", 30)).when().patch("/store/999999").then().statusCode(404);
  }

  @Test
  @DisplayName("PATCH no name")
  void testPatchNoName() {
    long id = createStore("T", 30);
    Store payload = store(null, 50);
    given().contentType(ContentType.JSON).body(payload).when().patch("/store/" + id).then().statusCode(422);
  }

  @Test
  @DisplayName("PATCH qty")
  void testPatchQty() {
    long id = createStore("T", 100);
    given().contentType(ContentType.JSON).body(store("U", 50)).when().patch("/store/" + id).then().statusCode(200).body("quantityProductsInStock", equalTo(50));
  }

  @Test
  @DisplayName("DELETE")
  void testDelete() {
    long id = createStore("D", 10);
    given().when().delete("/store/" + id).then().statusCode(204);
    given().when().get("/store/" + id).then().statusCode(404);
  }

  @Test
  @DisplayName("DELETE not found")
  void testDeleteNotFound() {
    given().when().delete("/store/999999").then().statusCode(404);
  }

  @Test
  @DisplayName("Error handling")
  void testError() {
    given().when().get("/store/invalid").then().statusCode(404);
  }

  @Test
  @DisplayName("JSON content type")
  void testJSON() {
    given().when().get("/store").then().contentType(ContentType.JSON);
  }

  @Test
  @DisplayName("POST JSON")
  void testPostJSON() {
    given().contentType(ContentType.JSON).body(store("J", 25)).when().post("/store").then().statusCode(201).contentType(ContentType.JSON);
  }

  @Test
  @DisplayName("CRUD workflow")
  void testCRUD() {
    long id = createStore("C", 10);
    given().when().get("/store/" + id).then().statusCode(200);
    given().contentType(ContentType.JSON).body(store("CU", 50)).when().put("/store/" + id).then().statusCode(200);
    given().when().delete("/store/" + id).then().statusCode(204);
    given().when().get("/store/" + id).then().statusCode(404);
  }

  @Test
  @DisplayName("Multi stores")
  void testMultipleStores() {
    createStore("Z", 1);
    createStore("A", 2);
    createStore("M", 3);
    given().when().get("/store").then().statusCode(200).body("$", hasSize(3)).body("[0].name", equalTo("A"));
  }

  @Test
  @DisplayName("Qty update")
  void testQtyUpdate() {
    long id = createStore("Q", 100);
    given().contentType(ContentType.JSON).body(store("Q", 200)).when().put("/store/" + id).then().statusCode(200).body("quantityProductsInStock", equalTo(200));
  }

  @Test
  @DisplayName("Created has ID")
  void testCreatedHasId() {
    given().contentType(ContentType.JSON).body(store("ID", 30)).when().post("/store").then().statusCode(201).body("id", notNullValue()).body("id", greaterThan(0));
  }

  @Test
  @DisplayName("Delete 204")
  void testDelete204() {
    long id = createStore("D204", 20);
    given().when().delete("/store/" + id).then().statusCode(204);
  }

  @Test
  @DisplayName("PATCH complete")
  void testPatchComplete() {
    long id = createStore("PC", 40);
    given().contentType(ContentType.JSON).body(store("PCA", 60)).when().patch("/store/" + id).then().statusCode(200).body("name", equalTo("PCA"));
  }

  @Test
  @DisplayName("List is array")
  void testListArray() {
    createStore("AR", 10);
    given().when().get("/store").then().statusCode(200).body("$", instanceOf(java.util.List.class));
  }

  @Test
  @DisplayName("Update name and qty")
  void testUpdateBoth() {
    long id = createStore("O", 50);
    given().contentType(ContentType.JSON).body(store("N", 150)).when().put("/store/" + id).then().statusCode(200).body("name", equalTo("N"));
  }

  @Test
  @DisplayName("Event firing on create")
  void testEventFiredOnCreate() {
    given()
        .contentType(ContentType.JSON)
        .body(store("EventTest", 40))
        .when()
        .post("/store")
        .then()
        .statusCode(201)
        .body("name", equalTo("EventTest"));
  }

  @Test
  @DisplayName("Event firing on update")
  void testEventFiredOnUpdate() {
    long id = createStore("OrigEvent", 30);
    given()
        .contentType(ContentType.JSON)
        .body(store("UpdatedEvent", 60))
        .when()
        .put("/store/" + id)
        .then()
        .statusCode(200)
        .body("name", equalTo("UpdatedEvent"));
  }

  @Test
  @DisplayName("Event firing on patch")
  void testEventFiredOnPatch() {
    long id = createStore("PatchEvent", 25);
    given()
        .contentType(ContentType.JSON)
        .body(store("PatchedEvent", 75))
        .when()
        .patch("/store/" + id)
        .then()
        .statusCode(200)
        .body("name", equalTo("PatchedEvent"));
  }

  @Test
  @DisplayName("Error response format for 404")
  void testErrorResponseFormat404() {
    given()
        .when()
        .get("/store/999999")
        .then()
        .statusCode(404)
        .body("error", containsString("does not exist"));
  }

  @Test
  @DisplayName("Error response format for 422")
  void testErrorResponseFormat422() {
    Store payload = store("Error422", 50);
    payload.id = 999L;
    given()
        .contentType(ContentType.JSON)
        .body(payload)
        .when()
        .post("/store")
        .then()
        .statusCode(422)
        .body("error", containsString("Id was invalidly set"));
  }

  @Test
  @DisplayName("Store with zero quantity")
  void testStoreWithZeroQuantity() {
    given()
        .contentType(ContentType.JSON)
        .body(store("ZeroQty", 0))
        .when()
        .post("/store")
        .then()
        .statusCode(201)
        .body("quantityProductsInStock", equalTo(0));
  }

  @Test
  @DisplayName("Store with negative quantity")
  void testStoreWithNegativeQuantity() {
    given()
        .contentType(ContentType.JSON)
        .body(store("NegQty", -5))
        .when()
        .post("/store")
        .then()
        .statusCode(201)
        .body("quantityProductsInStock", equalTo(-5));
  }

  @Test
  @DisplayName("Store with large quantity")
  void testStoreWithLargeQuantity() {
    given()
        .contentType(ContentType.JSON)
        .body(store("LargeQty", Integer.MAX_VALUE))
        .when()
        .post("/store")
        .then()
        .statusCode(201)
        .body("quantityProductsInStock", equalTo(Integer.MAX_VALUE));
  }

  @Test
  @DisplayName("Update quantity to zero")
  void testUpdateQuantityToZero() {
    long id = createStore("UpdateQtyZero", 100);
    given()
        .contentType(ContentType.JSON)
        .body(store("UpdateQtyZero", 0))
        .when()
        .put("/store/" + id)
        .then()
        .statusCode(200)
        .body("quantityProductsInStock", equalTo(0));
  }

  @Test
  @DisplayName("Patch does not update null name")
  void testPatchDoesNotUpdateNullName() {
    long id = createStore("OrigName", 50);
    Store payload = store(null, 100);
    given()
        .contentType(ContentType.JSON)
        .body(payload)
        .when()
        .patch("/store/" + id)
        .then()
        .statusCode(422);
  }

  @Test
  @DisplayName("Patch preserves name when qty zero")
  void testPatchPreservesNameWhenQtyZero() {
    long id = createStore("PreserveName", 50);
    given()
        .contentType(ContentType.JSON)
        .body(store("NewName", 0))
        .when()
        .patch("/store/" + id)
        .then()
        .statusCode(200)
        .body("name", equalTo("PreserveName"));
  }

  @Test
  @DisplayName("Response includes store ID")
  void testResponseIncludesStoreId() {
    given()
        .contentType(ContentType.JSON)
        .body(store("WithID", 30))
        .when()
        .post("/store")
        .then()
        .statusCode(201)
        .body("id", notNullValue());
  }

  @Test
  @DisplayName("Response includes store name")
  void testResponseIncludesStoreName() {
    given()
        .contentType(ContentType.JSON)
        .body(store("WithName", 40))
        .when()
        .post("/store")
        .then()
        .statusCode(201)
        .body("name", equalTo("WithName"));
  }

  @Test
  @DisplayName("Response includes store quantity")
  void testResponseIncludesStoreQuantity() {
    given()
        .contentType(ContentType.JSON)
        .body(store("WithQty", 75))
        .when()
        .post("/store")
        .then()
        .statusCode(201)
        .body("quantityProductsInStock", equalTo(75));
  }

  @Test
  @DisplayName("GET single returns correct fields")
  void testGetSingleReturnsCorrectFields() {
    long id = createStore("AllFields", 88);

    given()
        .when()
        .get("/store/" + id)
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo("AllFields"))
        .body("quantityProductsInStock", equalTo(88));
  }
}

