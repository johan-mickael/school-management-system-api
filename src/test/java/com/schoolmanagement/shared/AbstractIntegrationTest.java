package com.schoolmanagement.shared;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
public abstract class AbstractIntegrationTest {

  private static final String SIBLING_NETWORK = System.getenv("TESTCONTAINERS_DOCKER_NETWORK");

  static final PostgreSQLContainer POSTGRES;

  static {
    POSTGRES = SIBLING_NETWORK == null
        ? new PostgreSQLContainer("postgres:17")
        : new PostgreSQLContainer("postgres:17").withNetworkMode(SIBLING_NETWORK);
    POSTGRES.start();
  }

  @DynamicPropertySource
  static void datasourceProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", AbstractIntegrationTest::jdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }

  private static String jdbcUrl() {
    if (SIBLING_NETWORK == null) {
      return POSTGRES.getJdbcUrl();
    }
    String containerHost = POSTGRES.getContainerInfo().getName().substring(1);
    return "jdbc:postgresql://%s:5432/%s".formatted(containerHost, POSTGRES.getDatabaseName());
  }
}
