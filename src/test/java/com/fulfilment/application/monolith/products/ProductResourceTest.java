package com.fulfilment.application.monolith.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("ProductResource REST API Tests")
class ProductResourceTest {

  @Inject ProductRepository productRepository;

  @BeforeEach
  @Transactional
  void setUp() {
    productRepository.deleteAll();
  }

  private Product product(String name, String description, double price, int stock) {
    Product product = new Product(name);
    product.description = description;
    product.price = BigDecimal.valueOf(price);
    product.stock = stock;
    return product;
  }

  private Long createProduct(String name, String description, double price, int stock) {
    Number id =
        given()
        .contentType(ContentType.JSON)
        .body(product(name, description, price, stock))
        .when()
        .post("/product")
        .then()
        .statusCode(201)
        .extract()
        .path("id");
    return id.longValue();
  }

  @Test
  @DisplayName("GET empty")
  void testGetEmpty() {
    given().when().get("/product").then().statusCode(200).body("$", empty());
  }

  @Test
  @DisplayName("GET sorted")
  void testGetSorted() {
    createProduct("Zebra", "Desc", 99.99, 10);
    createProduct("Apple", "Desc", 49.99, 20);

    given().when().get("/product").then().statusCode(200).body("$", hasSize(2)).body("[0].name", equalTo("Apple"));
  }

  @Test
  @DisplayName("GET single")
  void testGetSingle() {
    Long id = createProduct("Test", "TestDesc", 29.99, 15);
    given().when().get("/product/" + id).then().statusCode(200).body("name", equalTo("Test"));
  }

  @Test
  @DisplayName("GET not found")
  void testGetNotFound() {
    given().when().get("/product/999999").then().statusCode(404);
  }

  @Test
  @DisplayName("POST create")
  void testCreate() {
    given()
        .contentType(ContentType.JSON)
        .body(product("New", "NewDesc", 59.99, 25))
        .when()
        .post("/product")
        .then()
        .statusCode(201)
        .body("id", notNullValue());
  }

  @Test
  @DisplayName("POST with id")
  void testCreateWithId() {
    Product payload = product("ID", "Desc", 19.99, 10);
    payload.id = 999L;

    given().contentType(ContentType.JSON).body(payload).when().post("/product").then().statusCode(422);
  }

  @Test
  @DisplayName("PUT update")
  void testUpdate() {
    Long id = createProduct("Orig", "OrigDesc", 10.00, 5);
    given()
        .contentType(ContentType.JSON)
        .body(product("Upd", "UpdDesc", 20.00, 10))
        .when()
        .put("/product/" + id)
        .then()
        .statusCode(200)
        .body("name", equalTo("Upd"));
  }

  @Test
  @DisplayName("PUT not found")
  void testUpdateNotFound() {
    given()
        .contentType(ContentType.JSON)
        .body(product("U", "Desc", 15.00, 8))
        .when()
        .put("/product/999999")
        .then()
        .statusCode(404);
  }

  @Test
  @DisplayName("PUT no name")
  void testUpdateNoName() {
    Long id = createProduct("T", "Desc", 15.00, 10);
    Product payload = product(null, "NewDesc", 25.00, 15);
    given().contentType(ContentType.JSON).body(payload).when().put("/product/" + id).then().statusCode(422);
  }

  @Test
  @DisplayName("DELETE")
  void testDelete() {
    Long id = createProduct("D", "Desc", 15.00, 10);
    given().when().delete("/product/" + id).then().statusCode(204);
    given().when().get("/product/" + id).then().statusCode(404);
  }

  @Test
  @DisplayName("DELETE not found")
  void testDeleteNotFound() {
    given().when().delete("/product/999999").then().statusCode(404);
  }

  @Test
  @DisplayName("Error")
  void testError() {
    given().when().get("/product/invalid").then().statusCode(404);
  }

  @Test
  @DisplayName("JSON")
  void testJSON() {
    given().when().get("/product").then().contentType(ContentType.JSON);
  }

