package me.june.springsecurity.common;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 23/09/2019
 * Time: 9:41 오후
 **/
public class SecurityLogger {

    public static void log (String message) {
        System.out.println(message);
        Thread thread = new Thread();
        System.out.println(thread.getName());
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(principal);
    }
}
