package com.schoolmanagement.shared;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class OpenApiSmokeIT extends AbstractIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void api_docs_list_the_known_resource_paths() throws Exception {
    mockMvc.perform(get("/v3/api-docs"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("/api/v1/students")))
        .andExpect(content().string(containsString("/api/v1/teachers")))
        .andExpect(content().string(containsString("/api/v1/courses")))
        .andExpect(content().string(containsString("/api/v1/auth/login")));
  }
}