  @Test
  @DisplayName("CRUD")
  void testCRUD() {
    Long id = createProduct("C", "CDesc", 49.99, 25);
    given().when().get("/product/" + id).then().statusCode(200);
    given()
        .contentType(ContentType.JSON)
        .body(product("CU", "CUDesc", 59.99, 30))
        .when()
        .put("/product/" + id)
        .then()
        .statusCode(200);
    given().when().delete("/product/" + id).then().statusCode(204);
    given().when().get("/product/" + id).then().statusCode(404);
  }

  @Test
  @DisplayName("Multi")
  void testMulti() {
    createProduct("Z", "ZD", 10.00, 5);
    createProduct("A", "AD", 20.00, 10);
    createProduct("M", "MD", 15.00, 8);

    given().when().get("/product").then().statusCode(200).body("$", hasSize(3)).body("[0].name", equalTo("A"));
  }

  @Test
  @DisplayName("ID")
  void testID() {
    given()
        .contentType(ContentType.JSON)
        .body(product("ID", "ID", 25.00, 12))
        .when()
        .post("/product")
        .then()
        .statusCode(201)
        .body("id", notNullValue())
        .body("id", greaterThan(0));
  }

  @Test
  @DisplayName("Price")
  void testPrice() {
    Long id = createProduct("P", "P", 10.00, 5);
    given()
        .contentType(ContentType.JSON)
        .body(product("P", "P", 99.99, 5))
        .when()
        .put("/product/" + id)
        .then()
        .statusCode(200)
        .body("price", equalTo(99.99f));
  }

  @Test
  @DisplayName("Stock")
  void testStock() {
    Long id = createProduct("S", "S", 50.00, 10);
    given()
        .contentType(ContentType.JSON)
        .body(product("S", "S", 50.00, 100))
        .when()
        .put("/product/" + id)
        .then()
        .statusCode(200)
        .body("stock", equalTo(100));
  }

  @Test
  @DisplayName("Prices")
  void testPrices() {
    for (double price : new double[] {0.99, 9.99, 99.99, 999.99}) {
      given()
          .contentType(ContentType.JSON)
          .body(product("P" + price, "D", price, 10))
          .when()
          .post("/product")
          .then()
          .statusCode(201)
          .body("price", equalTo((float) price));
    }
  }

  @Test
  @DisplayName("Stocks")
  void testStocks() {
    for (int stock : new int[] {0, 1, 10, 1000}) {
      given()
          .contentType(ContentType.JSON)
          .body(product("S" + stock, "D", 25.00, stock))
          .when()
          .post("/product")
          .then()
          .statusCode(201)
          .body("stock", equalTo(stock));
    }
  }

  @Test
  @DisplayName("Array")
  void testArray() {
    createProduct("A", "A", 30.00, 10);
    given().when().get("/product").then().statusCode(200).body("$", instanceOf(java.util.List.class));
  }

  @Test
  @DisplayName("Desc")
  void testDesc() {
    Long id = createProduct("O", "OD", 30.00, 10);
    given()
        .contentType(ContentType.JSON)
        .body(product("O", "UD", 30.00, 10))
        .when()
        .put("/product/" + id)
        .then()
        .statusCode(200)
        .body("description", equalTo("UD"));
  }

  @Test
  @DisplayName("Name")
  void testName() {
    Long id = createProduct("O", "D", 50.00, 20);
    given()
        .contentType(ContentType.JSON)
        .body(product("N", "D", 50.00, 20))
        .when()
        .put("/product/" + id)
        .then()
        .statusCode(200)
        .body("name", equalTo("N"));
  }

  @Test
  @DisplayName("Fields")
  void testFields() {
    Long id = createProduct("C", "CD", 87.50, 33);
    given().when().get("/product/" + id).then().statusCode(200).body("id", notNullValue()).body("name", equalTo("C"));
  }
}
