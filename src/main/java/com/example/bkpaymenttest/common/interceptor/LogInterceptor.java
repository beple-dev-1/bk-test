package com.example.bkpaymenttest.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    private static final String ATTR_START_TIME = "reqStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(ATTR_START_TIME, System.currentTimeMillis());
        log.info("[REQ] {} {}", request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute(ATTR_START_TIME);
        long elapsed = System.currentTimeMillis() - startTime;

        if (ex != null) {
            log.error("[RES] {} {} | status={} | {}ms | error={}",
                    request.getMethod(), request.getRequestURI(),
                    response.getStatus(), elapsed, ex.getMessage());
        } else {
            log.info("[RES] {} {} | status={} | {}ms",
                    request.getMethod(), request.getRequestURI(),
                    response.getStatus(), elapsed);
        }
    }
}
