package com.ecommerce.project.Payload;

import com.ecommerce.project.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {

    private List<CategoryDto> content;
}
