package me.june.springsecurity;

import me.june.springsecurity.account.Account;
import me.june.springsecurity.account.AccountContext;
import me.june.springsecurity.common.SecurityLogger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.security.RolesAllowed;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 10/09/2019
 * Time: 10:18 오후
 **/
@Service
public class SampleService {

    @Secured("ROLE_USER")
    @RolesAllowed("ROLE_USER")
    @PreAuthorize("hasRole('USER')")
//    @PostAuthorize("hasRole('USER')")
    @PostAuthorize("returnObject.username == authntication.principal.nickName")
    public void dashboard () {
        // SecurityContextHolder를 통해 SecurityContext에 접근이 가능하다.
        SecurityContext context = SecurityContextHolder.getContext();

        // SecurityContext 에서 Authentication 객체를 얻는다.
        Authentication authentication = context.getAuthentication();

        // Authentication 객채에서 사용자 인증정보인 Principal (UserDetailsService에서 리턴해준 타입 -> UserDetails Type)
        Object principal = authentication.getPrincipal();

        // Authorities 사용자의 권한을 의미 하는 객체 이다. User 객체를 만들때 roles 에 추가한 권한 객체 들이다.
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        //        // Holder가 Authentication을 담고있음
        //        // ThreadLocal 을 사용한다.
        //        // 애플리케이션 어디서든 접근이 가능하다.
        //        // 처리하는 Thread가 달라진다면, 제대로된 Authentication 정보를 받아오지 못한다.


        /*
            AccountContext에 저장된 account객체를 꺼내온다.

            Account account = AccountContext.getAccount();
            System.out.println("username = " + account.getUsername());
        */

    }

    // 특정 빈안에 메서드를 호출할때 별도의 스레드를 생성하여 비동기적인 처리를 한다.
    // 현재상태로 @Async 애노테이션만 사용했다면 아무런일도 벌어지지않고, 순서대로 호출이됨.
    // @Async만 사용한다고 되는것이 아니다.
    @Async
    public void asyncService() {
        SecurityLogger.log("Async Service");
        System.out.println("Async Service is Called");
        // 현재 상태로는 SecurityContext가 공유되지 않는다.
    }
}
