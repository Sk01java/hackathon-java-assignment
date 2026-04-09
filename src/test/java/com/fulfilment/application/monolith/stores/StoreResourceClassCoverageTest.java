package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StoreResourceClassCoverageTest {

  private StoreResource resource;

  @BeforeEach
  @Transactional
  void setUp() {
    Store.deleteAll();
    resource = new StoreResource();

    @SuppressWarnings("unchecked")
    Event<StoreCreatedEvent> createdEvent = (Event<StoreCreatedEvent>) mock(Event.class);
    @SuppressWarnings("unchecked")
    Event<StoreUpdatedEvent> updatedEvent = (Event<StoreUpdatedEvent>) mock(Event.class);

    resource.storeCreatedEvent = createdEvent;
    resource.storeUpdatedEvent = updatedEvent;
    resource.legacyStoreManagerGateway = mock(LegacyStoreManagerGateway.class);
  }

  private Store store(String name, int qty) {
    Store s = new Store(name);
    s.quantityProductsInStock = qty;
    return s;
  }

  @Test
  @Transactional
  void testGetAndCreate() {
    Store created = store("A", 10);
    Response response = resource.create(created);
    assertEquals(201, response.getStatus());

    List<Store> stores = resource.get();
    assertEquals(1, stores.size());
    assertEquals("A", stores.get(0).name);

    Store invalid = store("B", 5);
    invalid.id = 1L;
    assertThrows(WebApplicationException.class, () -> resource.create(invalid));
  }

  @Test
  @Transactional
  void testGetSingleUpdateAndDeletePaths() {
    Store s = store("S", 3);
    s.persist();

    assertNotNull(resource.getSingle(s.id));
    assertThrows(WebApplicationException.class, () -> resource.getSingle(999999L));

    Store updated = store("SU", 7);
    Store updatedResult = resource.update(s.id, updated);
    assertEquals("SU", updatedResult.name);

    assertThrows(WebApplicationException.class, () -> resource.update(999999L, updated));
    assertThrows(WebApplicationException.class, () -> resource.update(s.id, store(null, 1)));

    Response deleted = resource.delete(s.id);
    assertEquals(204, deleted.getStatus());
    assertThrows(WebApplicationException.class, () -> resource.delete(999999L));
  }

  @Test
  @Transactional
  void testPatchBranches() {
    Store nonZero = store("Patch", 5);
    nonZero.persist();

    Store patch = store("Patched", 8);
    Store patched = resource.patch(nonZero.id, patch);
    assertEquals("Patched", patched.name);
    assertEquals(8, patched.quantityProductsInStock);

    Store zeroQty = store("Zero", 0);
    zeroQty.persist();

    Store patchZero = store("ShouldNotRename", 3);
    Store patchedZero = resource.patch(zeroQty.id, patchZero);
    assertEquals("ShouldNotRename", patchedZero.name);
    assertEquals(0, patchedZero.quantityProductsInStock);

    assertThrows(WebApplicationException.class, () -> resource.patch(999999L, patch));
    assertThrows(WebApplicationException.class, () -> resource.patch(nonZero.id, store(null, 1)));
  }
}

