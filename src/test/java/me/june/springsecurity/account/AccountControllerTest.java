package me.june.springsecurity.account;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Test
    @WithAnonymousUser
    public void index_anonymous () throws Exception {
        mockMvc.perform(get("/")) // 인증되지 않은 유저
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @WithUser
    public void index_user () throws Exception {
        mockMvc.perform(get("/")
//                    .with(user("june").roles("USER"))
                // 유저 인증 정보를 Mocking 하고 테스트를 진행한다 (데이터베이스에는 존재하지않음. 로그인한 상태라고 가정)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @WithUser
    public void admin_user () throws Exception {
        mockMvc.perform(get("/admin")
                    .with(user("june").roles("USER")))
                .andDo(print())
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void admin_admin () throws Exception {
        mockMvc.perform(get("/admin")
                    .with(user("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isOk());
    }


    // 테스트코드에서 데이터베이스에 변경이 일어나는 경우 Transactional 을 사용하는것이 좋다.
    // 테스트가 끝난뒤에 롤백된다.
    // 다른 테스트에 영향을 주지않는 독립적인 테스트이다.
    // 다른 테스트에 영향을 주면 좋지 않은 테스트이다.
    // javax, spring 모두 사용가능
    // spring 도 java 표준을 지키고 있으며 spring 이 제공하는 기능이 좀더 많음
    @Test
    @Transactional
    public void login () throws Exception {
        // given
        final String username = "june";
        final String password = "1234";
        Account createdAccount = createUser(username, password);

        //when & then
        mockMvc.perform(formLogin().user(username).password(password))
            .andExpect(authenticated());
    }


    @Test
    @Transactional
    public void bad_credentials () throws Exception {
        // given
        final String username = "june";
        final String password = "1234";
        Account createdAccount = createUser(username, password);

        //when & then
        mockMvc.perform(formLogin().user(username).password("!2345678"))
                .andExpect(unauthenticated());
    }


    private Account createUser(String username, String password) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setRole("USER");
        return accountService.createAccount(account);
    }
}
