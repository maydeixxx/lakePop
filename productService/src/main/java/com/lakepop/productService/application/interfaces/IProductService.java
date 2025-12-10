package com.lakepop.productService.application.interfaces;

import com.lakepop.productService.api.dto.ProductDTO;
import com.lakepop.productService.domain.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface IProductService {
    Product getProductById(Long productId);

    List<Product> getAllProducts();

    void createProduct(Product product);

    void updateProduct(Long productId, Map<String, Object> updates);

    void deleteProductById(Long productId);
}
