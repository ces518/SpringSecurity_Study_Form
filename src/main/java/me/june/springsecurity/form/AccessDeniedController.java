package me.june.springsecurity.form;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 05/10/2019
 * Time: 10:16 오후
 **/
@Controller
public class AccessDeniedController {

    @GetMapping("/access-denied")
    public String accessDenied (Principal principal, Model model) {
        model.addAttribute("name", principal.getName());
        return "accessDenied";
    }
}
