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
    public ResponseEntity<?> getProductById(@PathVariable Long productId){
        try {
            Product product = service.getProductById(productId);
            ProductDTO productDTO = mapper.productToProductDto(product);

            return new ResponseEntity<>(productDTO, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Exception: " + e.getMessage());
        }
    }

    @GetMapping("/all_products")
    public ResponseEntity<?> getAllProducts(){
        try {
            List<Product> products = service.getAllProducts();
            List<ProductDTO> productDTOS = products.stream()
                    .map(mapper::productToProductDto)
                    .toList();

            return new ResponseEntity<>(productDTOS, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Exception: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete_product/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId){
        try {
            service.deleteProductById(productId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Exception: " + e.getMessage());
        }
    }

    @PatchMapping("/update_product/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody Map<String, Object> updates){
        try {
            service.updateProduct(productId, updates);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Exception: " + e.getMessage());
        }
    }

    @PostMapping("/create_product/")
    public ResponseEntity<?> createProduct(@RequestBody ProductDTO productDTO){
        try {
            Product product = service.getProductById(productDTO.getProductId());

            if(product != null){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "product already exists.");
            } else {
                service.createProduct(mapper.productDtoToProduct(productDTO));
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Exception: " + e.getMessage());
        }
    }
}
