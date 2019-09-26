package me.june.springsecurity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SignUpControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void 회원가입폼 () throws Exception {
        this.mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("_csrf")))
                .andDo(print());
    }

    @Test
    public void 회원가입_실패 () throws Exception {
        this.mockMvc.perform(post("/signup")
                .param("username", "june")
                .param("password", "1234")
                )
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    public void 회원가입_성공 () throws Exception {
        this.mockMvc.perform(post("/signup")
                    .param("username", "june")
                    .param("password", "1234")
                    .with(csrf()) // csrfToken을 넣어줌 Security Test
                )
                .andExpect(status().is3xxRedirection())
                .andDo(print());
    }
}
