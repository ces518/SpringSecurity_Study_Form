package me.june.springsecurity;

import lombok.RequiredArgsConstructor;
import me.june.springsecurity.account.Account;
import me.june.springsecurity.account.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 26/09/2019
 * Time: 9:20 오후
 **/
@Controller
@RequestMapping("/signup")
@RequiredArgsConstructor
public class SignUpController {

    private final AccountService accountService;

    @GetMapping
    public String signUpForm (Model model) {
        model.addAttribute("account", new Account());
        return "signup";
    }

    @PostMapping
    public String processSignUp (Account account) {
        account.setRole("USER");
        accountService.createAccount(account);
        return "redirect:/";
    }
}
