package com.fulfilment.application.monolith.products;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProductRepository Unit Tests")
class ProductRepositoryUnitTest {

  @Test
  @DisplayName("constructor")
  void testConstructor() {
    ProductRepository repository = new ProductRepository();
    assertNotNull(repository);
  }
}

