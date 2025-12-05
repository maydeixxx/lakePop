package com.lakepop.productService.application.interfaces;

import com.lakepop.productService.api.dto.ProductDTO;
import com.lakepop.productService.domain.Product;
import com.lakepop.productService.infrastructure.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IProductMapper {
    ProductEntity productToProductEntity(Product product);

    Product productEntityToProduct(ProductEntity product);

    ProductDTO productToProductDto(Product product);

    Product productDtoToProduct(ProductDTO productDto20);
}
