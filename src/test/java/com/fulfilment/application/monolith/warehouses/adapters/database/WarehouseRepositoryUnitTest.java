package com.fulfilment.application.monolith.warehouses.adapters.database;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class WarehouseRepositoryUnitTest {

  private Warehouse warehouse(String code) {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = code;
    warehouse.location = "AMSTERDAM-001";
    warehouse.capacity = 100;
    warehouse.stock = 10;
    warehouse.createdAt = LocalDateTime.now();
    return warehouse;
  }

  @Test
  void testRepositoryMethods() {
    WarehouseRepository repository = spy(new WarehouseRepository());

    DbWarehouse db = new DbWarehouse();
    db.businessUnitCode = "MWH.001";
    db.location = "AMSTERDAM-001";
    db.capacity = 100;
    db.stock = 10;
    db.createdAt = LocalDateTime.now();

    // create
    doNothing().when(repository).persist(any(DbWarehouse.class));
    repository.create(warehouse("MWH.001"));
    verify(repository).persist(any(DbWarehouse.class));

    // getAll
    doReturn(List.of(db)).when(repository).listAll();
    List<Warehouse> all = repository.getAll();
    assertEquals(1, all.size());

    // findByBusinessUnitCode
    @SuppressWarnings("unchecked")
    PanacheQuery<DbWarehouse> query = (PanacheQuery<DbWarehouse>) mock(PanacheQuery.class);
    doReturn(query).when(repository).find(eq("businessUnitCode"), eq("MWH.001"));
    when(query.firstResult()).thenReturn(db);

    Warehouse found = repository.findByBusinessUnitCode("MWH.001");
    assertNotNull(found);

    // update existing
    Warehouse update = warehouse("MWH.001");
    update.location = "ZWOLLE-001";
    update.capacity = 40;
    update.stock = 20;
    update.archivedAt = LocalDateTime.now();

    repository.update(update);
    assertEquals("ZWOLLE-001", db.location);
    assertEquals(40, db.capacity);

    // update missing
    doReturn(query).when(repository).find(eq("businessUnitCode"), eq("MWH.MISSING"));
    when(query.firstResult()).thenReturn(null);
    Warehouse missing = warehouse("MWH.MISSING");
    repository.update(missing);

    assertThrows(UnsupportedOperationException.class, () -> repository.remove(new Warehouse()));
  }
}

