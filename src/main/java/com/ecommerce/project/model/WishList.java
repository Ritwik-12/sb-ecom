package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WishList {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;


    @OneToMany()
    private List<WishlistItem> items=new ArrayList<>();

    @OneToOne
    private User user;

}
