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
import org.springframework.security.config.http.SessionCreationPolicy;
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

        http.anonymous()
                .principal("anonymousUser")
                .authorities("ROLE_ANONYMOUS");

        http.sessionManagement()
                .sessionFixation()
                .migrateSession(); // 인증 성공시 세션 전략

        http.sessionManagement()
                .invalidSessionUrl("/login"); // 유효하지않은 세션 접근시 URL 설정

        http.sessionManagement() // 최대 세션을 1로 설정
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false); // 중복로그인시 이전 로그인했던 세션이 만료된다. (기본 전략), true 시 이전 세션을 유지한다.

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED); // 세션 생성 전략 기본값: 필요하면 생성한다.
        // NEVER : 시큐리티에서는 생성하지않는다. 하지만 기존에 세션이 있다면 사용한다. 대부분의 경우에 이미 세션이 존재하기 때문에 사용하게됨
        // STATELESS : 세션이 존재하여도 쓰지않는다. (세션을 정말 쓰고싶지 않은경우) SecurityContext를 캐싱해서 사용해야하는데 캐싱을 하지 않는다., RESTAPI에서 사용해야하는 전략, 폼기반 인증에선 어울리지 않는다.
        // RequestCacheAwareFilter 도 Session을 사용하기 때문에 이 필터의 기능도 동작하지 않는다.
        // AWAYS : 항상 생성한다.
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
