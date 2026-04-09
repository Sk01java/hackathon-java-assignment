package com.fulfilment.application.monolith.warehouses.adapters.database;

import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class DbWarehouseTest {

  @Test
  void testToWarehouseMapping() {
    DbWarehouse db = new DbWarehouse();
    db.businessUnitCode = "MWH.001";
    db.location = "AMSTERDAM-001";
    db.capacity = 100;
    db.stock = 10;
    db.createdAt = LocalDateTime.of(2026, 1, 1, 0, 0);
    db.archivedAt = null;

    Warehouse model = db.toWarehouse();

    assertEquals("MWH.001", model.businessUnitCode);
    assertEquals("AMSTERDAM-001", model.location);
    assertEquals(100, model.capacity);
    assertEquals(10, model.stock);
    assertEquals(db.createdAt, model.createdAt);
    assertNull(model.archivedAt);
  }
}

