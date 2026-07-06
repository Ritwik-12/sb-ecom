package com.ecommerce.project.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println("intercepting req for catagory controller");
        logger.debug("AuthTokenFilter called for URI {}:",request.getRequestURI());

        try{

            String jwt=parseJwt(request);
            System.out.println("The token is "+jwt);
            if(jwt!=null && jwtUtils.validateJwtToken(jwt)){
                String username=jwtUtils.getUserNameFromJwtToken(jwt);
               UserDetails userDetails= userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(
                        userDetails,null,userDetails.getAuthorities()
                );
                System.out.println(authentication.getPrincipal());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Roles from jwt: {}",userDetails.getAuthorities());
            }
        }catch(Exception e){
            logger.error("Cannot set user authenticatoin: {}",e);
        }
        System.out.println("moving to the next filter in the filter chain!");
        filterChain.doFilter(request,response);
    }

    private String parseJwt(HttpServletRequest request){

        System.out.println("inside the parse jwt");
        System.out.println(request.getCookies());
        String jwt=jwtUtils.getJwtFromCookies(request);
        System.out.println(jwt);
        System.out.println(request.getCookies());
        logger.debug("AuthTokenFilter.java: {}",jwt);
        return jwt;
    }
}
