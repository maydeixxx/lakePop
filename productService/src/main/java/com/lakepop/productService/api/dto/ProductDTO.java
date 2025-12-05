package com.lakepop.productService.api.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ProductDTO {
    private Long productId;

    private String productName;

    private String productDescription;

    private String productPhoto;

    private String productPrice;

    private List<String> reviews;

    private Set<String> productCategory;
}
