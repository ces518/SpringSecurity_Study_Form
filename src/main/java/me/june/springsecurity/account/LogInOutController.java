package me.june.springsecurity.account;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 30/09/2019
 * Time: 8:42 오후
 **/
@Controller
public class LogInOutController {

    @GetMapping("/login")
    public String login () {
        return "login";
    }

    @GetMapping("/logout")
    public String logout () {
        return "logout";
    }
}
