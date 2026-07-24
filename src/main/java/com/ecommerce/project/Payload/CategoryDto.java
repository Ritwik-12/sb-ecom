package com.ecommerce.project.Payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    @Schema(description = "Category ID for a particular category")
    private Long categoryId;

    @Schema(description = "Category name that you wish to create")
    private String categoryName;
}
