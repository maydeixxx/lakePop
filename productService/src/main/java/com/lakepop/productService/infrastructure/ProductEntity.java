package com.lakepop.productService.infrastructure;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false, name = "productName")
    private String productName;

    @Column(name = "productDescription")
    private String productDescription;

    @Column(name = "productPhoto")
    private String productPhoto;

    @Column(nullable = false, name = "productPrice")
    private String productPrice;

    @Column(name = "reviews")
    @ElementCollection
    private List<String> reviews;

    @ElementCollection
    @Column(name = "productCategory")
    private Set<String> productCategory;
}
