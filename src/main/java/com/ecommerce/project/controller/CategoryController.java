package com.ecommerce.project.controller;


import com.ecommerce.project.Payload.CategoryDto;
import com.ecommerce.project.Payload.CategoryResponse;
import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Tag(name="Category APIs ",description="APIs for managing  categories")
    @Operation(summary="Get All Category",description = "API to get all the  category")
    @GetMapping("/public/categories")
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


    @Tag(name="Category APIs ",description="APIs for managing  categories")
    @Operation(summary="Create New Category",description = "API to create a new category")
    @ApiResponses({
            @ApiResponse(responseCode="201", description="Category is created successfully"),
            @ApiResponse(responseCode="400", description="Invalid input", content=@Content),
            @ApiResponse(responseCode="500", description="Internal server error", content=@Content)
    })
    @PostMapping("/admin/catagory")
    public ResponseEntity<CategoryDto> createNewCategory(@Valid @RequestBody CategoryDto categoryDto){
        System.out.println("new category creation");
        CategoryDto category=categoryService.createNewCategory(categoryDto);
        return new ResponseEntity<>(category,HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDto> deleteCategoryWithId(
            @Parameter(description = "Id of the Category that you wish to delete")
            @PathVariable Long categoryId){

            CategoryDto deletedCategory= categoryService.deleteCategoryWithId(categoryId);
            return  new ResponseEntity<>(deletedCategory, HttpStatus.OK);

    }

    @PutMapping("/admin/categoreis/{categoryId}")
    public ResponseEntity<CategoryDto>  updateCategory(@Valid @RequestBody CategoryDto categoryDto,@PathVariable Long categoryId){

            CategoryDto updatedCategory=categoryService.updateCategory(categoryDto,categoryId);
            return new ResponseEntity<>(updatedCategory,HttpStatus.OK);

    }
}
