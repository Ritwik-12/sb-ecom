package com.ecommerce.project.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank
    @Size(min=3, message="Product name must contains atleast 3 character")
    private String productName;

    @NotBlank
    @Size(min=3, message="Product description must contains atleast 3 character")
    private String description;

    private Integer quantity;

    private String image;

    private double price;

    private double discount;

    private double specialPrice;


    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;
}
