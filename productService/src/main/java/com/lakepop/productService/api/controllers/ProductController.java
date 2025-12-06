package com.lakepop.productService.api.controllers;

import com.lakepop.productService.api.dto.ProductDTO;
import com.lakepop.productService.application.interfaces.IProductMapper;
import com.lakepop.productService.application.services.ProductService;
import com.lakepop.productService.domain.Product;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/")
public class ProductController {
    private final ProductService service;
    private final IProductMapper mapper;

    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long productId) {
        Product product = service.getProductById(productId);
        ProductDTO productDTO = mapper.productToProductDto(product);

        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

    @GetMapping("/all_products")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<Product> products = service.getAllProducts();
        List<ProductDTO> productDTOS = products.stream()
                .map(mapper::productToProductDto)
                .toList();

        return new ResponseEntity<>(productDTOS, HttpStatus.OK);
    }

    @DeleteMapping("/delete_product/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        service.deleteProductById(productId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/update_product/{productId}")
    public ResponseEntity<Void> updateProduct(@PathVariable Long productId, @RequestBody Map<String, Object> updates) {
        service.updateProduct(productId, updates);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/create_product/")
    public ResponseEntity<Void> createProduct(@RequestBody ProductDTO productDTO) {
        service.createProduct(mapper.productDtoToProduct(productDTO));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
