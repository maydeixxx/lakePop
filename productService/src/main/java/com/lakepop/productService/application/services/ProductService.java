package com.lakepop.productService.application.services;

import com.lakepop.productService.application.interfaces.IProductMapper;
import com.lakepop.productService.application.interfaces.IProductRepository;
import com.lakepop.productService.application.interfaces.IProductService;
import com.lakepop.productService.domain.Product;
import com.lakepop.productService.infrastructure.ProductEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements IProductService {
    private final IProductRepository productRepository;
    private final IProductMapper mapper;

    @Override
    public Product getProductById(Long productId) {
        return mapper.productEntityToProduct(productRepository.findByProductId(productId));
    }

    @Override
    public void createProduct(Product product) {
        String username = getPrincipal();
        product.setOwnerUsername(username);

        productRepository.save(mapper.productToProductEntity(product));
    }

    @Override
    @Transactional
    public void updateProduct(Long productId, Map<String, Object> updates) {
        String userName = getPrincipal();

        try {
            ProductEntity product = productRepository.findByProductId(productId);

            if (product == null) {
                throw new IllegalArgumentException("Product with id - " + productId + "not found.");
            }

            if (product.getOwnerUsername().equals(userName)) {

                updates.forEach((key, value) -> {
                    switch (key) {
                        case "productName" -> product.setProductName((String) value);
                        case "productDescription" -> product.setProductDescription((String) value);
                        case "productPrice" -> product.setProductPrice((BigDecimal) value);
                        case "productPhoto" -> product.setProductPhoto((String) value);
                    }
                });

                log.info("Product Successfully update.");
            } else {
                throw new IllegalArgumentException("Чужое объявление");
            }
        } catch (Exception e) {
            log.error("Error while updating product. Error: {}", e.getMessage());
        }
    }

    public String getPrincipal() {
        return Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
    }

    @Override
    @Transactional
    public void deleteProductById(Long productId) {
        String userName = getPrincipal();
        Product productById = getProductById(productId);

        if (productById == null) {
            throw new NullPointerException("There is no ad by id [" + productId + "]");
        }

        if (!productById.getOwnerUsername().equals(userName)) {
            throw new IllegalArgumentException("Чужое объявление");
        }

        productRepository.deleteProductByProductId(productId);
        log.info("Product successfully deleted.");
    }

    @Override
    public List<Product> getAllProducts() {
        List<ProductEntity> allProducts = productRepository.findAll();
        return allProducts.stream()
                .map(mapper::productEntityToProduct)
                .toList();
    }

    /**
     * Метод для добавления отзыва к товару
     * @param productId id продукта
     * @param review отзыв полученный из kafka
     */
    public void handleReview(Long productId, String review) {
        if (productId == null) {
            log.error("Product id is null");
            throw new NullPointerException();
        }

        if (review == null) {
            log.error("Review is null");
            throw new NullPointerException();
        }

        try {
            Product productById = getProductById(productId);
            List<String> reviews = productById.getReviews();

            if (reviews == null) {
                reviews = new ArrayList<>();
            }

            reviews.add(review);
            productById.setReviews(reviews);

            productRepository.save(mapper.productToProductEntity(productById));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

