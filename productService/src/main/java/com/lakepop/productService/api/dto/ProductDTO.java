package com.lakepop.productService.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
public class ProductDTO {
    private Long productId;

    private String ownerUsername;

    private String productName;

    private String productDescription;

    private String productPhoto;

    private BigDecimal productPrice;

    private List<String> reviews;

    private Set<String> productCategory;

}
