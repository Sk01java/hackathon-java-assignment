package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class LegacyAndObserverUnitTest {

  @Test
  void testLegacyGatewayMethodsDoNotThrow() {
    LegacyStoreManagerGateway gateway = new LegacyStoreManagerGateway();
    Store store = new Store("Legacy");
    store.quantityProductsInStock = 3;

    assertDoesNotThrow(() -> gateway.createStoreOnLegacySystem(store));
    assertDoesNotThrow(() -> gateway.updateStoreOnLegacySystem(store));
    // Covers gateway error-handling branch (null store triggers exception inside writeToFile).
    assertDoesNotThrow(() -> gateway.createStoreOnLegacySystem(null));
  }

  @Test
  void testObserverDelegatesToGateway() throws Exception {
    LegacyStoreManagerGateway gateway = mock(LegacyStoreManagerGateway.class);
    StoreEventObserver observer = new StoreEventObserver();

    Field field = StoreEventObserver.class.getDeclaredField("legacyStoreManagerGateway");
    field.setAccessible(true);
    field.set(observer, gateway);

    Store store = new Store("Obs");
    observer.onStoreCreated(new StoreCreatedEvent(store));
    observer.onStoreUpdated(new StoreUpdatedEvent(store));

    verify(gateway).createStoreOnLegacySystem(store);
    verify(gateway).updateStoreOnLegacySystem(store);
  }
}

