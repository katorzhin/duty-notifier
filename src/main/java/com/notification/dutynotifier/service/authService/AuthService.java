package com.notification.dutynotifier.service.authService;

import com.notification.dutynotifier.dto.auth.LoginRequest;
import com.notification.dutynotifier.dto.auth.LoginResponse;
import com.notification.dutynotifier.entity.account.Account;
import com.notification.dutynotifier.repository.accountRepository.AccountRepository;
import com.notification.dutynotifier.service.jwtService.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {

        Account account = accountRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() -> new RuntimeException(
                                        "Invalid credentials"));

        boolean matches = passwordEncoder.matches(
                        request.getPassword(),
                        account.getPassword());

        if (!matches) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(account.getEmail());

        return new LoginResponse(token, account.getRole().name());
    }
}