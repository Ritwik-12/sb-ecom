package com.ecommerce.project.service;

import com.ecommerce.project.Payload.AddressDTO;
import com.ecommerce.project.model.User;

public interface AddressService {


    AddressDTO addNewAddress(AddressDTO addressDTO, User user);


}
