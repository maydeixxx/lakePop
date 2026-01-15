package com.lakepop.productService.infrastructure;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(name = "ownerEmail")
    private String ownerEmail;

    @Column(nullable = false, name = "productName")
    private String productName;

    @Column(name = "productDescription")
    private String productDescription;

    @Column(name = "productPhoto")
    private String productPhoto;

    @Column(nullable = false, name = "productPrice")
    private BigDecimal productPrice;

    @Column(name = "reviews")
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> reviews;

    @Column(name = "productCategory")
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> productCategory;
}
