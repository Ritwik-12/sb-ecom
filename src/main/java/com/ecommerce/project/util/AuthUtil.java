package com.ecommerce.project.util;


import com.ecommerce.project.Repositories.UserRepository;
import com.ecommerce.project.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final UserRepository userRepository;

    public String loggedInEmail(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        User user=userRepository.findByUserName(authentication.getName())
                .orElseThrow(()->new UsernameNotFoundException("User not found with name: "+authentication.getName()));

        return user.getEmail();
    }

    public Long loggedInUserId(){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        User user=userRepository.findByUserName(authentication.getName())
                .orElseThrow(()->new UsernameNotFoundException("User not found with name :"+authentication.getName()));
        return user.getUserId();
    }

    public User loggedInUser(){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        User user=userRepository.findByUserName(authentication.getName())
                .orElseThrow(()->new UsernameNotFoundException("User not found with name :"+authentication.getName()));

        return user;
    }
}
