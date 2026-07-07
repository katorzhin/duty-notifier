package com.notification.dutynotifier.service.accountUserDetailsService;

import com.notification.dutynotifier.entity.account.Account;
import com.notification.dutynotifier.repository.accountRepository.AccountRepository;
import com.notification.dutynotifier.security.AccountUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Account account = accountRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(email));

        return new AccountUserDetails(account);
    }
}