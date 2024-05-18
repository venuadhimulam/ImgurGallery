package com.demo.imgurGallery.modal.Authentication;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AuthResponse {
    private final String jwt;
}
