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
class WarehouseRepositoryTest {

  @Inject WarehouseRepository warehouseRepository;

  @BeforeEach
  @Transactional
  void setUp() {
    warehouseRepository.deleteAll();
  }

  private Warehouse warehouse(String bu, String location, int capacity, int stock) {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = bu;
    warehouse.location = location;
    warehouse.capacity = capacity;
    warehouse.stock = stock;
    warehouse.createdAt = LocalDateTime.now();
    return warehouse;
  }

  @Test
  @Transactional
  void testCreateFindUpdateGetAllAndRemoveUnsupported() {
    Warehouse input = warehouse("MWH.001", "AMSTERDAM-001", 100, 10);
    warehouseRepository.create(input);

    Warehouse found = warehouseRepository.findByBusinessUnitCode("MWH.001");
    assertNotNull(found);
    assertEquals("AMSTERDAM-001", found.location);

    Warehouse update = warehouse("MWH.001", "ZWOLLE-001", 40, 20);
    update.archivedAt = LocalDateTime.now();
    warehouseRepository.update(update);

    Warehouse updated = warehouseRepository.findByBusinessUnitCode("MWH.001");
    assertEquals("ZWOLLE-001", updated.location);
    assertEquals(40, updated.capacity);
    assertEquals(20, updated.stock);
    assertNotNull(updated.archivedAt);

    List<Warehouse> all = warehouseRepository.getAll();
    assertEquals(1, all.size());

    assertThrows(UnsupportedOperationException.class, () -> warehouseRepository.remove(new Warehouse()));
  }

  @Test
  @Transactional
  void testUpdateWhenWarehouseNotFoundDoesNothing() {
    Warehouse missing = warehouse("MWH.MISSING", "AMSTERDAM-001", 10, 1);
    warehouseRepository.update(missing);

    assertNull(warehouseRepository.findByBusinessUnitCode("MWH.MISSING"));
  }
}

