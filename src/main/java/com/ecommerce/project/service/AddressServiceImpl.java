package com.ecommerce.project.service;


import com.ecommerce.project.Payload.AddressDTO;
import com.ecommerce.project.Repositories.AddressRepository;
import com.ecommerce.project.Repositories.UserRepository;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddressServiceImpl implements  AddressService{

    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public AddressDTO addNewAddress(AddressDTO addressDTO, User user) {

        Address address=modelMapper.map(addressDTO, Address.class);
        List<Address> addressList=user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);
        address.setUser(user);

        Address savedAddress=addressRepository.save(address);

        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
       List<Address> addresses=addressRepository.findAll();
       List<AddressDTO> addressDTOS=addresses.stream()
               .map((address)->modelMapper.map(address, AddressDTO.class))
               .toList();

       return addressDTOS;
    }

    @Override
    public List<AddressDTO> getAddressByUserId(User user) {
       // List<Address> addresses=addressRepository.findByUserId(user.getUserId());
        List<Address> addressList=user.getAddresses();
       return  addressList.stream()
                .map((address)->modelMapper.map(address,AddressDTO.class))
                .toList();

    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
       Address address= addressRepository.findById(addressId)
               .orElseThrow(()->new ResourceNotFoundException("Address","AddressId",addressId));

       return modelMapper.map(address,AddressDTO.class);
    }


}
