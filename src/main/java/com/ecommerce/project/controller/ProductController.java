package com.ecommerce.project.controller;

import com.ecommerce.project.Payload.ProductDTO;
import com.ecommerce.project.Payload.ProductResponse;
import com.ecommerce.project.Repositories.ProductRepository;
import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {



    private final ProductService productService;


    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name="pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required=false) Integer pageNumber,
            @RequestParam(name="pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(name="sortBy",defaultValue = AppConstants.SORT_PRODUCTS_BY,required = false) String sortBy,
            @RequestParam(name="sortOrder",defaultValue = AppConstants.SORT_DIRECTION,required = false) String sortOrder
    )
    {
       ProductResponse allProducts= productService.getAllProducts(pageNumber,pageSize,sortBy,sortOrder);
       return new ResponseEntity<>(allProducts,HttpStatus.OK);
    }


    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO,
                                                 @PathVariable Long categoryId){


        ProductDTO createdProduct=productService.addProduct(productDTO,categoryId);

        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);

    }

    @GetMapping("/public/category/{categoryId}/products")
    public ResponseEntity<ProductResponse> searchProductByCategory(@PathVariable Long categoryId,
           @RequestParam(name="pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false)  Integer pageNumber,
           @RequestParam(name="pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
           @RequestParam(name="sortBy",defaultValue = AppConstants.SORT_PRODUCTS_BY,required = false) String sortBy,
           @RequestParam(name="sortOrder",defaultValue = AppConstants.SORT_DIRECTION,required = false)  String sortOrder)
    {

        ProductResponse productByCategory=productService.searchProductByCategory(categoryId,pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productByCategory,HttpStatus.OK);

    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> searchProductByKeyword(@PathVariable String keyword,
        @RequestParam(name="pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required=false) Integer pageNumber,
        @RequestParam(name="pageSize",defaultValue = AppConstants.PAGE_SIZE,required=false) Integer pageSize,
        @RequestParam(name="sortBy",defaultValue = AppConstants.SORT_PRODUCTS_BY,required=false) String sortBy,
        @RequestParam(name="sortOrder",defaultValue = AppConstants.SORT_DIRECTION,required = false)   String sortOrder

    ){
       ProductResponse productResponse= productService.searchProductByKeyword(keyword,pageNumber,pageSize,sortBy,sortOrder);
       return new ResponseEntity<>(productResponse,HttpStatus.FOUND);
    }

    @PutMapping("/admin/product/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @PathVariable Long productId, @RequestBody ProductDTO productDTO){
      ProductDTO updatedProduct= productService.updateProduct(productId,productDTO);
      return new ResponseEntity<>(updatedProduct,HttpStatus.OK);
    }

    @DeleteMapping("/admin/product/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){
        ProductDTO deletedProduct =productService.deleteProduct(productId);
        return new ResponseEntity<>(deletedProduct,HttpStatus.OK);
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId,
                                                         @RequestParam("image")MultipartFile image)
                                                            throws IOException

    {
        ProductDTO updatedProduct=productService.updateProductImage(productId,image);
        return new ResponseEntity<>(updatedProduct,HttpStatus.OK);
    }
}
