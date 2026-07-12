package com.ecommerce.project.controller;


import com.ecommerce.project.Payload.CartDTO;
import com.ecommerce.project.Repositories.CartRepository;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.service.CartService;
import com.ecommerce.project.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private CartService cartService;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(
            @PathVariable Long productId,
            @PathVariable Integer quantity
    ){
        CartDTO cartDTO= cartService.addProductToCart(productId,quantity);
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.CREATED);
    }


    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getCarts(){
        List<CartDTO> cartDTOS=cartService.getAllCarts();
        return new ResponseEntity<List<CartDTO>>(cartDTOS,HttpStatus.FOUND);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartById(){

        String emailId= authUtil.loggedInEmail();
        Cart cart=cartRepository.findCartByEmail(emailId);
        Long cartId=cart.getId();
       CartDTO cartDTO= cartService.getCart(emailId,cartId);
       return new ResponseEntity<CartDTO>(cartDTO,HttpStatus.FOUND);
    }
    @PutMapping("/cart/product/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateProductQuantity(
            @PathVariable Long productId,
            @PathVariable String operation
    ){
        System.out.println("inside the patch controller");
       CartDTO cartDTO= cartService.updateProductQuantityInCart(productId,
                operation.equalsIgnoreCase("delete")?-1:1);

        return new ResponseEntity<CartDTO>(cartDTO,HttpStatus.OK);

    }

    @DeleteMapping("/cart/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(
            @PathVariable Long cartId,
            @PathVariable Long productId
    ){
       String status= cartService.deleteProductFromCart(cartId,productId);
       return new ResponseEntity<>(status,HttpStatus.OK);
    }
}
