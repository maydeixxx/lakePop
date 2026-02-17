package com.lakepop.productService.application.interfaces;

import com.lakepop.productService.api.dto.ProductDTO;
import com.lakepop.productService.application.models.Product;
import com.lakepop.productService.infrastructure.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IProductMapper {
    @Mapping(target = "ownerUsername", source = "ownerUsername")
    ProductEntity productToProductEntity(Product product);

    @Mapping(target = "ownerUsername", source = "ownerUsername")
    Product productEntityToProduct(ProductEntity product);

    ProductDTO productToProductDto(Product product);

    Product productDtoToProduct(ProductDTO productDto20);
}
