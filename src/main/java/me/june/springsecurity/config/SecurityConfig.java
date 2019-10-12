package me.june.springsecurity.config;

import me.june.springsecurity.account.AccountService;
import me.june.springsecurity.filters.LoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @Autowired
    AccountService accountService;


    /*


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

        // ExceptionTranslationFilter -> SecurityInterceptor 밀접한 관계가 있음
        // ExceptionTranslationFilter 가 SecurityInterceptor 이전에 존재해야함
        // SecurityInterceptor 를 감싸고 있다.
        // ExceptionTranslationFilter 가 try catch블록으로 감싸고 SecurityInterceptor 를 실행한다.
        // SecurityInterceptor가 실제 인가 처리를한다.
        // (AccessDecisionManager) -> AffirmativeBased (기본구현체)
        // 두가지 예외가 발생할 수 있음
        // 1. AuthenticationException 인증 관련
        // 2. AccessDeniedException 인가가 안됨
        // 두 예외가 따라 각기 다른 처리를한다.
        // 인증이 안된경우 => AuthenticationEntryPoint를 사용하여 처리를함.
        // -> 해당 유저를 로그인 하게끔 유도한다. 커스텀할 필요가 없다고 판단
        // 인가가 안된경우 => AccessDeniedHandler를 사용하여 처리를함
        // -> 기본 처리는 403 에러페이지를 보여줌
        // 인가가 안된경우 보여줄 페이지
        http.exceptionHandling()
                .accessDeniedPage("/access-denied")
                .accessDeniedHandler(new AccessDeniedHandler() { // 별도의 클래스로 분리하는것이 좋다.
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                        String username = principal.getUsername();
                        System.out.println(username + "is denied to access" + request.getRequestURI()); // 요청실패 로깅
                        response.sendRedirect("/access-denied");
                    }
                });


        http.rememberMe()
            .userDetailsService(accountService);


        // addFilter 메서드들을 통해 필터를 추가할수 있다.
        // 특정 필터의 앞에 추가하거나, 특정 필터의 뒤에 추가하는 등 메서드를 제공한다.
        // 우리가 구현한 로깅필터를 가장 맨앞에 존재하던 Filter의 앞쪽에 추가하는 코드
        // 즉 우리가 추가한 필터가 Spring Security의 1순위 필터가 된다.
        http.addFilterBefore(new LoggingFilter(), WebAsyncManagerIntegrationFilter.class);

    }

    /*
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin").password("{noop}1234").roles("ADMIN").and()
                .withUser("user").password("{noop}user1234").roles("USER");
    }
     */

    /* AuthenticaitonManager를 Bean으로 등록하여 노출시킴 */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
