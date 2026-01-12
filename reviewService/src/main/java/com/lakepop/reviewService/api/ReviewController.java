package com.lakepop.reviewService.api;

import com.lakepop.reviewService.services.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/reviewService")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/sendReview/{productId}")
    private ResponseEntity<?> sendReview(@RequestBody Map<String, String> request, @PathVariable Long productId) {

        try {
            reviewService.sendRequestProductReview(String.valueOf(productId), request.get("review"));
            log.info("Sent message product review");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Exception: " + e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

}
