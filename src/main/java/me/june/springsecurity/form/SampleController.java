package me.june.springsecurity.form;

import lombok.RequiredArgsConstructor;
import me.june.springsecurity.SampleService;
import me.june.springsecurity.account.AccountContext;
import me.june.springsecurity.account.AccountRepository;
import me.june.springsecurity.common.SecurityLogger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.concurrent.Callable;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 05/09/2019
 * Time: 9:35 오후
 **/
@Controller
@RequiredArgsConstructor
public class SampleController {

    private final AccountRepository accountRepository;
    private final SampleService sampleService;

    @GetMapping("/")
    public String index (Model model, Principal principal) {
        if (principal == null) {
            model.addAttribute("message", "Hello Spring Security");
        } else {
            model.addAttribute("message", "Hello Index" + principal.getName());
        }
        return "index";
    }

    @GetMapping("/info")
    public String info (Model model) {
        model.addAttribute("message", "Hello Info");
        return "info";
    }

    @GetMapping("/dashboard")
    public String dashboard (Model model, Principal principal) {
        model.addAttribute("message", "Hello " + principal.getName());
        /*
          AccountRepository에서 조회한 Account객체를 AccountContext에 저장한다.
        */
        AccountContext.setAccount(accountRepository.findByUsername(principal.getName()));
        sampleService.dashboard();
        return "dashboard";
    }

    @GetMapping("/user")
    public String user (Model model, Principal principal) {
        model.addAttribute("message", "Hello " + principal.getName());
        return "user";
    }

    @GetMapping("/admin")
    public String admin (Model model, Principal principal) {
        model.addAttribute("message", "Hello Admin" + principal.getName());
        return "admin";
    }

    @GetMapping("/async-handler")
    @ResponseBody
    public Callable<String> asyncHandler () {
        // Callable 내에서 처리할 일들을 처리하기 이전에 현재 요청을 받은 Thread를 반환한다.
        // Callable이 완료될때쯤 응답을 보낸다.. 2 페이즈로 나누어서 요청을 처리한다.
        // preprocessing
        SecurityLogger.log("MVC");
        // tomcat이 할당한 NIO Thread
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                // 별도의 Thread
                SecurityLogger.log("ASYNC");
                return "async-handler";
            }
        };
        // postprocessing
    }
}
