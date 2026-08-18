package com.sdatfinals.backend.admin;

import com.sdatfinals.backend.user.UserResponse;
import com.sdatfinals.backend.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers().stream().map(UserResponse::from).toList();
    }

    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Map.of("success", true);
    }

    @PatchMapping("/{id}/role")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateRole(@PathVariable Long id, @RequestBody RoleUpdateRequest request) {
        return UserResponse.from(userService.updateRole(id, request.role()));
    }
}
