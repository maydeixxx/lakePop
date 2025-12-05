package com.lakepop.productService.infrastructure;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column
    private String productName;

    @Column
    private String productDescription;

    @Column
    private String productPhoto;

    @Column
    private String productPrice;

    @Column
    private Set<String> productCategory;
}
