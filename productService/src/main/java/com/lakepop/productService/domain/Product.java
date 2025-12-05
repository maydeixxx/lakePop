package com.lakepop.productService.domain;

import lombok.Data;

import java.util.Set;

@Data
public class Product {
    private Long productId;

    private String productName;

    private String productDescription;

    private String productPhoto;

    private String productPrice;

    private Set<String> productCategory;
}
