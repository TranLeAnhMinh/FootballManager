package com.example.footballmanagement.controller.api;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.footballmanagement.config.JwtUserDetails;
import com.example.footballmanagement.dto.request.UserUpdateRequest;
import com.example.footballmanagement.dto.response.UserUpdateResponse;
import com.example.footballmanagement.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    @PutMapping("/me")
    public ResponseEntity<UserUpdateResponse> updateMyInfo(
        @Valid @RequestBody UserUpdateRequest request,
        @AuthenticationPrincipal JwtUserDetails userDetails
    ){
        UUID userId = userDetails.getId(); // lấy userId từ token
        UserUpdateResponse response = userService.updateUser(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserUpdateResponse> getMyInfo(
         @AuthenticationPrincipal JwtUserDetails userDetails
    ){
        UUID userID = userDetails.getId();
        UserUpdateResponse response = userService.getMyInfo(userID);
        return ResponseEntity.ok(response);
    }
    
}
