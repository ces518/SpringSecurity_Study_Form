package me.june.springsecurity.account;

import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 09/09/2019
 * Time: 9:44 오후
 **/
@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(username = "june", roles = "USER")
public @interface WithUser {

}
