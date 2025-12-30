package com.lakepop.productService.application.services;

import com.lakepop.productService.application.interfaces.IProductMapper;
import com.lakepop.productService.application.interfaces.IProductRepository;
import com.lakepop.productService.application.interfaces.IProductService;
import com.lakepop.productService.domain.Product;
import com.lakepop.productService.infrastructure.ProductEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        productRepository.save(mapper.productToProductEntity(product));
    }

    @Override
    public void updateProduct(Long productId, Map<String, Object> updates) {
        try {
            ProductEntity product = productRepository.findByProductId(productId);

            if(product == null){
                throw new IllegalArgumentException("Product with id - " + productId + "not found.");
            }

            updates.forEach((key, value) -> {
                switch (key) {
                    case "productName" -> product.setProductName((String) value);
                    case "productDescription" -> product.setProductDescription((String) value);
                    case "productPrice" -> product.setProductPrice((String) value);
                    case "productPhoto" -> product.setProductPhoto((String) value);
                }
            });

            productRepository.save(product);
            log.info("Product Successfully update.");
        } catch (Exception e) {
            log.error("Error while updating product. Error: {}", e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteProductById(Long productId) {
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

