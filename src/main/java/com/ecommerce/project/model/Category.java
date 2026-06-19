package com.ecommerce.project.model;

public class Category {

    private Long categoryId;
    private String categoryName;

    public Category(Long categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryId(Long id){
        this.categoryId=id;
    }
    public void setCategoryName(String name){
        this.categoryName=name;
    }
}
