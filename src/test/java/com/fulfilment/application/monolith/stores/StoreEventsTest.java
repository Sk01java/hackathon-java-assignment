package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.*;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for Store event objects (StoreCreatedEvent and StoreUpdatedEvent).
 * Tests event creation, store reference preservation, and null handling.
 */
@QuarkusTest
@DisplayName("Store Events Tests")
class StoreEventsTest {

  private Store testStore;

  @BeforeEach
  @Transactional
  void setUp() {
    Store.deleteAll();
    testStore = new Store("Test Store");
    testStore.quantityProductsInStock = 100;
    testStore.persist();
  }

  // ========== StoreCreatedEvent Tests ==========

  @Test
  @DisplayName("StoreCreatedEvent creation with valid store")
  void testStoreCreatedEventCreation() {
    StoreCreatedEvent event = new StoreCreatedEvent(testStore);
    assertNotNull(event);
    assertNotNull(event.getStore());
  }

  @Test
  @DisplayName("StoreCreatedEvent preserves store reference")
  void testStoreCreatedEventStoreAccess() {
    StoreCreatedEvent event = new StoreCreatedEvent(testStore);
    Store retrievedStore = event.getStore();
    
    assertNotNull(retrievedStore);
    assertEquals(testStore.id, retrievedStore.id);
    assertEquals("Test Store", retrievedStore.name);
    assertEquals(100, retrievedStore.quantityProductsInStock);
  }

  @Test
  @DisplayName("StoreCreatedEvent with null store")
  void testStoreCreatedEventWithNullStore() {
    StoreCreatedEvent event = new StoreCreatedEvent(null);
    assertNotNull(event);
    assertNull(event.getStore());
  }

  @Test
  @DisplayName("StoreCreatedEvent getStore method multiple calls")
  void testStoreCreatedEventGetStoreMultipleCalls() {
    StoreCreatedEvent event = new StoreCreatedEvent(testStore);
    Store store1 = event.getStore();
    Store store2 = event.getStore();
    
    assertSame(store1, store2);
    assertEquals(store1.id, store2.id);
  }

  // ========== StoreUpdatedEvent Tests ==========

  @Test
  @DisplayName("StoreUpdatedEvent creation with valid store")
  void testStoreUpdatedEventCreation() {
    StoreUpdatedEvent event = new StoreUpdatedEvent(testStore);
    assertNotNull(event);
    assertNotNull(event.getStore());
  }

  @Test
  @DisplayName("StoreUpdatedEvent preserves store reference")
  void testStoreUpdatedEventStoreAccess() {
    StoreUpdatedEvent event = new StoreUpdatedEvent(testStore);
    Store retrievedStore = event.getStore();
    
    assertNotNull(retrievedStore);
    assertEquals(testStore.id, retrievedStore.id);
    assertEquals("Test Store", retrievedStore.name);
    assertEquals(100, retrievedStore.quantityProductsInStock);
  }

  @Test
  @DisplayName("StoreUpdatedEvent with null store")
  void testStoreUpdatedEventWithNullStore() {
    StoreUpdatedEvent event = new StoreUpdatedEvent(null);
    assertNotNull(event);
    assertNull(event.getStore());
  }

  @Test
  @DisplayName("StoreUpdatedEvent getStore method multiple calls")
  void testStoreUpdatedEventGetStoreMultipleCalls() {
    StoreUpdatedEvent event = new StoreUpdatedEvent(testStore);
    Store store1 = event.getStore();
    Store store2 = event.getStore();
    
    assertSame(store1, store2);
    assertEquals(store1.id, store2.id);
  }

  // ========== Store Entity Tests ==========

  @Test
  @DisplayName("Store entity default constructor")
  void testStoreDefaultConstructor() {
    Store store = new Store();
    assertNotNull(store);
    assertNull(store.name);
    assertEquals(0, store.quantityProductsInStock);
  }

  @Test
  @DisplayName("Store entity constructor with name")
  void testStoreConstructorWithName() {
    Store store = new Store("New Store");
    assertNotNull(store);
    assertEquals("New Store", store.name);
    assertEquals(0, store.quantityProductsInStock);
  }

  @Test
  @DisplayName("Store entity field modification")
  @Transactional
  void testStoreFieldModification() {
    Store store = new Store();
    store.name = "Modified";
    store.quantityProductsInStock = 50;
    
    assertEquals("Modified", store.name);
    assertEquals(50, store.quantityProductsInStock);
  }

  @Test
  @DisplayName("Store entity persistence")
  @Transactional
  void testStorePersistence() {
    Store store = new Store("Persist Test");
    store.quantityProductsInStock = 75;
    store.persist();
    
    assertNotNull(store.id);
    
    Store found = Store.findById(store.id);
    assertNotNull(found);
    assertEquals("Persist Test", found.name);
    assertEquals(75, found.quantityProductsInStock);
  }

  @Test
  @DisplayName("Store entity name unique constraint")
  @Transactional
  void testStoreNameUniqueness() {
    Store store1 = new Store("Unique Name");
    store1.quantityProductsInStock = 10;
    store1.persist();
    
    Store store2 = new Store("Unique Name");
    store2.quantityProductsInStock = 20;

    store2.persist();
    assertThrows(Exception.class, () -> Store.getEntityManager().flush());
  }

  @Test
  @DisplayName("Store listAll method")
  @Transactional
  void testStoreListAll() {
    Store store1 = new Store("Store 1");
    store1.quantityProductsInStock = 10;
    store1.persist();
    
    Store store2 = new Store("Store 2");
    store2.quantityProductsInStock = 20;
    store2.persist();
    
    long count = Store.count();
    assertTrue(count >= 2);
  }

  @Test
  @DisplayName("Store delete operation")
  @Transactional
  void testStoreDelete() {
    Store store = new Store("To Delete");
    store.quantityProductsInStock = 50;
    store.persist();
    
    Long storeId = store.id;
    store.delete();
    
    Store found = Store.findById(storeId);
    assertNull(found);
  }

  @Test
  @DisplayName("Event with modified store")
  @Transactional
  void testEventWithModifiedStore() {
    Store store = new Store("Original");
    store.quantityProductsInStock = 10;
    store.persist();
    
    store.name = "Modified";
    store.quantityProductsInStock = 20;
    
    StoreUpdatedEvent event = new StoreUpdatedEvent(store);
    assertEquals("Modified", event.getStore().name);
    assertEquals(20, event.getStore().quantityProductsInStock);
  }
}

