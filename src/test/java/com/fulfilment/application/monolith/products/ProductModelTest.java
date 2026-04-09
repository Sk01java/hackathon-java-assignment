package com.fulfilment.application.monolith.products;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Product Model Tests")
class ProductModelTest {

  @Test
  @DisplayName("default constructor creates empty product")
  void testDefaultConstructor() {
    Product product = new Product();

    assertNull(product.id);
    assertNull(product.name);
    assertNull(product.description);
    assertNull(product.price);
    assertEquals(0, product.stock);
  }

  @Test
  @DisplayName("name constructor sets name")
  void testNameConstructor() {
    Product product = new Product("Table");

    assertEquals("Table", product.name);
  }

  @Test
  @DisplayName("fields are assignable")
  void testFieldAssignment() {
    Product product = new Product("Chair");
    product.description = "Office";
    product.price = new BigDecimal("149.90");
    product.stock = 14;

    assertEquals("Office", product.description);
    assertEquals(0, new BigDecimal("149.90").compareTo(product.price));
    assertEquals(14, product.stock);
  }
}

