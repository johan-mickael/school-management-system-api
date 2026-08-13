package com.schoolmanagement.iam.presentation;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.schoolmanagement.iam.domain.PasswordHasher;
import com.schoolmanagement.iam.domain.Role;
import com.schoolmanagement.iam.domain.User;
import com.schoolmanagement.iam.domain.UserId;
import com.schoolmanagement.iam.domain.UserRepository;
import com.schoolmanagement.iam.domain.Username;
import com.schoolmanagement.shared.AbstractIntegrationTest;

@AutoConfigureMockMvc
@Transactional
class AuthControllerIT extends AbstractIntegrationTest {

  private static final Pattern TOKEN_FIELD = Pattern.compile("\"token\":\"([^\"]+)\"");

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository users;

  @Autowired
  private PasswordHasher passwordHasher;

  @BeforeEach
  void seedUsers() {
    users.save(User.register(
        UserId.generate(), new Username("admin.it"), passwordHasher.hash("secret123"), Role.ADMIN, null));
    users.save(User.register(
        UserId.generate(), new Username("student.it"), passwordHasher.hash("secret123"), Role.STUDENT, null));
  }

  @Test
  void login_issues_a_token_for_valid_credentials() throws Exception {
    mockMvc.perform(post("/api/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"username":"admin.it","password":"secret123"}"""))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isNotEmpty());
  }

  @Test
  void login_rejects_wrong_password() throws Exception {
    mockMvc.perform(post("/api/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"username":"admin.it","password":"wrong"}"""))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithAnonymousUser
  void protected_endpoint_rejects_missing_token() throws Exception {
    mockMvc.perform(get("/api/v1/students/" + UUID.randomUUID()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void protected_endpoint_rejects_wrong_role() throws Exception {
    String token = login("student.it", "secret123");

    mockMvc.perform(post("/api/v1/students")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"studentNumber":"STU-2025-0001","firstName":"Ada","lastName":"Lovelace","email":"ada@example.com"}"""))
        .andExpect(status().isForbidden());
  }

  @Test
  void protected_endpoint_accepts_the_right_role() throws Exception {
    String token = login("admin.it", "secret123");

    mockMvc.perform(post("/api/v1/students")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"studentNumber":"STU-2025-0002","firstName":"Grace","lastName":"Hopper","email":"grace@example.com"}"""))
        .andExpect(status().isCreated());
  }

  private String login(String username, String password) throws Exception {
    String body = mockMvc.perform(post("/api/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"%s\",\"password\":\"%s\"}".formatted(username, password)))
        .andReturn().getResponse().getContentAsString();

    Matcher matcher = TOKEN_FIELD.matcher(body);
    if (!matcher.find()) {
      throw new IllegalStateException("token not found in response: " + body);
    }
    return matcher.group(1);
  }
}
