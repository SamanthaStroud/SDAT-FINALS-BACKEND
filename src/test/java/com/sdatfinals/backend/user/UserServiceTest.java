package com.sdatfinals.backend.user;

import com.sdatfinals.backend.auth.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void register_savesHashedPasswordWhenEmailIsUnique() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Ada Lovelace");
        request.setEmail("ada@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("ada@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.register(request);

        assertThat(result.getEmail()).isEqualTo("ada@example.com");
        assertThat(result.getPasswordHash()).isEqualTo("hashed-password");
        assertThat(result.getRole()).isEqualTo("user");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_throwsConflictWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Ada Lovelace");
        request.setEmail("ada@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("ada@example.com")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.register(request)
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(409);
        verify(userRepository, never()).save(any());
    }

    @Test
    void findByEmail_throwsNotFoundWhenMissing() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.findByEmail("missing@example.com")
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void deleteUser_throwsBadRequestWhenTargetIsAdmin() {
        User admin = new User();
        admin.setId(1L);
        admin.setRole("admin");

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.deleteUser(1L)
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(400);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void deleteUser_deletesWhenTargetIsRegularUser() {
        User user = new User();
        user.setId(2L);
        user.setRole("user");

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        userService.deleteUser(2L);

        verify(userRepository, times(1)).deleteById(2L);
    }

    @Test
    void updateRole_throwsBadRequestForInvalidRole() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.updateRole(1L, "superadmin")
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(400);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void updateRole_promotesUserToAdmin() {
        User user = new User();
        user.setId(3L);
        user.setRole("user");

        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateRole(3L, "admin");

        assertThat(result.getRole()).isEqualTo("admin");
    }

    @Test
    void getAllUsers_returnsAllUsersFromRepository() {
        when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
    }
}
