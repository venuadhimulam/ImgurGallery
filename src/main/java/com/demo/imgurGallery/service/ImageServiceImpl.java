package com.demo.imgurGallery.service;

import com.demo.imgurGallery.entity.ImageEntity;
import com.demo.imgurGallery.entity.UserEntity;
import com.demo.imgurGallery.exception.CustomException;
import com.demo.imgurGallery.modal.ImageResponse;
import com.demo.imgurGallery.modal.ImgurModal.ImgurResponse;
import com.demo.imgurGallery.modal.UserResponse;
import com.demo.imgurGallery.repository.ImageRepository;
import com.demo.imgurGallery.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ImageServiceImpl implements ImgService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Value("${imgur.uploadUrl}")
    private String uploadUrl;

    @Value("${imgur.deleteUrl}")
    private String deleteUrl;

    @Value("${imgur.clientId}")
    private String clientId;

    @Value("${imgur.client.access_token}")
    private String clientAccessToken;

    public void uploadAndSaveImage(MultipartFile file, String username) {

        // Upload the image to Imgur API
        ImgurResponse res = uploadImg(file);

        //Obtain Imgur API
        String imgurUrl = res.getData().getLink();
        log.info("Image uploaded successfully, URL: {}", imgurUrl);


        // Retrieve the user by username
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        //Saving the image to Image Repo
        ImageEntity image = new ImageEntity();
        image.setImageUrl(imgurUrl);
        image.setUser(user);
        image.setDeleteHash(res.getData().getId());
        imageRepository.save(image);
    }

    @Override
    public void deleteImage(String imageId) {
        try {
            ImageEntity image = imageRepository.findById(imageId)
                    .orElseThrow(() -> new CustomException("Image not found with the Image Id : " + imageId, "IMG_NOT_FOUND"));

//            log.info("Deleting image from Imgur API with url: {}", image.getImageUrl());
//            deleteImageFromService(image.getImageUrl(), image.getDeleteHash());

            log.info("Deleting image from repo with url: {}", image.getImageUrl());
            imageRepository.delete(image);
        } catch (CustomException e) {
            log.error("Error during image deletion: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("An unexpected error occurred during image deletion: {}", e.getMessage(), e);
            throw new CustomException("An unexpected error occurred", "UNEXPECTED_ERROR");
        }
    }

    private void deleteImageFromService(String imageUrl, String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
//            headers.set("Authorization", "Client-ID " + clientId);

//            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//            body.add("image", file.getResource());

//            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            String deleteEndPoint = deleteUrl + "/" + accessToken;
            new RestTemplate().exchange(
                    deleteEndPoint,
                    HttpMethod.DELETE,
                    requestEntity,
                    Void.class
            );
        } catch (HttpClientErrorException e) {
            throw new CustomException("Image deletion failed: " + e.getMessage(), "IMG_DELETION_ERROR");
        }
    }

    @Override
    public List<ImageResponse> getImagesByUsername(String username) {
        try {
            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            List<ImageEntity> imageEntityList = imageRepository.findByUserId(user.getId());

            List<ImageResponse> imageResponseList = new ArrayList<>();
            for (ImageEntity imageEntity : imageEntityList) {
                ImageResponse imageResponse = ImageResponse.builder()
                        .imageId(imageEntity.getId())
                        .imageUrl(imageEntity.getImageUrl())
                        .build();

                UserEntity userEntity = imageEntity.getUser();
                UserResponse userResponse = UserResponse.builder()
                        .userId(userEntity.getId())
                        .username(userEntity.getUsername())
                        .email(userEntity.getEmail())
                        .gender(userEntity.getGender())
                        .build();
                imageResponse.setUser(userResponse);
                imageResponseList.add(imageResponse);
            }
            return imageResponseList;
        } catch (Exception e) {
            log.error("An error occurred while retrieving images for user: {}", username, e);
            throw new RuntimeException("Failed to retrieve images", e);
        }
    }

    public ImgurResponse uploadImg(MultipartFile file) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//        headers.setBearerAuth(accessToken);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", file.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<ImgurResponse> responseEntity = new RestTemplate().exchange(
                    uploadUrl + "?client_id=" + clientId,
                    HttpMethod.POST,
                    requestEntity,
                    ImgurResponse.class
            );

            ImgurResponse res = responseEntity.getBody();
            if (res != null && res.isSuccess()) {
                return res;
            } else {
                throw new RuntimeException("Image upload failed: " + res.getStatus_code());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        }
    }
}
