package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReplaceWarehouseUseCaseUnitTest {

  private WarehouseStore warehouseStore;
  private LocationResolver locationResolver;
  private ReplaceWarehouseUseCase useCase;

  @BeforeEach
  void setUp() {
    warehouseStore = mock(WarehouseStore.class);
    locationResolver = mock(LocationResolver.class);
    useCase = new ReplaceWarehouseUseCase(warehouseStore, locationResolver);
  }

  @Test
  void testReplaceSuccess() {
    Warehouse existing = new Warehouse();
    existing.businessUnitCode = "MWH.001";
    existing.location = "OLD";
    existing.capacity = 10;
    existing.stock = 5;

    Warehouse replacement = new Warehouse();
    replacement.businessUnitCode = "MWH.001";
    replacement.location = "AMSTERDAM-001";
    replacement.capacity = 60;
    replacement.stock = 40;

    when(warehouseStore.findByBusinessUnitCode("MWH.001")).thenReturn(existing);
    when(locationResolver.resolveByIdentifier("AMSTERDAM-001"))
        .thenReturn(new Location("AMSTERDAM-001", 5, 100));

    useCase.replace(replacement);

    assertEquals("AMSTERDAM-001", existing.location);
    assertEquals(60, existing.capacity);
    assertEquals(40, existing.stock);
    verify(warehouseStore).update(existing);
  }

  @Test
  void testReplaceFailsForMissingWarehouse() {
    Warehouse replacement = new Warehouse();
    replacement.businessUnitCode = "MWH.404";

    when(warehouseStore.findByBusinessUnitCode("MWH.404")).thenReturn(null);

    assertThrows(IllegalArgumentException.class, () -> useCase.replace(replacement));
  }

  @Test
  void testReplaceFailsForArchivedWarehouse() {
    Warehouse existing = new Warehouse();
    existing.businessUnitCode = "MWH.002";
    existing.archivedAt = java.time.LocalDateTime.now();

    Warehouse replacement = new Warehouse();
    replacement.businessUnitCode = "MWH.002";
    replacement.location = "AMSTERDAM-001";
    replacement.capacity = 50;
    replacement.stock = 10;

    when(warehouseStore.findByBusinessUnitCode("MWH.002")).thenReturn(existing);

    assertThrows(IllegalArgumentException.class, () -> useCase.replace(replacement));
  }
}

