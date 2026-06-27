package com.ecommerce.project.service;

import com.ecommerce.project.Payload.ProductDTO;
import com.ecommerce.project.Payload.ProductResponse;

public interface ProductService {

    ProductDTO addProduct(ProductDTO productDTO,Long categoryId);

    ProductResponse getAllProducts();

    ProductResponse searchProductByCategory(Long categoryId);

    ProductResponse searchProductByCategory(String keyword);

    ProductDTO updateProduct(Long productId,ProductDTO productDTO);

    ProductDTO deleteProduct(Long productId);
}
