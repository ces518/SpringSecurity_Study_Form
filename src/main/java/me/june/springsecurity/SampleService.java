package me.june.springsecurity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 10/09/2019
 * Time: 10:18 오후
 **/
@Service
public class SampleService {

    public void dashboard () {
        // SecurityContextHolder를 통해 SecurityContext에 접근이 가능하다.
        SecurityContext context = SecurityContextHolder.getContext();

        // SecurityContext 에서 Authentication 객체를 얻는다.
        Authentication authentication = context.getAuthentication();

        // Authentication 객채에서 사용자 인증정보인 Principal (UserDetailsService에서 리턴해준 타입 -> UserDetails Type)
        Object principal = authentication.getPrincipal();

        // Authorities 사용자의 권한을 의미 하는 객체 이다. User 객체를 만들때 roles 에 추가한 권한 객체 들이다.
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Holder가 Authentication을 담고있음
        // ThreadLocal 을 사용한다.
        // 애플리케이션 어디서든 접근이 가능하다.
        // 처리하는 Thread가 달라진다면, 제대로된 Authentication 정보를 받아오지 못한다.
    }
}
