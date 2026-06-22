package com.ecommerce.project.controller;


import com.ecommerce.project.model.Category;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
       this.categoryService=categoryService;
    }

    @GetMapping("/public/catagory")
    public ResponseEntity<List<Category>> getAllCategories(){
         List<Category> allCategories=categoryService.getAllCategories();
         return new ResponseEntity<>(allCategories,HttpStatus.OK);
    }

    //@RequestMapping(value="/api/public/category", method=RequestMethod.GET)
    @PostMapping("/public/catagory")
    public ResponseEntity<String> createNewCategory(@Valid @RequestBody Category catagory){
        categoryService.createNewCategory(catagory);
        return new ResponseEntity<>("Category added successfully",HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategoryWithId(@PathVariable Long categoryId){

            String status= categoryService.deleteCategoryWithId(categoryId);
            return  new ResponseEntity<>(status, HttpStatus.OK);

    }

    @PutMapping("/admin/categoreis/{categoryId}")
    public ResponseEntity<String>  updateCategory(@Valid @RequestBody Category category,@PathVariable Long categoryId){

            Category savedCategory=categoryService.updateCategory(category,categoryId);
            return new ResponseEntity<>("category updated successfully with id "+categoryId,HttpStatus.OK);

    }
}
