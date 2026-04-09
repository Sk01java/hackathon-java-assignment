package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import jakarta.ws.rs.WebApplicationException;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class WarehouseResourceImplUnitTest {

  private WarehouseRepository warehouseRepository;
  private CreateWarehouseOperation createWarehouseOperation;
  private ArchiveWarehouseOperation archiveWarehouseOperation;
  private ReplaceWarehouseOperation replaceWarehouseOperation;
  private WarehouseResourceImpl resource;

  @BeforeEach
  void setUp() {
    warehouseRepository = mock(WarehouseRepository.class);
    createWarehouseOperation = mock(CreateWarehouseOperation.class);
    archiveWarehouseOperation = mock(ArchiveWarehouseOperation.class);
    replaceWarehouseOperation = mock(ReplaceWarehouseOperation.class);

    resource = new WarehouseResourceImpl();
    inject("warehouseRepository", warehouseRepository);
    inject("createWarehouseOperation", createWarehouseOperation);
    inject("archiveWarehouseOperation", archiveWarehouseOperation);
    inject("replaceWarehouseOperation", replaceWarehouseOperation);
  }

  private void inject(String fieldName, Object value) {
    try {
      Field field = WarehouseResourceImpl.class.getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(resource, value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  @DisplayName("listAllWarehousesUnits maps each domain warehouse into the API response")
  void listAllWarehousesUnitsMapsRepositoryResults() {
    Warehouse first = warehouse("MWH.001", "AMSTERDAM-001", 100, 10);
    Warehouse second = warehouse("MWH.002", "ZWOLLE-001", 40, 5);
    when(warehouseRepository.getAll()).thenReturn(List.of(first, second));

    List<com.warehouse.api.beans.Warehouse> response = resource.listAllWarehousesUnits();

    assertEquals(2, response.size());
    assertEquals("MWH.001", response.get(0).getBusinessUnitCode());
    assertEquals("ZWOLLE-001", response.get(1).getLocation());
  }

  @Test
  @DisplayName("createANewWarehouseUnit maps request values and returns the created warehouse")
  void createWarehouseMapsAndReturnsResponse() {
    com.warehouse.api.beans.Warehouse request = new com.warehouse.api.beans.Warehouse();
    request.setBusinessUnitCode("MWH.001");
    request.setLocation("AMSTERDAM-001");
    request.setCapacity(100);
    request.setStock(10);

    com.warehouse.api.beans.Warehouse response = resource.createANewWarehouseUnit(request);

    assertEquals("MWH.001", response.getBusinessUnitCode());
    assertEquals("AMSTERDAM-001", response.getLocation());
    assertEquals(100, response.getCapacity());
    assertEquals(10, response.getStock());

    ArgumentCaptor<Warehouse> captor = ArgumentCaptor.forClass(Warehouse.class);
    verify(createWarehouseOperation).create(captor.capture());
    assertEquals("MWH.001", captor.getValue().businessUnitCode);
    assertEquals(10, captor.getValue().stock);
  }

  @Test
  @DisplayName("createANewWarehouseUnit defaults stock to zero when omitted")
  void createWarehouseDefaultsMissingStockToZero() {
    com.warehouse.api.beans.Warehouse request = new com.warehouse.api.beans.Warehouse();
    request.setBusinessUnitCode("MWH.002");
    request.setLocation("AMSTERDAM-002");
    request.setCapacity(75);

    com.warehouse.api.beans.Warehouse response = resource.createANewWarehouseUnit(request);

    assertEquals(0, response.getStock());
    ArgumentCaptor<Warehouse> captor = ArgumentCaptor.forClass(Warehouse.class);
    verify(createWarehouseOperation).create(captor.capture());
    assertEquals(0, captor.getValue().stock);
  }

  @Test
  @DisplayName("createANewWarehouseUnit converts IllegalArgumentException into HTTP 400")
  void createWarehouseWrapsValidationErrors() {
    com.warehouse.api.beans.Warehouse request = new com.warehouse.api.beans.Warehouse();
    request.setBusinessUnitCode("MWH.003");
    request.setLocation("UNKNOWN");
    request.setCapacity(10);
    doThrow(new IllegalArgumentException("invalid warehouse")).when(createWarehouseOperation).create(any(Warehouse.class));

    WebApplicationException exception =
        assertThrows(WebApplicationException.class, () -> resource.createANewWarehouseUnit(request));

    assertEquals(400, exception.getResponse().getStatus());
    assertEquals("invalid warehouse", exception.getMessage());
  }

  @Test
  @DisplayName("getAWarehouseUnitByID returns the mapped warehouse when present")
  void getByIdReturnsWarehouseWhenFound() {
    when(warehouseRepository.findByBusinessUnitCode("MWH.010"))
        .thenReturn(warehouse("MWH.010", "HELMOND-001", 45, 9));

    com.warehouse.api.beans.Warehouse response = resource.getAWarehouseUnitByID("MWH.010");

    assertEquals("MWH.010", response.getBusinessUnitCode());
    assertEquals("HELMOND-001", response.getLocation());
    assertEquals(45, response.getCapacity());
    assertEquals(9, response.getStock());
  }

  @Test
  @DisplayName("getAWarehouseUnitByID returns HTTP 404 when the warehouse does not exist")
  void getByIdReturns404WhenMissing() {
    when(warehouseRepository.findByBusinessUnitCode("MWH.404")).thenReturn(null);

    WebApplicationException exception =
        assertThrows(WebApplicationException.class, () -> resource.getAWarehouseUnitByID("MWH.404"));

    assertEquals(404, exception.getResponse().getStatus());
    assertTrue(exception.getMessage().contains("MWH.404"));
  }

  @Test
  @DisplayName("archiveAWarehouseUnitByID delegates to the archive use case for an active warehouse")
  void archiveWarehouseDelegatesForExistingWarehouse() {
    Warehouse domainWarehouse = warehouse("MWH.020", "EINDHOVEN-001", 70, 15);
    when(warehouseRepository.findByBusinessUnitCode("MWH.020")).thenReturn(domainWarehouse);

    resource.archiveAWarehouseUnitByID("MWH.020");

    verify(archiveWarehouseOperation).archive(domainWarehouse);
  }

  @Test
  @DisplayName("archiveAWarehouseUnitByID returns HTTP 400 for archive validation failures")
  void archiveWarehouseWrapsValidationErrors() {
    Warehouse domainWarehouse = warehouse("MWH.021", "EINDHOVEN-001", 70, 15);
    when(warehouseRepository.findByBusinessUnitCode("MWH.021")).thenReturn(domainWarehouse);
    doThrow(new IllegalArgumentException("warehouse already archived"))
        .when(archiveWarehouseOperation)
        .archive(domainWarehouse);

    WebApplicationException exception =
        assertThrows(WebApplicationException.class, () -> resource.archiveAWarehouseUnitByID("MWH.021"));

    assertEquals(400, exception.getResponse().getStatus());
    assertEquals("warehouse already archived", exception.getMessage());
  }

  @Test
  @DisplayName("archiveAWarehouseUnitByID returns HTTP 404 when the warehouse does not exist")
  void archiveThrows404WhenMissing() {
    when(warehouseRepository.findByBusinessUnitCode("MWH.404")).thenReturn(null);

    WebApplicationException exception =
        assertThrows(WebApplicationException.class, () -> resource.archiveAWarehouseUnitByID("MWH.404"));

    assertEquals(404, exception.getResponse().getStatus());
    assertTrue(exception.getMessage().contains("MWH.404"));
  }

  @Test
  @DisplayName("replaceTheCurrentActiveWarehouse maps request values, uses the path code, and returns the persisted warehouse")
  void replaceReturnsUpdatedWarehouse() {
    com.warehouse.api.beans.Warehouse request = new com.warehouse.api.beans.Warehouse();
    request.setLocation("ZWOLLE-001");
    request.setCapacity(40);
    request.setStock(20);

    Warehouse updated = warehouse("MWH.100", "ZWOLLE-001", 40, 20);
    when(warehouseRepository.findByBusinessUnitCode("MWH.100")).thenReturn(updated);

    com.warehouse.api.beans.Warehouse response = resource.replaceTheCurrentActiveWarehouse("MWH.100", request);

    assertEquals("MWH.100", response.getBusinessUnitCode());
    assertEquals("ZWOLLE-001", response.getLocation());
    assertEquals(40, response.getCapacity());
    assertEquals(20, response.getStock());

    ArgumentCaptor<Warehouse> captor = ArgumentCaptor.forClass(Warehouse.class);
    verify(replaceWarehouseOperation).replace(captor.capture());
    assertEquals("MWH.100", captor.getValue().businessUnitCode);
    assertEquals(20, captor.getValue().stock);
  }

  @Test
  @DisplayName("replaceTheCurrentActiveWarehouse defaults stock to zero when omitted")
  void replaceDefaultsMissingStockToZero() {
    com.warehouse.api.beans.Warehouse request = new com.warehouse.api.beans.Warehouse();
    request.setLocation("TILBURG-001");
    request.setCapacity(40);

    when(warehouseRepository.findByBusinessUnitCode("MWH.101"))
        .thenReturn(warehouse("MWH.101", "TILBURG-001", 40, 0));

    com.warehouse.api.beans.Warehouse response = resource.replaceTheCurrentActiveWarehouse("MWH.101", request);

    assertEquals(0, response.getStock());
    ArgumentCaptor<Warehouse> captor = ArgumentCaptor.forClass(Warehouse.class);
    verify(replaceWarehouseOperation).replace(captor.capture());
    assertEquals(0, captor.getValue().stock);
  }

  @Test
  @DisplayName("replaceTheCurrentActiveWarehouse converts IllegalArgumentException into HTTP 400")
  void replaceWrapsValidationErrors() {
    com.warehouse.api.beans.Warehouse request = new com.warehouse.api.beans.Warehouse();
    request.setLocation("AMSTERDAM-001");
    request.setCapacity(10);

    doThrow(new IllegalArgumentException("replacement not allowed"))
        .when(replaceWarehouseOperation)
        .replace(any(Warehouse.class));

    WebApplicationException exception =
        assertThrows(
            WebApplicationException.class,
            () -> resource.replaceTheCurrentActiveWarehouse("MWH.102", request));

    assertEquals(400, exception.getResponse().getStatus());
    assertEquals("replacement not allowed", exception.getMessage());
  }

  private Warehouse warehouse(String businessUnitCode, String location, int capacity, int stock) {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = businessUnitCode;
    warehouse.location = location;
    warehouse.capacity = capacity;
    warehouse.stock = stock;
    return warehouse;
  }
}

