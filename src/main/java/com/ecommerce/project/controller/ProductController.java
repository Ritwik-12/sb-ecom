package com.ecommerce.project.controller;

import com.ecommerce.project.Payload.ProductDTO;
import com.ecommerce.project.Payload.ProductResponse;
import com.ecommerce.project.Repositories.ProductRepository;
import com.ecommerce.project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {



    private final ProductService productService;
    private final ProductRepository productRepository;


    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(){
       ProductResponse allProducts= productService.getAllProducts();
       return new ResponseEntity<>(allProducts,HttpStatus.OK);
    }


    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody ProductDTO productDTO,
                                                 @PathVariable Long categoryId){


        ProductDTO createdProduct=productService.addProduct(productDTO,categoryId);

        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);

    }
    @GetMapping("/public/category/{categoryId}/products")
    public ResponseEntity<ProductResponse> searchProductByCategory(@PathVariable Long categoryId){

        ProductResponse productByCategory=productService.searchProductByCategory(categoryId);
        return new ResponseEntity<>(productByCategory,HttpStatus.OK);


    }


}
