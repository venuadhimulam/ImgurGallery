package com.demo.imgurGallery.service;

import com.demo.imgurGallery.modal.Authentication.AuthResponse;
import com.demo.imgurGallery.modal.UserRequest;
import com.demo.imgurGallery.modal.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest user);

    UserResponse getUserByName(String username);

    List<UserResponse> getAllUsers();

    String deleteByUsername(String userId);

    AuthResponse authorizeUser(String username, String password);
}
