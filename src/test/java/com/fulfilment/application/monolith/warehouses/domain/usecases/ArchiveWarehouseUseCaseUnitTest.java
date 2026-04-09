package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArchiveWarehouseUseCaseUnitTest {

  private WarehouseStore warehouseStore;
  private ArchiveWarehouseUseCase useCase;

  @BeforeEach
  void setUp() {
    warehouseStore = mock(WarehouseStore.class);
    useCase = new ArchiveWarehouseUseCase(warehouseStore);
  }

  @Test
  void testArchiveSuccess() {
    Warehouse input = new Warehouse();
    input.businessUnitCode = "MWH.001";

    Warehouse existing = new Warehouse();
    existing.businessUnitCode = "MWH.001";

    when(warehouseStore.findByBusinessUnitCode("MWH.001")).thenReturn(existing);

    useCase.archive(input);

    assertNotNull(existing.archivedAt);
    verify(warehouseStore).update(existing);
  }

  @Test
  void testArchiveFailsWhenWarehouseMissing() {
    Warehouse input = new Warehouse();
    input.businessUnitCode = "MWH.404";

    when(warehouseStore.findByBusinessUnitCode("MWH.404")).thenReturn(null);

    assertThrows(IllegalArgumentException.class, () -> useCase.archive(input));
  }

  @Test
  void testArchiveFailsWhenAlreadyArchived() {
    Warehouse input = new Warehouse();
    input.businessUnitCode = "MWH.002";

    Warehouse existing = new Warehouse();
    existing.businessUnitCode = "MWH.002";
    existing.archivedAt = java.time.LocalDateTime.now();

    when(warehouseStore.findByBusinessUnitCode("MWH.002")).thenReturn(existing);

    assertThrows(IllegalArgumentException.class, () -> useCase.archive(input));
    verify(warehouseStore, never()).update(any());
  }
}

