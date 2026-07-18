package com.ecommerce.project.service;

import com.ecommerce.project.Payload.AddressDTO;
import com.ecommerce.project.model.User;

import java.util.List;

public interface AddressService {


    AddressDTO addNewAddress(AddressDTO addressDTO, User user);


    List<AddressDTO> getAllAddresses();

    List<AddressDTO> getAddressByUserId(User user);

    AddressDTO getAddressById(Long addressId);
}
