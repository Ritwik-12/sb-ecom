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

    @Override
    public CartDTO getCart(String email, Long cartId) {
        Cart cart =cartRepository.findCartByEmailAndCartId(email,cartId);
        if(cart==null){
            throw new ResourceNotFoundException("Cart ","CartId",cartId);
        }
        CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);
        cart.getItems().forEach(
                c->c.getProduct().setQuantity(c.getQuantity())
        );
        List<ProductDTO> products=cart.getItems().stream()
                .map(p->modelMapper.map(p.getProduct(), ProductDTO.class))
                .toList();
        cartDTO.setProducts(products);


        return cartDTO;

    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {

            String  email= authUtil.loggedInEmail();
            Cart userCart=cartRepository.findCartByEmail(email);
            Long cartId= userCart.getId();
        System.out.println("cart id "+cartId);
            Cart cart=cartRepository.findById(cartId)
                    .orElseThrow(()->new ResourceNotFoundException("Cart","CartId",cartId));


        Product product=productRepository.findById(productId)
                .orElseThrow(()->new ApiException("No product found with the id "+productId));


        if(product.getQuantity()==0){
            throw new ApiException(product.getProductName()+" is not available");
        }

        if(product.getQuantity()<quantity){
            throw new ApiException("Please, make an order of the "+product.getProductName()+
                    " less than or equals to the quantity "+product.getQuantity());
        }

        System.out.println("product id "+product.getProductId());
                CartItem item=cartItemRepository.findCartItemByProductIdAndCartId(productId,cartId);

            System.out.println("item is "+item);
                if(item==null){
                    throw new ApiException("Product "+product.getProductName()+" is not available in the cart!!!");
                }

                int newQuantity=item.getQuantity()+quantity;
                 System.out.println("new quantity"+newQuantity);
                if(newQuantity<0){
                    throw new ApiException("The resulting quantity can not be negative!!");
                }

                if(newQuantity==0){
                  String msg=  deleteProductFromCart(cartId,productId);
                    System.out.println("hi "+msg);
                }else {

                    item.setProductPrice(product.getSpecialPrice());
                    item.setQuantity(item.getQuantity() + quantity); //4-1 3
                    System.out.println("current quantity" + item.getQuantity());
                    //product.setQuantity(product.getQuantity()-quantity); //--
                    item.setDiscount(product.getDiscount());
                    cart.setTotalPrice(cart.getTotalPrice() + (item.getProductPrice() * quantity));
                    // productRepository.save(product);
                    cartRepository.save(cart);
                }
                    CartItem updatedCartItem = cartItemRepository.save(item);

            if(updatedCartItem.getQuantity()==0){
                cartItemRepository.deleteById(updatedCartItem.getId());
            }

            CartDTO cartDTO= modelMapper.map(cart,CartDTO.class);
                  List<CartItem> cartItems=cart.getItems();
            Stream<ProductDTO> productDTOStream=cartItems.stream()
                    .map(i->{
                        ProductDTO prd=modelMapper.map(i.getProduct(), ProductDTO.class);
                        System.out.println(i.getQuantity());
                        prd.setQuantity(i.getQuantity());
                        return prd;
                    });
            cartDTO.setProducts(productDTOStream.toList());
            return cartDTO;
    }


    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId,Long productId) {
        Cart cart=cartRepository.findById(cartId)
                .orElseThrow(()->new ResourceNotFoundException("Cart","CartID",cartId));

        CartItem cartItem=cartItemRepository.findCartItemByProductIdAndCartId(productId,cartId);
        if(cartItem==null){
            throw new ResourceNotFoundException("product", "ProductId",productId);
        }
        cart.setTotalPrice(cart.getTotalPrice()-(cartItem.getProductPrice()*cartItem.getQuantity()));
        cartItemRepository.deleteCartItemByProductIdAndCartId(productId,cartId);
        return "Product"+ cartItem.getProduct().getProductName()+" removed from the cart|||";
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
