package me.june.springsecurity.filters;

import org.springframework.util.StopWatch;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 07/10/2019
 * Time: 10:37 오후
 **/

public class LoggingFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        /* HttpServletRequest의 RequestURI 를 Task이름으로 지정하여 로깅을 진행한다.*/
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(request.getRequestURI());

        filterChain.doFilter(servletRequest, servletResponse);
        stopWatch.stop();

        logger.info(stopWatch.prettyPrint());
    }
}
