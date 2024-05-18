package com.demo.imgurGallery.controller;

import com.demo.imgurGallery.modal.ImageResponse;
import com.demo.imgurGallery.service.ImageServiceImpl;
import com.demo.imgurGallery.service.ImgService;
import com.demo.imgurGallery.util.JwtUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageServiceImpl imgService;

    @Autowired
    private JwtUtil jwtUtil;

//    @PostMapping("/uploadImage/{accessToken}")
//    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file, @PathVariable String accessToken) {
//        try {
//            //Validating token
//            jwtUtil.validateToken(accessToken);
//            String username = jwtUtil.extractUsername(accessToken);
//            log.info("Username from token is: {}", username);
//            // Upload & save the image associated with the user.
//            imgService.uploadAndSaveImage(file, username, accessToken);
//            log.info("Image uploaded successfully for user: {}", username);
//            return ResponseEntity.ok("Image uploaded successfully");
//        } catch (Exception e) {
//            // Log the exception for debugging
//            log.error("Error while uploading the image for user: {}", username, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload failed");
//        }
//    }

    @PostMapping("/uploadImage")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file,
                                              @RequestHeader("Authorization") String accessToken) {
        String username = null;
        try {
            username = getUsernameFromToken(accessToken);
            log.info("Username extracted from token: {}", username);

            // Upload & save the image associated with the user.
            imgService.uploadAndSaveImage(file, username);
            log.info("Image uploaded successfully for user: {}", username);

            return ResponseEntity.ok("Image uploaded successfully");
        } catch (Exception e) {
            if (username != null) {
                log.error("Error while uploading the image for user: {}", username, e);
            } else {
                log.error("Error while validating token or extracting username.", e);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload failed");
        }
    }

    @DeleteMapping("/deleteImage/{imageId}")
    public ResponseEntity<String> deleteImageForAuthenticatedUser(@PathVariable String imageId,
                                                                  @RequestHeader("Authorization") String accessToken) {
        try {
            if (accessToken.startsWith("Bearer ")) {
                accessToken = accessToken.substring(7);
            }
            validateToken(accessToken);
            log.info("Validating token");

            //Deleting the image with ImageId
            imgService.deleteImage(imageId);
            log.info("Image deleted successfully for user: {}", imageId);
            return ResponseEntity.ok("Image deleted successfully");
        } catch (Exception e) {
            log.error("Error while deleting the image", e);
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/viewImages")
    public ResponseEntity<List<ImageResponse>> viewImages(@RequestHeader("Authorization") String accessToken) {
        String username = null;
        List<ImageResponse> userImages = new ArrayList<>();
        try {
            username = getUsernameFromToken(accessToken);
            log.info("Username extracted from token: {}", username);

            // Retrieve the user by username
            userImages = imgService.getImagesByUsername(username);
            log.info("Image uploaded successfully for user: {}", username);

            return ResponseEntity.ok(userImages);
        } catch (Exception e) {
            if (username != null) {
                log.error("Error while viewing the image for user: {}", username, e);
            } else {
                log.error("Error while validating token or extracting username.", e);
            }
            return ResponseEntity.ok(userImages);
        }
    }

    private String getUsernameFromToken(String accessToken) {
        try {
            if (accessToken.startsWith("Bearer ")) {
                accessToken = accessToken.substring(7);
            }
            validateToken(accessToken);
            String username = extractUsername(accessToken);
            log.info("Username extracted from token: {}", username);
            return username;
        } catch (Exception e) {
            log.error("Error while validating token or extracting username.", e);
            throw new RuntimeException("Invalid or expired token.", e);
        }
    }

    private void validateToken(String accessToken) {
        try {
            jwtUtil.validateToken(accessToken);
            log.error("Token validation Successfully.");
        } catch (Exception e) {
            log.error("Token validation failed.", e);
            throw new RuntimeException("Invalid or expired token.", e);
        }
    }

    private String extractUsername(String token) {
        try {
            return jwtUtil.extractUsername(token);
        } catch (Exception e) {
            log.error("Failed to extract username from token.", e);
            throw new RuntimeException("Failed to extract username.", e);
        }
    }

}
