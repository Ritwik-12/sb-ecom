package com.ecommerce.project.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Role;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="addresses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min=5, message="Street name must be atleast 5 character")
    private String street;

    @NotBlank
    @Size(min=5, message="Building name must be atleast 5 character")
    private  String buildingName;

    @NotBlank
    @Size(min=4,message="City name must be atleast 5 character")
    private String city;

    @NotBlank
    @Size(min=2,message="state name must be atleast 2 character")
    private String state;

    @NotBlank
    @Size(min=5,message="Bulding name must be atleast 5 character")
    private String country;


    @NotBlank
    @Size(min=6,message="Bulding name must be atleast 6 character")
    private String pinCode;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

}
