package me.june.springsecurity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 12/10/2019
 * Time: 10:43 오후
 **/
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true)
// @secured,
// preAuthorized, postAuthorized
// rolesAllowed 사용시 활성
// Spring AOP를 사용하여 특정 빈의 method에 보안 적용이 가능하다.
public class MethodSecurity extends GlobalMethodSecurityConfiguration {

    /* MethodSecurity용 Hierachy 설정을 해주어야함 */
    @Override
    protected AccessDecisionManager accessDecisionManager() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        AffirmativeBased accessDecisionManager = (AffirmativeBased) super.accessDecisionManager();
        accessDecisionManager.getDecisionVoters().add(new RoleHierarchyVoter(roleHierarchy));
        return accessDecisionManager;
    }
}
