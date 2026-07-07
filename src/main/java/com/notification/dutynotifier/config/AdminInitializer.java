package com.notification.dutynotifier.config;

import com.notification.dutynotifier.entity.account.Account;
import com.notification.dutynotifier.entity.account.Role;
import com.notification.dutynotifier.repository.accountRepository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (accountRepository.count() == 0) {

            Account admin = Account.builder()
                    .email("admin@test.com")
                    .password(passwordEncoder.encode("123456"))
                    .role(Role.ADMIN)
                    .build();

            accountRepository.save(admin);

            Account user = Account.builder()
                    .email("user@test.com")
                    .password(passwordEncoder.encode("123456"))
                    .role(Role.USER)
                    .build();

            accountRepository.save(user);
        }
    }
}