package com.lakepop.productService.application.services;

import com.lakepop.productService.application.interfaces.IProductMapper;
import com.lakepop.productService.application.interfaces.IProductRepository;
import com.lakepop.productService.application.interfaces.IProductService;
import com.lakepop.productService.domain.Product;
import com.lakepop.productService.infrastructure.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

//ДОБАВЬ КОММЕНТАРИИ И ОБРАБОТКУ ОШИБОК С ЛОГИРОВАНИЕМ ПЖ!!!!
@Service
@RequiredArgsConstructor
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
        ProductEntity product = productRepository.findByProductId(productId);

        if(product == null){
            throw new NullPointerException("Product with id - " + productId + "not found.");
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
    }

    @Override
    @Transactional
    public void deleteProductById(Long productId) {
        productRepository.deleteProductByProductId(productId);
    }

    @Override
    public List<Product> getAllProducts() {
        List<ProductEntity> allProducts = productRepository.findAll();
        return allProducts.stream()
                .map(mapper::productEntityToProduct)
                .toList();
    }

    public void handleReview(Long productId, String review) {
        try {
            Product productById = getProductById(productId);
            List<String> reviews = productById.getReviews();
            reviews.add(review);

            productById.setReviews(reviews);
            productRepository.saveAndFlush(mapper.productToProductEntity(productById));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

