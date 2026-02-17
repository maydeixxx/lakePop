package com.lakepop.productService.application.interfaces;

import com.lakepop.productService.infrastructure.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByProductId(Long productId);

    List<ProductEntity> findAll();

    void deleteProductByProductId(Long productId);
}
