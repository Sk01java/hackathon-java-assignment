package com.fulfilment.application.monolith.warehouses.domain.models;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Warehouse Domain Model Tests")
class WarehouseTest {

  @Test
  @DisplayName("warehouse starts with null fields")
  void testWarehouseDefaultState() {
	Warehouse warehouse = new Warehouse();

	assertNull(warehouse.businessUnitCode);
	assertNull(warehouse.location);
	assertNull(warehouse.capacity);
	assertNull(warehouse.stock);
	assertNull(warehouse.createdAt);
	assertNull(warehouse.archivedAt);
  }

  @Test
  @DisplayName("warehouse fields can be populated and updated")
  void testWarehouseFieldAssignmentAndMutation() {
	Warehouse warehouse = new Warehouse();
	LocalDateTime created = LocalDateTime.of(2026, 4, 8, 10, 30);
	LocalDateTime archived = LocalDateTime.of(2026, 5, 1, 9, 0);

	warehouse.businessUnitCode = "MWH.100";
	warehouse.location = "ROTTERDAM-001";
	warehouse.capacity = 200;
	warehouse.stock = 80;
	warehouse.createdAt = created;
	warehouse.archivedAt = archived;

	assertEquals("MWH.100", warehouse.businessUnitCode);
	assertEquals("ROTTERDAM-001", warehouse.location);
	assertEquals(200, warehouse.capacity);
	assertEquals(80, warehouse.stock);
	assertEquals(created, warehouse.createdAt);
	assertEquals(archived, warehouse.archivedAt);

	warehouse.stock = 120;
	warehouse.archivedAt = null;

	assertEquals(120, warehouse.stock);
	assertNull(warehouse.archivedAt);
  }

  @Test
  @DisplayName("location record exposes components")
  void testLocationComponents() {
	Location location = new Location("ZWOLLE", 5, 1000);

	assertEquals("ZWOLLE", location.identifier());
	assertEquals(5, location.maxNumberOfWarehouses());
	assertEquals(1000, location.maxCapacity());
  }

  @Test
  @DisplayName("location record equality hashCode and toString")
  void testLocationValueSemantics() {
	Location left = new Location("TILBURG", 3, 600);
	Location right = new Location("TILBURG", 3, 600);
	Location different = new Location("AMSTERDAM", 3, 600);

	assertEquals(left, right);
	assertEquals(left.hashCode(), right.hashCode());
	assertNotEquals(left, different);
	assertTrue(left.toString().contains("TILBURG"));
  }
}