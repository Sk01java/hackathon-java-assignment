package com.fulfilment.application.monolith.health;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class WarehouseServiceReadinessCheck implements HealthCheck {

  @Override
  public HealthCheckResponse call() {
    return HealthCheckResponse.named("warehouse-service-readiness")
        .withData("service", "java-code-assignment")
        .up()
        .build();
  }
}

