package me.june.springsecurity.account;

import lombok.RequiredArgsConstructor;
import me.june.springsecurity.account.Account;
import me.june.springsecurity.account.AccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 08/09/2019
 * Time: 3:24 오후
 **/
@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    // Database에서 조회해서 UserDetails Type으로 Return 해줘야함
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException(username);
        }
        return User.builder()
                .username(account.getUsername())
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    public Account createAccount(Account account) {
        account.encodePassword(passwordEncoder);
        return accountRepository.save(account);
    }
}
