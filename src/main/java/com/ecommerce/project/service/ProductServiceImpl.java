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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {


        //CHECK IF PRODUCT IS ALRREAD PRESENT OR NOT

        Category category=categoryRepository.findById(categoryId)
                                       .orElseThrow(()->new ResourceNotFoundException("category","categoryId",categoryId));

        boolean isProductNotPresent=true;
        List<Product> products=category.getProducts();
        for (Product value : products) {
            if (value.getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }
        if(isProductNotPresent) {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setCategory(category);
            double specialPrice = product.getPrice() -
                    ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            log.info("Product added to Category and In ProductRepository {}");
            return modelMapper.map(productRepository.save(product), ProductDTO.class);
        }else{
            throw new ApiException("Product already exists!!!");
        }

    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber,Integer pageSize,String sortBy,String sortOrder) {


        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);

        Page<Product> productPage=productRepository.findAll(pageDetails);

        List<Product> products=productPage.getContent();

       if(products.isEmpty()){
           throw new ApiException("No product created yet!!!");
       }
         List<ProductDTO> allProduct=products.stream()
                 .map((p)->modelMapper.map(p,ProductDTO.class))
                 .toList();
         ProductResponse productResponse=new ProductResponse();
         productResponse.setContent(allProduct);
         productResponse.setPageNumber(productPage.getNumber());
         productResponse.setPageSize(productPage.getSize());
         productResponse.setTotalElements(productPage.getTotalElements());
         productResponse.setTotalPages(productPage.getTotalPages());
         productResponse.setLastPage(productPage.isLast());

         return productResponse;
    }

    @Override
    public ProductResponse searchProductByCategory(Long categoryId,Integer pageNumber,Integer pageSize,String sortBy,String sortOrder) {

        Sort sortByAndOrder =sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetails=PageRequest.of(pageNumber,pageSize,sortByAndOrder);

        Category category=categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("category","categoryId",categoryId));
        String categoryName=category.getCategoryName();

        Page<Product> productPage=productRepository.findByCategoryOrderByPriceAsc(category,pageDetails);

        List<Product> productsByCategory=productPage.getContent();

        if(productsByCategory.isEmpty()){
            throw new ApiException("No product created yet!!!");
        }

        List<ProductDTO> allProduct=productsByCategory.stream()
                .map((p)->modelMapper.map(p,ProductDTO.class))
                .toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(allProduct);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;

    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword,Integer pageNumber,Integer pageSize,String sortBy,String sortOrder) {


        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
        Pageable pageDetails=PageRequest.of(pageNumber,pageSize,sortByAndOrder);

        Page<Product> productPage=productRepository.findByProductNameLikeIgnoreCase("%"+ keyword+ "%",pageDetails);

        List<Product> products=productPage.getContent();

        if(products.isEmpty()){
            throw new ApiException("No product created yet!!!");
        }
        List<ProductDTO> allProduct=products.stream()
                .map((p)->modelMapper.map(p,ProductDTO.class))
                .toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(allProduct);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
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

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {

        //find the product
        Product productFromDB=productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("product","productId",productId));

        //upload the image in server
        //get the file name of uploaded image
        String fileName=fileService.uploadImage(path,image);

        //updating the new file name to the product
        productFromDB.setImage(fileName);
        //save the updated product
        Product updatedProduct=productRepository.save(productFromDB);
        return modelMapper.map(updatedProduct,ProductDTO.class);

    }


}
