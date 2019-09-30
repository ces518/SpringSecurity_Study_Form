package me.june.springsecurity.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 07/09/2019
 * Time: 10:29 오후
 **/
@Configuration
@EnableWebSecurity
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /*
    @Autowired
    AccountService accountService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService);
    }
     */

//    @Bean
//    public PasswordEncoder passwordEncoder () {
//        return NoOpPasswordEncoder.getInstance();
//    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        // 매번 static resource들을 적어주어야하나 ?
        // spring boot에서 제공하는 설정을 사용
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public PasswordEncoder passwordEncoder () {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /* 기본 accessDecisionManager를 사용하지 않고 커스터마이징 한다 */
    public AccessDecisionManager accessDecisionManager () {
        DefaultWebSecurityExpressionHandler securityExpressionHandler = expressionHandler();

        WebExpressionVoter expressionVoter = new WebExpressionVoter();
        // WebExpressionVoter가 사용하는 expressionHandler를 커스터마이징 해주어야한다.
        expressionVoter.setExpressionHandler(securityExpressionHandler);
        List<AccessDecisionVoter<? extends Object>> voters = Arrays.asList(expressionVoter);
        return new AffirmativeBased(voters);
    }

    public DefaultWebSecurityExpressionHandler expressionHandler () {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        // ROLE_ADMIN은 ROLE_USER의 상위권한이라는것을 정의해준다.
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

        // WebExpressionVoter 는 expressionHandler를 사용한다.
        // Voter가 사용하는 expressionHandler에게 Hierarchy를 설정
        DefaultWebSecurityExpressionHandler securityExpressionHandler = new DefaultWebSecurityExpressionHandler();
        // Hierarchy 커스터마이징
        securityExpressionHandler.setRoleHierarchy(roleHierarchy);
        return securityExpressionHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/", "/info", "/account/**", "async-handler", "/signup").permitAll()
                .mvcMatchers("/admin").hasRole("ADMIN")
                .mvcMatchers("/user").hasRole("USER")
                .anyRequest().authenticated()
//                .accessDecisionManager(accessDecisionManager())
                .expressionHandler(expressionHandler())
                .and()
            .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
                .logoutUrl("/logout")// 기본은 /logout
                .logoutSuccessUrl("/")
                .invalidateHttpSession(false) // 기본값은 true
                .deleteCookies("COOKIE_NAME") // 쿠키기반의 인증을 사용하고있다면 해당 쿠키를 제거한다.
                .and()
            .httpBasic();

        // 현재 스레드에서 하위 스레드가 생성되는경우 해당 스레드까지 공유된다.
        // SecurityContextHolder 는 다양한 Strategy가 존재하는데 기본 전략은 ThreadLocal 임.
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    /*
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin").password("{noop}1234").roles("ADMIN").and()
                .withUser("user").password("{noop}user1234").roles("USER");
    }
     */
}
