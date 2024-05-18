package com.demo.imgurGallery.service;

import com.demo.imgurGallery.modal.ImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImgService {
    void uploadAndSaveImage(MultipartFile file, String username);

    void deleteImage(String imageId);

    List<ImageResponse> getImagesByUsername(String username);

}
