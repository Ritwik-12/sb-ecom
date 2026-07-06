package com.ecommerce.project.security.jwt;


import com.ecommerce.project.security.service.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${spring.app.jwtExpirationMs}")
    private Long jwtExpirationInMs;

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtCookieName}")
    private String jwtCookie;


    //Getting JWT form header
    private static final Logger logger= LoggerFactory.getLogger(JwtUtils.class);
//    public String getJwtFromHeader(HttpServletRequest request){
//
//        String bearerToken=request.getHeader("Authorization");
//        logger.debug("Authorization Header: {} ",bearerToken);
//        if(bearerToken!=null && bearerToken.startsWith("Bearer ")){
//            return bearerToken.split("Bearer ")[1];
//        }
//        return null;
//    }

    public String getJwtFromCookies(HttpServletRequest request){
        Cookie cookie= WebUtils.getCookie(request,jwtCookie);
        if(cookie!=null){
            return cookie.getValue();
        }else{
            return null;
        }

    }


    public ResponseCookie generateJwtCookie(UserDetailsImpl userPricipal){
            String jwt=generateTokenFromUsreName(userPricipal.getUsername());

            ResponseCookie cookie= ResponseCookie.from(jwtCookie,jwt)
                    .path("/api")
                    .maxAge(24*60*60)
                    .httpOnly(false)
                    .build();

            return cookie;
    }

    public ResponseCookie getCleanJwtCookie(){
       ResponseCookie cookie= ResponseCookie.from(jwtCookie,null)
               .path("/api")
               .build();

        return cookie;
    }

    //Generating token form user name
    public String generateTokenFromUsreName(String username){
           // String username=userDetails.getUsername();
            return Jwts.builder()
                    .subject(username)
                    .issuedAt(new Date())
                    .expiration(new Date((new Date().getTime()+jwtExpirationInMs)))
                    .signWith(key())
                    .compact();
    }



    //Getting username from jwt token
    public String getUserNameFromJwtToken(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload().getSubject();
    }
    //Generate sign key

    public Key key(){
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }
    //validate jwt token

    public boolean validateJwtToken(String authToken){

        try{

            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        }catch(MalformedJwtException e){
            logger.error("Invalid JWT token: {}", e.getMessage());
        }
        catch(ExpiredJwtException e){
            logger.error("JWT token is expired: {}",e.getMessage());
        }
        catch (UnsupportedJwtException e){
            logger.error("JWT token is unsupported: {}",e.getMessage());
        }
        catch(IllegalArgumentException e){
            logger.error("Jwt Claims string is empty: {}",e.getMessage());
        }
        return false;
    }

}
