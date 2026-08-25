package com.ecommerce.project.service;

import com.ecommerce.project.Payload.CartDTO;
import com.ecommerce.project.Payload.ProductDTO;
import com.ecommerce.project.Payload.ProductResponse;
import com.ecommerce.project.Repositories.CartRepository;
import com.ecommerce.project.Repositories.CategoryRepository;
import com.ecommerce.project.Repositories.ProductRepository;
import com.ecommerce.project.exception.ApiException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;
    private final CartRepository cartRepository;
    private final CartService  cartService;

    @Value("${project.image}")
    private String path;

    @Value("${image.base.url}")
    private String imageBaseUrl;

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
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword, String category) {


        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);


        Specification<Product> spec = null;

        if (keyword != null && !keyword.isEmpty()) {
            Specification<Product> keywordSpec = (root, query, cb) ->
                    cb.like(cb.lower(root.get("productName")), "%" + keyword.toLowerCase() + "%");
            spec = keywordSpec;
        }

        if (category != null && !category.isEmpty()) {
            Specification<Product> categorySpec = (root, query, cb) ->
                    cb.like(cb.lower(root.get("category").get("categoryName")), "%" + category.toLowerCase() + "%");
            spec = (spec == null) ? categorySpec : spec.and(categorySpec);
        }




        Page<Product> productPage=productRepository.findAll(spec,pageDetails);

        List<Product> products=productPage.getContent();

       if(products.isEmpty()){
           throw new ApiException("No product created yet!!!");
       }
         List<ProductDTO> allProduct=products.stream()
                 .map((p)->
                         {
                            ProductDTO productDTO= modelMapper.map(p,ProductDTO.class);
                            productDTO.setImage(constructImageUrl(p.getImage()));
                            return productDTO;
                         })
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
    private String constructImageUrl(String imageName){
        return imageBaseUrl.endsWith("/")?imageBaseUrl+imageName : imageBaseUrl +"/"+imageName;
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
        System.out.println("updated product rpice "+product1.getPrice());
        double specialPrice = product1.getPrice() -
                ((product1.getDiscount() * 0.01) * product1.getPrice());
        product1.setSpecialPrice(specialPrice);
        System.out.println("p1sp"+ product1.getSpecialPrice());
       product1.setProductId(productId);
//       product.setProductName(product1.getProductName());
//       product.setDescription(product1.getDescription());
//       product.setQuantity(product1.getQuantity());
//       product.setDiscount(product1.getDiscount());
//       product.setPrice(product1.getPrice());
//       product.setSpecialPrice(product1.getSpecialPrice());
        System.out.println("product price"+product1.getSpecialPrice());
       Product savedProduct=productRepository.save(product1);

       List<Cart> carts=cartRepository.findCartByProductId(productId);
       // System.out.println("user cart "+carts.getFirst().getId());
    //    System.out.println("cart is "+carts.getFirst());
       // System.out.println(carts.getFirst().getTotalPrice());
       List<CartDTO> cartDTOS=carts.stream().map(cart->{
           CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);
           List<ProductDTO> productDTOS=cart.getItems().stream().map(p->
                   modelMapper.map(p,ProductDTO.class))
                   .toList();
           cartDTO.setProducts(productDTOS);
           return cartDTO;
       }).toList();

       cartDTOS.forEach(cart->cartService.updateProductInCart(cart.getCartId(),productId));

       return modelMapper.map(savedProduct,ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product=productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("product","productId",productId));

        List<Cart> carts=cartRepository.findCartByProductId(productId);
        carts.forEach(cart->cartService.deleteProductFromCart(cart.getId(),productId ));

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
