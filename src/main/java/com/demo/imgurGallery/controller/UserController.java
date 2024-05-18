package com.demo.imgurGallery.controller;

import com.demo.imgurGallery.modal.Authentication.AuthRequest;
import com.demo.imgurGallery.modal.Authentication.AuthResponse;
import com.demo.imgurGallery.modal.UserRequest;
import com.demo.imgurGallery.modal.UserResponse;
import com.demo.imgurGallery.service.UserServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/v1")
@Log4j2
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/createUser")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest user) {
        UserResponse createdUser = userService.createUser(user);
        log.info("Successfully saved user : {}", createdUser.getUsername());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authorizeUser(@RequestBody AuthRequest authRequest) {
        AuthResponse accessToken = userService.authorizeUser(authRequest.getUsername(), authRequest.getPassword());
        return new ResponseEntity<>(accessToken, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        UserResponse user = userService.getUserByName(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/getAllUsers")
    public List<UserResponse> getAllUsers(){
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public String deleteUserById(@PathVariable("id") String userId) {
        return userService.deleteByUsername(userId);
    }
}
