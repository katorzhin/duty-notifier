package com.notification.dutynotifier.service.passwordService;

import com.notification.dutynotifier.dto.user.ChangePasswordRequest;
import com.notification.dutynotifier.dto.user.ResetPasswordRequest;
import com.notification.dutynotifier.entity.auditLog.AuditAction;
import com.notification.dutynotifier.entity.auditLog.messages.SystemAuditMessages;
import com.notification.dutynotifier.entity.user.Role;
import com.notification.dutynotifier.entity.user.User;
import com.notification.dutynotifier.exception.InvalidPasswordException;
import com.notification.dutynotifier.exception.PasswordMismatchException;
import com.notification.dutynotifier.exception.SamePasswordException;
import com.notification.dutynotifier.exception.SystemAdminModificationException;
import com.notification.dutynotifier.exception.UserManagementAccessDeniedException;
import com.notification.dutynotifier.exception.UserNotFoundException;
import com.notification.dutynotifier.repository.accountRepository.UserRepository;
import com.notification.dutynotifier.service.auditLogService.AuditLogService;
import com.notification.dutynotifier.service.securityService.AuthenticatedUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final AuthenticatedUserService authenticatedUserService;

    public void changePassword(ChangePasswordRequest request) {

        User currentUser = authenticatedUserService.getCurrentUser();

        if (!passwordEncoder.matches(
                request.getOldPassword(),
                currentUser.getPassword())) {
            throw new InvalidPasswordException();
        }

        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }

        if (passwordEncoder.matches(
                request.getNewPassword(),
                currentUser.getPassword())) {
            throw new SamePasswordException();
        }

        currentUser.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(currentUser);

        auditLogService.log(
                currentUser.getEmail(),
                AuditAction.PASSWORD_CHANGED,
                SystemAuditMessages.passwordChanged(currentUser.getEmail())
        );
    }

    public void resetPassword(Long userId, ResetPasswordRequest request) {

        User currentUser = authenticatedUserService.getCurrentUser();

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (targetUser.getId().equals(currentUser.getId())) {
            throw new UserManagementAccessDeniedException();
        }

        if (targetUser.isSystemAdmin()) {
            throw new SystemAdminModificationException();
        }

        if (!currentUser.isSystemAdmin() && currentUser.getRole()==Role.USER) {
            throw new UserManagementAccessDeniedException();
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }

        targetUser.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        userRepository.save(targetUser);

        auditLogService.log(
                currentUser.getEmail(),
                AuditAction.PASSWORD_RESET,
                SystemAuditMessages.passwordReset(
                        currentUser.getEmail(),
                        targetUser.getEmail()
                )
        );
    }
}