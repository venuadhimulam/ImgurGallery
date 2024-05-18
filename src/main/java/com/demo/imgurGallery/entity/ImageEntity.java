package com.demo.imgurGallery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String deleteHash;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}