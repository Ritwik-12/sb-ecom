package com.ecommerce.project.controller;


import com.ecommerce.project.Payload.CategoryDto;
import com.ecommerce.project.Payload.CategoryResponse;
import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
       this.categoryService=categoryService;
    }

    @GetMapping("/public/catagory")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NUMBER,required=false) Integer pageNumber,
            @RequestParam(name="pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(name="sortBy",defaultValue=AppConstants.SORT_CATEGORIES_BY,required=false) String sortBy,
            @RequestParam(name="sortOrder",defaultValue=AppConstants.SORT_DIRECTION,required=false) String sortOrder
    ){
        System.out.println("control is here");
         CategoryResponse allCategories=categoryService.getAllCategories(pageNumber,pageSize,sortBy,sortOrder);
         return new ResponseEntity<>(allCategories,HttpStatus.OK);
    }

    //@RequestMapping(value="/api/public/category", method=RequestMethod.GET)
    @PostMapping("/public/catagory")
    public ResponseEntity<CategoryDto> createNewCategory(@Valid @RequestBody CategoryDto categoryDto){
        CategoryDto category=categoryService.createNewCategory(categoryDto);
        return new ResponseEntity<>(category,HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDto> deleteCategoryWithId(@PathVariable Long categoryId){

            CategoryDto deletedCategory= categoryService.deleteCategoryWithId(categoryId);
            return  new ResponseEntity<>(deletedCategory, HttpStatus.OK);

    }

    @PutMapping("/admin/categoreis/{categoryId}")
    public ResponseEntity<CategoryDto>  updateCategory(@Valid @RequestBody CategoryDto categoryDto,@PathVariable Long categoryId){

            CategoryDto updatedCategory=categoryService.updateCategory(categoryDto,categoryId);
            return new ResponseEntity<>(updatedCategory,HttpStatus.OK);

    }
}
