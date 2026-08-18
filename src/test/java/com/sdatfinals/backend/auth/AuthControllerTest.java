package com.sdatfinals.backend.auth;

import com.sdatfinals.backend.user.User;
import com.sdatfinals.backend.user.UserPrincipal;
import com.sdatfinals.backend.user.UserService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private SecurityContextRepository securityContextRepository;

    private User sampleUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Ada Lovelace");
        user.setEmail("ada@example.com");
        user.setRole("user");
        return user;
    }

    @Test
    void register_returns201WithCreatedUser() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Ada Lovelace");
        request.setEmail("ada@example.com");
        request.setPassword("password123");

        when(userService.register(any(RegisterRequest.class))).thenReturn(sampleUser());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value("ada@example.com"));
    }

    @Test
    void login_returns200WithUserOnValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("ada@example.com");
        request.setPassword("password123");

        User user = sampleUser();
        Authentication authResult = new UsernamePasswordAuthenticationToken(
                new UserPrincipal(user), null, new UserPrincipal(user).getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authResult);
        when(userService.findByEmail("ada@example.com")).thenReturn(user);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("ada@example.com"));

        verify(userService, times(1)).recordLogin(user);
        verify(securityContextRepository, times(1)).saveContext(any(), any(), any());
    }

    @Test
    void login_returns401WithErrorOnInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("ada@example.com");
        request.setPassword("wrong-password");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad creds"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid email or password"));

        verify(userService, never()).recordLogin(any());
    }

    @Test
    void me_returnsNullUserWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user").value(org.hamcrest.Matchers.nullValue()));
    }

    @Test
    void me_returnsCurrentUserWhenAuthenticated() throws Exception {
        User user = sampleUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new UserPrincipal(user), null, new UserPrincipal(user).getAuthorities());

        mockMvc.perform(get("/api/auth/me").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("ada@example.com"));
    }
}
