package com.ecommerce.project.infrastructure;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@Order(1)
public class RequestLoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        long start=System.currentTimeMillis();

        try{

                filterChain.doFilter(request,response);
        }finally{
            long duration=System.currentTimeMillis()-start;
            String ip=request.getRemoteAddr();
            if("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)){
                ip="127.0.0.1";
            }
            log.info("IPV4 method,path,duration {} , {},{},{}",
                    ip,
                    request.getRequestURI(),
                    request.getMethod(),
                    duration
                    );
        }
    }
}
