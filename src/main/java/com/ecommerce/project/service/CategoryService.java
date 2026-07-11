package com.ecommerce.project.service;

import com.ecommerce.project.Payload.CategoryDto;
import com.ecommerce.project.Payload.CategoryResponse;

import org.springframework.stereotype.Service;


public interface CategoryService {

        CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize,String sortBy,String sortOrder);

        CategoryDto createNewCategory(CategoryDto category);

        CategoryDto deleteCategoryWithId(Long categoryId);

        CategoryDto updateCategory(CategoryDto category,Long categoryId);
}
