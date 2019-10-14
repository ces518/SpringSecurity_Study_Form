package me.june.springsecurity.form;

import lombok.RequiredArgsConstructor;
import me.june.springsecurity.SampleService;
import me.june.springsecurity.account.Account;
import me.june.springsecurity.account.AccountContext;
import me.june.springsecurity.account.AccountRepository;
import me.june.springsecurity.account.UserAccount;
import me.june.springsecurity.book.BookRepository;
import me.june.springsecurity.common.AuthUser;
import me.june.springsecurity.common.SecurityLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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
    private final BookRepository bookRepository;

    @GetMapping("/")
    public String index (Model model,
                         @AuthUser Account account) {
        if (account == null) {
            model.addAttribute("message", "Hello Spring Security");
        } else {
            model.addAttribute("message", "Hello Index" + account.getUsername());
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
        // 현재 user가 가지고있는 책의 목록
        model.addAttribute("books", bookRepository.findCurrentUserBooks());
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

    // async한 service 호출
    @GetMapping("/async-service")
    @ResponseBody
    public String asyncService () {
        SecurityLogger.log("MVC, before Async");
        sampleService.asyncService(); // asyncService내의 메세지가 먼저 호출될지 순서가 보장되지 않는다.
        SecurityLogger.log("MVC, After Async");
        return "Async Service";
    }
}
