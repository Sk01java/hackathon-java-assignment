package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class WarehouseEndpointIT {

  @Inject
  EntityManager em;

  @BeforeEach
  @Transactional
  public void setup() {
    em.createQuery("DELETE FROM DbWarehouse").executeUpdate();

    // Insert initial warehouses
    var w1 = new com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse();
    w1.businessUnitCode = "MWH.001";
    w1.location = "ZWOLLE-001";
    w1.capacity = 100;
    w1.stock = 10;
    w1.createdAt = LocalDateTime.parse("2024-07-01T00:00:00");
    em.persist(w1);

    var w2 = new com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse();
    w2.businessUnitCode = "MWH.012";
    w2.location = "AMSTERDAM-001";
    w2.capacity = 50;
    w2.stock = 5;
    w2.createdAt = LocalDateTime.parse("2023-07-01T00:00:00");
    em.persist(w2);

    var w3 = new com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse();
    w3.businessUnitCode = "MWH.023";
    w3.location = "TILBURG-001";
    w3.capacity = 30;
    w3.stock = 27;
    w3.createdAt = LocalDateTime.parse("2021-02-01T00:00:00");
    em.persist(w3);
  }

  @Test
  public void testSimpleListWarehouses() {

    final String path = "warehouse";

    // List all, should have all 3 products the database has initially:
    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(containsString("MWH.001"), containsString("MWH.012"), containsString("MWH.023"));
  }
}
