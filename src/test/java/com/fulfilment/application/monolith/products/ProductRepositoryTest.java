package com.fulfilment.application.monolith.products;

import static org.junit.jupiter.api.Assertions.*;

import io.quarkus.panache.common.Sort;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("ProductRepository Tests")
class ProductRepositoryTest {

  @Inject ProductRepository productRepository;

  @BeforeEach
  @Transactional
  void setUp() {
	productRepository.deleteAll();
  }

  @Test
  @DisplayName("repository class can be instantiated")
  void testRepositoryClassInstantiation() {
	ProductRepository repository = new ProductRepository();
	assertNotNull(repository);
  }

  private Product product(String name, String description, String price, int stock) {
	Product product = new Product(name);
	product.description = description;
	product.price = price == null ? null : new BigDecimal(price);
	product.stock = stock;
	return product;
  }

  @Test
  @Transactional
  @DisplayName("persist and findById")
  void testPersistAndFindById() {
	Product saved = product("Desk", "Wooden desk", "199.99", 7);
	productRepository.persist(saved);

	Product found = productRepository.findById(saved.id);
	assertNotNull(found);
	assertEquals("Desk", found.name);
	assertEquals(0, new BigDecimal("199.99").compareTo(found.price));
	assertEquals(7, found.stock);
  }

  @Test
  @Transactional
  @DisplayName("listAll sorted by name")
  void testListAllSortedByName() {
	productRepository.persist(product("Zebra", "A", "10.00", 1));
	productRepository.persist(product("Apple", "B", "20.00", 2));

	List<Product> products = productRepository.listAll(Sort.by("name"));
	assertEquals(2, products.size());
	assertEquals("Apple", products.get(0).name);
	assertEquals("Zebra", products.get(1).name);
  }

  @Test
  @Transactional
  @DisplayName("update managed entity")
  void testUpdateManagedEntity() {
	Product entity = product("Chair", "Office", "49.50", 4);
	productRepository.persist(entity);

	Product managed = productRepository.findById(entity.id);
	managed.description = "Office Chair";
	managed.stock = 9;

	Product updated = productRepository.findById(entity.id);
	assertEquals("Office Chair", updated.description);
	assertEquals(9, updated.stock);
  }

  @Test
  @Transactional
  @DisplayName("delete by entity")
  void testDeleteByEntity() {
	Product entity = product("Lamp", null, null, 3);
	productRepository.persist(entity);

	assertTrue(productRepository.isPersistent(entity));
	productRepository.delete(entity);

	assertNull(productRepository.findById(entity.id));
	assertEquals(0, productRepository.count());
  }

  @Test
  @Transactional
  @DisplayName("find by field and unique name constraint")
  void testFindAndUniqueConstraint() {
	productRepository.persist(product("UniqueName", "First", "1.00", 1));
	Product found = productRepository.find("name", "UniqueName").firstResult();
	assertNotNull(found);
	assertEquals("First", found.description);

	productRepository.persist(product("UniqueName", "Second", "2.00", 2));
	assertThrows(PersistenceException.class, () -> productRepository.getEntityManager().flush());
  }
}