package com.demo.imgurGallery.service;

import com.demo.imgurGallery.entity.UserEntity;
import com.demo.imgurGallery.exception.CustomException;
import com.demo.imgurGallery.modal.Authentication.AuthResponse;
import com.demo.imgurGallery.modal.UserRequest;
import com.demo.imgurGallery.modal.UserResponse;
import com.demo.imgurGallery.repository.UserRepository;
import com.demo.imgurGallery.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

//    @Autowired
//    private AuthenticationManager authenticationManager;

    @Override
    public UserResponse createUser(UserRequest user) {
        log.info("Checking whether username already exist or not");

        String username = user.getUsername();
        log.debug("Username provided: {}", username);

        Optional<UserEntity> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            log.warn("User with username '{}' already exists", username);
            throw new CustomException("User already exists", "USER_EXISTS");
        }

        String encrypted_Password = passwordEncoder.encode(user.getPassword());

        // Create a new UserEntity from the UserRequest
        UserEntity userEntity = UserEntity.builder()
                .username(user.getUsername())
                .password(encrypted_Password)
                .email(user.getEmail())
                .gender(user.getGender())
                .build();

        // Save the new user entity to the database
        userEntity = userRepository.save(userEntity);

        // Created a new UserResponse and copied data from the saved UserEntity
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(userEntity, userResponse);

        return userResponse;
    }

    @Override
    public UserResponse getUserByName(String username) {
        UserEntity userEntity = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new CustomException("User doesn't exist", "USER_NOT_EXIST"));

        // Created a new UserResponse and copied data from the saved UserEntity
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(userEntity, userResponse);

        return userResponse;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();

        for (UserEntity userEntity : userEntities) {
            UserResponse userResponse = new UserResponse();
            userResponse.setUserId(userEntity.getId());
            userResponse.setUsername(userEntity.getUsername());
//            userResponse.setPassword(userEntity.getPassword());
            userResponse.setEmail(userEntity.getEmail());
            userResponse.setGender(userEntity.getGender());
            userResponses.add(userResponse);
        }
        return userResponses;
    }

    @Override
    public String deleteByUsername(String userId) {
        try {
            userRepository.deleteById(userId);
            return "Employee with Id: " + userId + " deleted successfully!";
        } catch (Exception e) {
            throw new CustomException("Failed to delete user with Id: " + userId, "USER_DELETE_ERROR");
        }
    }

    @Override
    public AuthResponse authorizeUser(String username, String password) {
        try {
            Optional<UserResponse> userOptional = Optional.ofNullable(getUserByName(username));
            if (userOptional.isPresent()) {
                UserResponse userDetails = userOptional.get();
                if (passwordEncoder.matches(password, userDetails.getPassword())) {
                    final String jwt = jwtUtil.generateToken(userDetails.getUsername());
                    return new AuthResponse(jwt);
//                    return "Token : " + jwt;
                } else {
                    throw new CustomException("Invalid username/password", "BAD_CREDENTIALS");
                }
            } else {
                throw new CustomException("User doesn't exist", "USER_NOT_EXIST");
            }
        } catch (Exception e) {
            e.printStackTrace();
//            return "An error occurred during authorization. Please try again later.";
            throw new CustomException("An error occurred during authorization.", "AUTH_ERROR");
        }
    }

}
