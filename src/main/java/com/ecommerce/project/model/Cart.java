package com.ecommerce.project.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @OneToMany(mappedBy="cart" ,cascade ={CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE},
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private List<CartItem> items=new ArrayList<>();

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;


    private Double totalPrice=0.0;
}
