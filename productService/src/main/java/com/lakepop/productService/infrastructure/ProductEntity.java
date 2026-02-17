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

    private String ownerUsername;

    @Column(nullable = false)
    private String productName;

    private String productDescription;

    private String productPhoto;

    @Column(nullable = false)
    private BigDecimal productPrice;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> reviews;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> productCategory;
}
