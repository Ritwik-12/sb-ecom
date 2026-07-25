package com.ecommerce.project.service;


import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class DataServiceImplDev implements DataService{

    @Override
    public String getData() {
        return "dev data";
    }
}
