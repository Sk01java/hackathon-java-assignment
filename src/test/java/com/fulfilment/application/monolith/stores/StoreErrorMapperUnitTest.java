package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class StoreErrorMapperUnitTest {

  private StoreResource.ErrorMapper mapper() throws Exception {
    StoreResource.ErrorMapper mapper = new StoreResource.ErrorMapper();
    Field field = StoreResource.ErrorMapper.class.getDeclaredField("objectMapper");
    field.setAccessible(true);
    field.set(mapper, new ObjectMapper());
    return mapper;
  }

  @Test
  void testWebApplicationExceptionMapping() throws Exception {
    StoreResource.ErrorMapper mapper = mapper();

    Response response = mapper.toResponse(new WebApplicationException("Not found", 404));
    ObjectNode json = (ObjectNode) response.getEntity();

    assertEquals(404, response.getStatus());
    assertEquals(404, json.get("code").asInt());
    assertEquals("Not found", json.get("error").asText());
  }

  @Test
  void testGenericExceptionMappingWithoutMessage() throws Exception {
    StoreResource.ErrorMapper mapper = mapper();

    RuntimeException ex =
        new RuntimeException() {
          @Override
          public String getMessage() {
            return null;
          }
        };

    Response response = mapper.toResponse(ex);
    ObjectNode json = (ObjectNode) response.getEntity();

    assertEquals(500, response.getStatus());
    assertEquals(500, json.get("code").asInt());
    assertNull(json.get("error"));
  }
}

