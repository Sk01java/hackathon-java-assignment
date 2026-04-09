package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.*;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StoreResourceDirectTest {

  @Inject StoreResource storeResource;

  @BeforeEach
  @Transactional
  void setUp() {
    Store.deleteAll();
  }

  private Store store(String name, int qty) {
    Store store = new Store(name);
    store.quantityProductsInStock = qty;
    return store;
  }

  @Test
  @Transactional
  void testGetAndGetSingle() {
    Store saved = store("A", 10);
    saved.persist();

    List<Store> stores = storeResource.get();
    assertEquals(1, stores.size());
    assertEquals("A", stores.get(0).name);

    Store single = storeResource.getSingle(saved.id);
    assertEquals("A", single.name);
  }

  @Test
  void testGetSingleNotFound() {
    WebApplicationException ex =
        assertThrows(WebApplicationException.class, () -> storeResource.getSingle(999999L));
    assertEquals(404, ex.getResponse().getStatus());
  }

  @Test
  @Transactional
  void testCreateAndCreateWithIdValidation() {
    Response created = storeResource.create(store("C", 20));
    assertEquals(201, created.getStatus());

    Store invalid = store("X", 1);
    invalid.id = 1L;
    WebApplicationException ex =
        assertThrows(WebApplicationException.class, () -> storeResource.create(invalid));
    assertEquals(422, ex.getResponse().getStatus());
  }

  @Test
  @Transactional
  void testUpdateVariants() {
    Store saved = store("U", 10);
    saved.persist();

    Store updated = store("UU", 30);
    Store response = storeResource.update(saved.id, updated);
    assertEquals("UU", response.name);
    assertEquals(30, response.quantityProductsInStock);

    WebApplicationException missing =
        assertThrows(WebApplicationException.class, () -> storeResource.update(999999L, updated));
    assertEquals(404, missing.getResponse().getStatus());

    Store invalid = store(null, 5);
    WebApplicationException nameMissing =
        assertThrows(WebApplicationException.class, () -> storeResource.update(saved.id, invalid));
    assertEquals(422, nameMissing.getResponse().getStatus());
  }

  @Test
  @Transactional
  void testPatchAndDelete() {
    Store saved = store("P", 5);
    saved.persist();

    Store patch = store("PP", 7);
    Store patched = storeResource.patch(saved.id, patch);
    assertEquals("PP", patched.name);
    assertEquals(7, patched.quantityProductsInStock);

    // When incoming quantity is zero, name remains unchanged per resource logic.
    Store keepName = store("NEW", 0);
    Store patchedKeepName = storeResource.patch(saved.id, keepName);
    assertEquals("PP", patchedKeepName.name);

    Response deleted = storeResource.delete(saved.id);
    assertEquals(204, deleted.getStatus());

    WebApplicationException missingDelete =
        assertThrows(WebApplicationException.class, () -> storeResource.delete(999999L));
    assertEquals(404, missingDelete.getResponse().getStatus());
  }
}

