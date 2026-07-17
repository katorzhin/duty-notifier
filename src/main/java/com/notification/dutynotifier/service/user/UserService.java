package com.notification.dutynotifier.service.user;

import com.notification.dutynotifier.dto.response.UserResponse;
import com.notification.dutynotifier.dto.user.UserCreateRequest;
import com.notification.dutynotifier.dto.user.UserUpdateRequest;
import com.notification.dutynotifier.entity.auditLog.AuditAction;
import com.notification.dutynotifier.entity.auditLog.messages.SystemAuditMessages;
import com.notification.dutynotifier.entity.user.Role;
import com.notification.dutynotifier.entity.user.User;
import com.notification.dutynotifier.exception.SelfUserDeletionException;
import com.notification.dutynotifier.exception.SystemAdminModificationException;
import com.notification.dutynotifier.exception.UserAlreadyExistsException;
import com.notification.dutynotifier.exception.UserManagementAccessDeniedException;
import com.notification.dutynotifier.exception.UserNotFoundException;
import com.notification.dutynotifier.repository.accountRepository.UserRepository;
import com.notification.dutynotifier.service.auditLogService.AuditLogService;
import com.notification.dutynotifier.service.securityService.AuthenticatedUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final AuthenticatedUserService authenticatedUserService;

    public List<UserResponse> getAll() {
        User currentUser = authenticatedUserService.getCurrentUser();
        validateUserManagementAccess(currentUser);

        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getRole(),
                        user.isSystemAdmin()
                ))
                .toList();
    }

    public UserResponse create(UserCreateRequest request) {
        User currentUser = authenticatedUserService.getCurrentUser();
        validateUserManagementAccess(currentUser);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .systemAdmin(false)
                .build();

        User saved = userRepository.save(user);

        auditLogService.log(
                currentUser.getEmail(),
                AuditAction.USER_CREATED,
                SystemAuditMessages.created(saved.getEmail())
        );

        return new UserResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getRole(),
                saved.isSystemAdmin()
        );
    }

    public UserResponse update(Long id, UserUpdateRequest request) {
        User currentUser = authenticatedUserService.getCurrentUser();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        validateUserModificationAccess(currentUser, user);

        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        user.setEmail(request.getEmail());
        user.setRole(request.getRole());

        User saved = userRepository.save(user);

        auditLogService.log(
                currentUser.getEmail(),
                AuditAction.USER_UPDATED,
                SystemAuditMessages.updated(saved.getEmail())
        );

        return new UserResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getRole(),
                saved.isSystemAdmin()
        );
    }

    public void delete(Long id) {
        User currentUser = authenticatedUserService.getCurrentUser();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (user.getId().equals(currentUser.getId())) {
            throw new SelfUserDeletionException();
        }

        validateUserModificationAccess(currentUser, user);

        if (!currentUser.isSystemAdmin() && user.getRole() == Role.ADMIN) {
            throw new UserManagementAccessDeniedException();
        }

        userRepository.delete(user);

        auditLogService.log(
                currentUser.getEmail(),
                AuditAction.USER_DELETED,
                SystemAuditMessages.deleted(user.getEmail())
        );
    }

    private void validateUserManagementAccess(User currentUser) {
        if (currentUser.isSystemAdmin()) {
            return;
        }

        if (currentUser.getRole() != Role.ADMIN) {
            throw new UserManagementAccessDeniedException();
        }
    }

    private void validateUserModificationAccess(
            User currentUser,
            User targetUser) {

        if (targetUser.isSystemAdmin()) {
            throw new SystemAdminModificationException();
        }

        validateUserManagementAccess(currentUser);
    }
}