package com.notification.dutynotifier.controller.userController;

import com.notification.dutynotifier.dto.response.UserResponse;
import com.notification.dutynotifier.dto.user.UserCreateRequest;
import com.notification.dutynotifier.dto.user.UserUpdateRequest;
import com.notification.dutynotifier.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserResponse create(@RequestBody @Valid UserCreateRequest request) {
        return userService.create(request);
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @RequestBody @Valid UserUpdateRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    @GetMapping
    public List<UserResponse> getAll() {
        return userService.getAll();
    }


}