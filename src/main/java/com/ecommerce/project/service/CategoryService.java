package com.ecommerce.project.service;

import com.ecommerce.project.Payload.CategoryDto;
import com.ecommerce.project.Payload.CategoryResponse;
import com.ecommerce.project.model.Category;

import java.util.List;

public interface CategoryService {

        CategoryResponse getAllCategories();

        CategoryDto createNewCategory(CategoryDto category);

        CategoryDto deleteCategoryWithId(Long categoryId);

        CategoryDto updateCategory(CategoryDto category,Long categoryId);
}
