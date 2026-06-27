package com.ecommerce.project.service;

import com.ecommerce.project.Payload.CategoryDto;
import com.ecommerce.project.Payload.ProductDTO;
import com.ecommerce.project.Payload.ProductResponse;
import com.ecommerce.project.Repositories.CategoryRepository;
import com.ecommerce.project.Repositories.ProductRepository;
import com.ecommerce.project.exception.ApiException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {

        Category category=categoryRepository.findById(categoryId)
                                       .orElseThrow(()->new ResourceNotFoundException("category","categoryId",categoryId));

        Product product=modelMapper.map(productDTO,Product.class);
        product.setCategory(category);
        double specialPrice=product.getPrice()-
                ((product.getDiscount()*0.01)*product.getPrice());
        product.setSpecialPrice(specialPrice);
        log.info("Product added to Category and In ProductRepository {}");
        return modelMapper.map(productRepository.save(product),ProductDTO.class);

    }

    @Override
    public ProductResponse getAllProducts() {
       List<Product> products= productRepository.findAll();
       if(products.isEmpty()){
           throw new ApiException("No product created yet!!!");
       }
         List<ProductDTO> allProduct=products.stream()
                 .map((p)->modelMapper.map(p,ProductDTO.class))
                 .toList();
         ProductResponse productResponse=new ProductResponse();
         productResponse.setContent(allProduct);
         return productResponse;
    }

    @Override
    public ProductResponse searchProductByCategory(Long categoryId) {
        Category category=categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("category","categoryId",categoryId));
        String categoryName=category.getCategoryName();
        List<Product> productsByCategory=productRepository.findByCategoryOrderByPriceAsc(category);

        List<ProductDTO> allProduct=productsByCategory.stream()
                .map((p)->modelMapper.map(p,ProductDTO.class))
                .toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(allProduct);
        return productResponse;

    }

    @Override
    public ProductResponse searchProductByCategory(String keyword) {
        List<Product> products=productRepository.findByProductNameLikeIgnoreCase("%"+ keyword+ "%");

        List<ProductDTO> allProduct=products.stream()
                .map((p)->modelMapper.map(p,ProductDTO.class))
                .toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(allProduct);
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId,ProductDTO productDTO) {
       Product product=productRepository.findById(productId)
               .orElseThrow(()->new ResourceNotFoundException("product","productId",productId));

       Product product1= modelMapper.map(productDTO,Product.class);
       product1.setProductId(productId);
       product=productRepository.save(product1);
       return modelMapper.map(product,ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product=productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("product","productId",productId));

        productRepository.deleteById(productId);
        return modelMapper.map(product,ProductDTO.class);
    }
}
