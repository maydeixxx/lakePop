package com.lakepop.productService.application.interfaces;

import com.lakepop.productService.infrastructure.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<ProductEntity, Long> {
    ProductEntity findByProductId(Long productId);

    List<ProductEntity> findAll();

    void deleteProductByProductId(Long productId);
}
