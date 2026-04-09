package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CreateWarehouseUseCaseTest {

  private WarehouseStore warehouseStore;
  private LocationResolver locationResolver;
  private CreateWarehouseUseCase useCase;

  @BeforeEach
  void setUp() {
	warehouseStore = mock(WarehouseStore.class);
	locationResolver = mock(LocationResolver.class);
	useCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);
  }

  @Test
  void testCreateSuccess() {
	Warehouse warehouse = new Warehouse();
	warehouse.businessUnitCode = "MWH.001";
	warehouse.location = "AMSTERDAM-001";
	warehouse.capacity = 50;
	warehouse.stock = 20;

	when(warehouseStore.findByBusinessUnitCode("MWH.001")).thenReturn(null);
	when(locationResolver.resolveByIdentifier("AMSTERDAM-001"))
		.thenReturn(new Location("AMSTERDAM-001", 5, 100));

	useCase.create(warehouse);

	assertNotNull(warehouse.createdAt);
	verify(warehouseStore).create(warehouse);
  }

  @Test
  void testCreateFailsWhenBusinessUnitExists() {
	Warehouse warehouse = new Warehouse();
	warehouse.businessUnitCode = "MWH.001";

	when(warehouseStore.findByBusinessUnitCode("MWH.001")).thenReturn(new Warehouse());

	assertThrows(IllegalArgumentException.class, () -> useCase.create(warehouse));
	verify(warehouseStore, never()).create(any());
  }

  @Test
  void testCreateFailsForInvalidLocation() {
	Warehouse warehouse = new Warehouse();
	warehouse.businessUnitCode = "MWH.002";
	warehouse.location = "INVALID";
	warehouse.capacity = 10;
	warehouse.stock = 5;

	when(warehouseStore.findByBusinessUnitCode("MWH.002")).thenReturn(null);
	when(locationResolver.resolveByIdentifier("INVALID")).thenReturn(null);

	assertThrows(IllegalArgumentException.class, () -> useCase.create(warehouse));
  }

  @Test
  void testCreateFailsWhenCapacityExceedsLocationLimit() {
	Warehouse warehouse = new Warehouse();
	warehouse.businessUnitCode = "MWH.004";
	warehouse.location = "AMSTERDAM-001";
	warehouse.capacity = 150;
	warehouse.stock = 50;

	when(warehouseStore.findByBusinessUnitCode("MWH.004")).thenReturn(null);
	when(locationResolver.resolveByIdentifier("AMSTERDAM-001"))
		.thenReturn(new Location("AMSTERDAM-001", 5, 100));

	assertThrows(IllegalArgumentException.class, () -> useCase.create(warehouse));
	verify(warehouseStore, never()).create(any());
  }

  @Test
  void testCreateFailsWhenStockExceedsCapacity() {
	Warehouse warehouse = new Warehouse();
	warehouse.businessUnitCode = "MWH.003";
	warehouse.location = "ZWOLLE-001";
	warehouse.capacity = 10;
	warehouse.stock = 11;

	when(warehouseStore.findByBusinessUnitCode("MWH.003")).thenReturn(null);
	when(locationResolver.resolveByIdentifier("ZWOLLE-001"))
		.thenReturn(new Location("ZWOLLE-001", 1, 40));

	assertThrows(IllegalArgumentException.class, () -> useCase.create(warehouse));
  }
}
