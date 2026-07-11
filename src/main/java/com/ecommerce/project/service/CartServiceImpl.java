package com.ecommerce.project.service;

import com.ecommerce.project.Payload.CartDTO;
import com.ecommerce.project.Payload.ProductDTO;
import com.ecommerce.project.Repositories.CartItemRepository;
import com.ecommerce.project.Repositories.CartRepository;
import com.ecommerce.project.Repositories.ProductRepository;
import com.ecommerce.project.exception.ApiException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Autowired
    private  AuthUtil authUtil;

    @Autowired
    private  ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private  ModelMapper modelMapper;




    @Override
    @Transactional
    public CartDTO addProductToCart(Long productId, Integer quantity) {
         //Find existing cart or create one
            Cart cart=createCart();
            Product product=productRepository.findById(productId)
                    .orElseThrow(()->new ResourceNotFoundException("Product","ProductId",productId));
            CartItem cartItem=cartItemRepository.findCartItemByProductIdAndCartId(
                    cart.getId(),
                    productId
            );
            if(cartItem!=null){
                throw new ApiException("Product "+product.getProductName()+ " already exists in the cart");
            }
            if(product.getQuantity()==0){
                throw new ApiException(product.getProductName()+" is not available");
            }

            if(product.getQuantity()<quantity){
                throw new ApiException("Please, make an order of the "+product.getProductName()+
                        " less than or equals to the quantity "+product.getQuantity());
            }

            CartItem newCartItem=new CartItem();
            newCartItem.setProduct(product);
            newCartItem.setCart(cart);
            newCartItem.setQuantity(quantity);
            newCartItem.setDiscount(product.getDiscount());
            newCartItem.setProductPrice(product.getSpecialPrice());
              cart.getItems().add(newCartItem);
            cartItemRepository.save(newCartItem);
            product.setQuantity(product.getQuantity()-quantity);
            cart.setTotalPrice(cart.getTotalPrice()+(product.getSpecialPrice()*quantity));

           cartRepository.save(cart);
        System.out.println("cart is "+cart);
           CartDTO cartDTO=modelMapper.map(cart, CartDTO.class);

           List<CartItem> cartItems=cart.getItems();
        System.out.println(cartItems);
           List<ProductDTO> productStream=cartItems.stream().map(item->{
                ProductDTO map=modelMapper.map(item.getProduct(), ProductDTO.class);
                map.setQuantity(item.getQuantity());
                return map;
           }).toList();
        System.out.println("products in the cart:"+productStream);
           cartDTO.setProducts(productStream);
           return cartDTO;

    }

    @Override
    public List<CartDTO> getAllCarts() {
       List<Cart> carts= cartRepository.findAll();
       if(carts.isEmpty()) throw new ApiException("No cart's found!!");
       List<CartDTO> cartDTOS= carts.stream()
               .map((c)-> {
                   CartDTO cartDTO = modelMapper.map(c, CartDTO.class);
                   List<ProductDTO> products=c.getItems().stream()
                           .map((item)->modelMapper.map(item, ProductDTO.class))
                           .toList();
                     cartDTO.setProducts(products);
                     return cartDTO;

               }).toList();

       return cartDTOS;

    }

    private Cart createCart(){
        Cart userCart=cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart!=null){
            return userCart;
        }

        Cart cart=new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart=cartRepository.save(cart);
        return newCart;
    }
}
