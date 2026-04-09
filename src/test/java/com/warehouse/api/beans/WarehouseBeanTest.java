package com.warehouse.api.beans;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Generated Warehouse Bean Tests")
class WarehouseBeanTest {

  @Test
  @DisplayName("default values are null")
  void testDefaultValues() {
    Warehouse warehouse = new Warehouse();

    assertNull(warehouse.getId());
    assertNull(warehouse.getBusinessUnitCode());
    assertNull(warehouse.getLocation());
    assertNull(warehouse.getCapacity());
    assertNull(warehouse.getStock());
  }

  @Test
  @DisplayName("getters and setters round trip")
  void testGettersAndSetters() {
    Warehouse warehouse = new Warehouse();

    warehouse.setId("1");
    warehouse.setBusinessUnitCode("MWH.001");
    warehouse.setLocation("ZWOLLE-001");
    warehouse.setCapacity(100);
    warehouse.setStock(10);

    assertEquals("1", warehouse.getId());
    assertEquals("MWH.001", warehouse.getBusinessUnitCode());
    assertEquals("ZWOLLE-001", warehouse.getLocation());
    assertEquals(100, warehouse.getCapacity());
    assertEquals(10, warehouse.getStock());
  }
}

