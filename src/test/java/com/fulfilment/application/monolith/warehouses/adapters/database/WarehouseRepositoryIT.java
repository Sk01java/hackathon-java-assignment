package com.fulfilment.application.monolith.warehouses.adapters.database;

import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class WarehouseRepositoryIT {

  @Inject WarehouseRepository warehouseRepository;

  @BeforeEach
  @Transactional
  void setUp() {
    warehouseRepository.deleteAll();
  }

  @Test
  @Transactional
  void testCreateAndFindByBusinessUnitCode() {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.001";
    warehouse.location = "AMSTERDAM-001";
    warehouse.capacity = 100;
    warehouse.stock = 10;
    warehouse.createdAt = LocalDateTime.now();

    warehouseRepository.create(warehouse);

    Warehouse found = warehouseRepository.findByBusinessUnitCode("MWH.001");
    assertNotNull(found);
    assertEquals("AMSTERDAM-001", found.location);
  }

  @Test
  @Transactional
  void testUpdateAndGetAll() {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.002";
    warehouse.location = "ZWOLLE-001";
    warehouse.capacity = 40;
    warehouse.stock = 8;
    warehouse.createdAt = LocalDateTime.now();

    warehouseRepository.create(warehouse);

    Warehouse update = new Warehouse();
    update.businessUnitCode = "MWH.002";
    update.location = "ZWOLLE-002";
    update.capacity = 50;
    update.stock = 20;
    update.archivedAt = LocalDateTime.now();

    warehouseRepository.update(update);

    Warehouse found = warehouseRepository.findByBusinessUnitCode("MWH.002");
    assertEquals("ZWOLLE-002", found.location);
    assertEquals(50, found.capacity);

    List<Warehouse> all = warehouseRepository.getAll();
    assertEquals(1, all.size());
  }

  @Test
  void testRemoveThrowsUnsupportedOperation() {
    Warehouse warehouse = new Warehouse();
    assertThrows(UnsupportedOperationException.class, () -> warehouseRepository.remove(warehouse));
  }
}

