package com.lakepop.reviewService.api;

import com.lakepop.reviewService.services.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviewService")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ProducerService producerService;

    @PostMapping("/sendReview/{productId}")
    private ResponseEntity<?> sendReview(@RequestBody String review, @PathVariable Long productId) {

        try {
            producerService.sendRequestProductReview(String.valueOf(productId), review);
            log.info("Sent message product review");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Exception: " + e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

}
