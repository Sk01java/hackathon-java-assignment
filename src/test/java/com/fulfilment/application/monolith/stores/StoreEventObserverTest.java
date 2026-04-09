package com.fulfilment.application.monolith.stores;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class StoreEventObserverTest {

  @Inject
  StoreEventObserver storeEventObserver;

  @InjectMock
  LegacyStoreManagerGateway legacyGateway;

  private Store testStore;

  @BeforeEach
  @Transactional
  public void setup() {
    Store.deleteAll();
    
    testStore = new Store();
    testStore.name = "Test Store";
    testStore.quantityProductsInStock = 100;
  }

  @Test
  @Transactional
  public void testStoreCreatedEventCallsLegacyGateway() {
    Mockito.reset(legacyGateway);

    StoreCreatedEvent event = new StoreCreatedEvent(testStore);
    storeEventObserver.onStoreCreated(event);
    
    verify(legacyGateway, times(1)).createStoreOnLegacySystem(any(Store.class));
  }

  @Test
  @Transactional
  public void testStoreUpdatedEventCallsLegacyGateway() {
    Mockito.reset(legacyGateway);

    StoreUpdatedEvent event = new StoreUpdatedEvent(testStore);
    storeEventObserver.onStoreUpdated(event);
    
    verify(legacyGateway, times(1)).updateStoreOnLegacySystem(any(Store.class));
  }
}
