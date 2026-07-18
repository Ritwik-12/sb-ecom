package com.ecommerce.project.controller;


import com.ecommerce.project.Payload.AddressDTO;
import com.ecommerce.project.model.User;
import com.ecommerce.project.service.AddressService;
import com.ecommerce.project.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final AuthUtil authUtil;

    @PostMapping("/address")
    public ResponseEntity<AddressDTO> addNewAddress(@RequestBody AddressDTO addressDTO){

          User user =authUtil.loggedInUser();

         AddressDTO address=  addressService.addNewAddress(addressDTO,user);
         return new ResponseEntity<AddressDTO>(address, HttpStatus.CREATED);
    }

    @GetMapping("/admin/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses(){

        List<AddressDTO> addressDTOS=addressService.getAllAddresses();
        return new ResponseEntity<>(addressDTOS,HttpStatus.OK);
    }

    @GetMapping("/address/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId){
      AddressDTO addressDTO=  addressService.getAddressById(addressId);
      return new ResponseEntity<>(addressDTO,HttpStatus.OK);
    }

    @GetMapping("/useraddress")
    public ResponseEntity<List<AddressDTO>> getAddressByUserId(){
        User user =authUtil.loggedInUser();
        List<AddressDTO> addresses=addressService.getAddressByUserId(user);
        return new ResponseEntity<>(addresses,HttpStatus.OK);
    }
}
