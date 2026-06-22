package com.ecommerce.project.service;

import com.ecommerce.project.Repositories.CategoryRepository;
import com.ecommerce.project.exception.ApiException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {


    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository){
        this.categoryRepository=categoryRepository;
    }



    @Override
    public List<Category> getAllCategories() {

        List<Category> categories= categoryRepository.findAll();
        if(categories.isEmpty()){
            throw new ApiException("No category created till now !!!");
        }
        return categories;
    }

    @Override
    public void createNewCategory(Category catagory) {

        Category savedCategory=categoryRepository.findByCategoryName(catagory.getCategoryName());
        if(savedCategory!=null){
            throw new ApiException("Category with "+savedCategory.getCategoryName() +" already exists");
        }
        Category c=categoryRepository.save(catagory);


    }

    @Override
    public String deleteCategoryWithId(Long categoryId) {

             categoryRepository.findById(categoryId)
                       .orElseThrow(()->new ResourceNotFoundException("category","categoryId",categoryId));
            categoryRepository.deleteById(categoryId);
        return "category with categoryId: "+ categoryId + "deleted successfully!";
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {

        Category getSavedCategory=categoryRepository.findByCategoryName(category.getCategoryName());

        if(getSavedCategory!=null){
            throw new ApiException("Category with "+category.getCategoryName()+" already exists");
        }

         Category savedCategory=categoryRepository.findById(categoryId)
                         .orElseThrow(()->new ResourceNotFoundException("category","categoryId",categoryId));
         category.setCategoryId(categoryId);
          savedCategory=categoryRepository.save(category);
          return savedCategory;
    }
}
