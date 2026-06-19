package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

   private List<Category> catagories= new ArrayList<>();
   private Long nextId= 1L;


    @Override
    public List<Category> getAllCategories() {
        return catagories;
    }

    @Override
    public String createNewCategory(Category catagory) {

        catagory.setCategoryId(nextId++);
        catagories.add(catagory);
        return "Category added successfully";
    }

    @Override
    public String deleteCategoryWithId(Long categoryId) {
        Category category=catagories.stream()
                .filter(c->c.getCategoryId().equals(categoryId))
                .findFirst()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource not found"));

        catagories.remove(category);
        return "category with categoryId: "+ categoryId + "deleted successfully!";

    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        Optional<Category> optionalCategory=catagories.stream()
                .filter((c)->c.getCategoryId().equals(categoryId))
                .findFirst();
        if(optionalCategory.isPresent()){
            Category existingCategory=optionalCategory.get();
            existingCategory.setCategoryName(category.getCategoryName());
            return existingCategory;
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found");
        }
    }
}
