package me.june.springsecurity.form;

import lombok.RequiredArgsConstructor;
import me.june.springsecurity.SampleService;
import me.june.springsecurity.account.AccountContext;
import me.june.springsecurity.account.AccountRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

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

    @GetMapping("/admin")
    public String admin (Model model, Principal principal) {
        model.addAttribute("message", "Hello Admin" + principal.getName());
        return "admin";
    }
}
