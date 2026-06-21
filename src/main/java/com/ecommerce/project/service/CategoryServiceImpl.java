package com.ecommerce.project.service;

import com.ecommerce.project.Repositories.CategoryRepository;
import com.ecommerce.project.model.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

        return categoryRepository.findAll();
    }

    @Override
    public void createNewCategory(Category catagory) {

        Category c=categoryRepository.save(catagory);


    }

    @Override
    public String deleteCategoryWithId(Long categoryId) {

             categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource not found"));
            categoryRepository.deleteById(categoryId);
        return "category with categoryId: "+ categoryId + "deleted successfully!";
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {

         Category savedCategory=categoryRepository.findById(categoryId)
                 .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
         category.setCategoryId(categoryId);
          savedCategory=categoryRepository.save(category);
          return savedCategory;
    }
}
