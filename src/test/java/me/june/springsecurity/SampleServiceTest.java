package me.june.springsecurity;


import me.june.springsecurity.account.Account;
import me.june.springsecurity.account.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SampleServiceTest {

    @Autowired
    SampleService sampleService;

    @Autowired
    AccountService accountService;

    /* AuthenticationManager 는 기본으로는 Bean으로 노출되어 있찌 않음. */
    @Autowired
    AuthenticationManager authenticationManager;

    @Test
    public void dashboard () {
        Account account = new Account();
        account.setRole("USER");
        account.setUsername("june");
        account.setPassword("1234");
        accountService.createAccount(account);

        UserDetails userDetails = accountService.loadUserByUsername(account.getUsername());

        /* 데스크탑 애플리케이션의 경우에는 token을 직접 생성해 주어야한다. */
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, "1234");

        /* authenticationManager를 직접사용하여 로그인 처리를 한다. */
        authenticationManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(token);

        sampleService.dashboard();
    }
}
