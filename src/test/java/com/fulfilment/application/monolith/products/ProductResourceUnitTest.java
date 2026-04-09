package com.fulfilment.application.monolith.products;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.panache.common.Sort;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProductResource Unit Tests")
class ProductResourceUnitTest {

  private ProductRepository productRepository;
  private ProductResource resource;

  @BeforeEach
  void setUp() {
    productRepository = mock(ProductRepository.class);
    resource = new ProductResource();
    resource.productRepository = productRepository;
  }

  private Product product(String name, String description, String price, int stock) {
    Product product = new Product(name);
    product.description = description;
    product.price = new BigDecimal(price);
    product.stock = stock;
    return product;
  }

  @Test
  @DisplayName("get returns sorted list")
  void testGet() {
    when(productRepository.listAll(any(Sort.class)))
        .thenReturn(List.of(product("A", "d", "1.00", 1), product("B", "d", "2.00", 2)));

    List<Product> result = resource.get();

    assertEquals(2, result.size());
    verify(productRepository).listAll(any(Sort.class));
  }

  @Test
  @DisplayName("getSingle returns entity")
  void testGetSingle() {
    Product stored = product("Desk", "Wood", "12.00", 2);
    when(productRepository.findById(10L)).thenReturn(stored);

    Product result = resource.getSingle(10L);

    assertEquals("Desk", result.name);
  }

  @Test
  @DisplayName("getSingle throws 404 when missing")
  void testGetSingleMissing() {
    when(productRepository.findById(99L)).thenReturn(null);

    WebApplicationException exception = assertThrows(WebApplicationException.class, () -> resource.getSingle(99L));

    assertEquals(404, exception.getResponse().getStatus());
  }

  @Test
  @DisplayName("create rejects payload with id")
  void testCreateWithIdRejected() {
    Product payload = product("Desk", "Wood", "12.00", 2);
    payload.id = 5L;

    WebApplicationException exception = assertThrows(WebApplicationException.class, () -> resource.create(payload));

    assertEquals(422, exception.getResponse().getStatus());
  }

  @Test
  @DisplayName("create persists and returns 201")
  void testCreateSuccess() {
    Product payload = product("Desk", "Wood", "12.00", 2);

    Response response = resource.create(payload);

    assertEquals(201, response.getStatus());
    assertSame(payload, response.getEntity());
    verify(productRepository).persist(payload);
  }

  @Test
  @DisplayName("update rejects null name")
  void testUpdateNullName() {
    Product payload = product(null, "Wood", "12.00", 2);

    WebApplicationException exception =
        assertThrows(WebApplicationException.class, () -> resource.update(1L, payload));

    assertEquals(422, exception.getResponse().getStatus());
  }

  @Test
  @DisplayName("update rejects missing entity")
  void testUpdateMissing() {
    Product payload = product("Desk", "Wood", "12.00", 2);
    when(productRepository.findById(7L)).thenReturn(null);

    WebApplicationException exception =
        assertThrows(WebApplicationException.class, () -> resource.update(7L, payload));

    assertEquals(404, exception.getResponse().getStatus());
  }

  @Test
  @DisplayName("update copies all fields")
  void testUpdateSuccess() {
    Product existing = product("Old", "Old desc", "10.00", 1);
    Product payload = product("New", "New desc", "99.99", 9);
    when(productRepository.findById(3L)).thenReturn(existing);

    Product updated = resource.update(3L, payload);

    assertEquals("New", updated.name);
    assertEquals("New desc", updated.description);
    assertEquals(0, new BigDecimal("99.99").compareTo(updated.price));
    assertEquals(9, updated.stock);
    verify(productRepository).persist(existing);
  }

  @Test
  @DisplayName("delete removes entity")
  void testDeleteSuccess() {
    Product existing = product("Old", "Old desc", "10.00", 1);
    when(productRepository.findById(3L)).thenReturn(existing);

    Response response = resource.delete(3L);

    assertEquals(204, response.getStatus());
    verify(productRepository).delete(existing);
  }

  @Test
  @DisplayName("delete returns 404 when missing")
  void testDeleteMissing() {
    when(productRepository.findById(33L)).thenReturn(null);

    WebApplicationException exception = assertThrows(WebApplicationException.class, () -> resource.delete(33L));

    assertEquals(404, exception.getResponse().getStatus());
  }

  @Test
  @DisplayName("error mapper returns web app exception status")
  void testErrorMapperWebApplicationException() {
    ProductResource.ErrorMapper mapper = new ProductResource.ErrorMapper();
    mapper.objectMapper = new ObjectMapper();

    Response response = mapper.toResponse(new WebApplicationException("Not found", 404));
    ObjectNode body = (ObjectNode) response.getEntity();

    assertEquals(404, response.getStatus());
    assertEquals(404, body.get("code").asInt());
    assertEquals("Not found", body.get("error").asText());
  }

  @Test
  @DisplayName("error mapper returns 500 with null message")
  void testErrorMapperGenericExceptionWithoutMessage() {
    ProductResource.ErrorMapper mapper = new ProductResource.ErrorMapper();
    mapper.objectMapper = new ObjectMapper();

    RuntimeException exception =
        new RuntimeException() {
          @Override
          public String getMessage() {
            return null;
          }
        };

    Response response = mapper.toResponse(exception);
    ObjectNode body = (ObjectNode) response.getEntity();

    assertEquals(500, response.getStatus());
    assertEquals(500, body.get("code").asInt());
    assertNull(body.get("error"));
  }
}

