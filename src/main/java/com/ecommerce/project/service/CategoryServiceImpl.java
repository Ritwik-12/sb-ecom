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

        List<Category> categories=categoryRepository.findAll();
        Category category=categories.stream()
                .filter(c->c.getCategoryId().equals(categoryId))
                .findFirst()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource not found"));

        categoryRepository.delete(category);
        return "category with categoryId: "+ categoryId + "deleted successfully!";

    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        List<Category> categories=categoryRepository.findAll();
        Optional<Category> optionalCategory=categories.stream()
                .filter((c)->c.getCategoryId().equals(categoryId))
                .findFirst();
        if(optionalCategory.isPresent()){
            Category existingCategory=optionalCategory.get();
            existingCategory.setCategoryName(category.getCategoryName());
            Category savedCategory=categoryRepository.save(existingCategory);
            return savedCategory;
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found");
        }
    }
}
