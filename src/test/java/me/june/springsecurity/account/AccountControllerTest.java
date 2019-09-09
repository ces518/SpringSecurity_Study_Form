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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

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
}
