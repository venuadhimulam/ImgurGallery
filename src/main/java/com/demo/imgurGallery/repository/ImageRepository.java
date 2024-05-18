package com.demo.imgurGallery.repository;

import com.demo.imgurGallery.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, String> {

    List<ImageEntity> findByUserId(String userId);
}