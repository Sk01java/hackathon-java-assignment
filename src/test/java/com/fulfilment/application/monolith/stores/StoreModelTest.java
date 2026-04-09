package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StoreModelTest {

  @Test
  void testConstructorsAndFields() {
    Store empty = new Store();
    assertNull(empty.name);
    assertEquals(0, empty.quantityProductsInStock);

    Store named = new Store("Demo");
    assertEquals("Demo", named.name);
    named.quantityProductsInStock = 12;
    assertEquals(12, named.quantityProductsInStock);
  }
}

