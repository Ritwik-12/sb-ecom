package com.ecommerce.project.service;

import com.ecommerce.project.Payload.CategoryDto;
import com.ecommerce.project.Payload.CategoryResponse;
import com.ecommerce.project.Repositories.CategoryRepository;
import com.ecommerce.project.exception.ApiException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {


    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository,ModelMapper modelMapper){
        this.categoryRepository=categoryRepository;
        this.modelMapper=modelMapper;
    }



    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize,String sortBy,String sortOrder) {

        System.out.println("control is now here");
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetails=PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Category> categoryPage=categoryRepository.findAll(pageDetails);

        List<Category> categories= categoryPage.getContent();
        if(categories.isEmpty()){
            throw new ApiException("No category created till now !!!");
        }

             List<CategoryDto>  categoryDtos= categories.stream()
                     .map((category)->modelMapper.map(category,CategoryDto.class))
                     .toList();

            CategoryResponse categoryResponse=new CategoryResponse();
            categoryResponse.setContent(categoryDtos);
            categoryResponse.setPageNumber(categoryPage.getNumber());
            categoryResponse.setPageSize(categoryPage.getSize());
            categoryResponse.setTotalElements(categoryPage.getTotalElements());
            categoryResponse.setTotalPages(categoryPage.getTotalPages());
            categoryResponse.setLastPage(categoryPage.isLast());

            return categoryResponse;
    }

    @Override
    public CategoryDto createNewCategory(CategoryDto catagoryDto) {

        Category categoryToCreate=modelMapper.map(catagoryDto,Category.class);
        Category savedCategory=categoryRepository.findByCategoryName(categoryToCreate.getCategoryName());
        if(savedCategory!=null){
            throw new ApiException("Category with "+savedCategory.getCategoryName() +" already exists");
        }
        Category c=categoryRepository.save(categoryToCreate);

        return modelMapper.map(c,CategoryDto.class);
    }

    @Override
    public CategoryDto deleteCategoryWithId(Long categoryId) {

            Category categoryToDelete= categoryRepository.findById(categoryId)
                       .orElseThrow(()->new ResourceNotFoundException("category","categoryId",categoryId));
            categoryRepository.deleteById(categoryId);
          return modelMapper.map(categoryToDelete,CategoryDto.class);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId) {

        Category categoryToUpdate=modelMapper.map(categoryDto,Category.class);
        Category getSavedCategory=categoryRepository.findByCategoryName(categoryToUpdate.getCategoryName());

        if(getSavedCategory!=null){
            throw new ApiException("Category with "+categoryToUpdate.getCategoryName()+" already exists");
        }

         Category savedCategory=categoryRepository.findById(categoryId)
                         .orElseThrow(()->new ResourceNotFoundException("category","categoryId",categoryId));
          categoryToUpdate.setCategoryId(categoryId);
          savedCategory=categoryRepository.save(categoryToUpdate);
          return modelMapper.map(savedCategory,CategoryDto.class);
    }
}
