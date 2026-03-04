package com.duan.controller;

import com.duan.dto.AuthResponseDto;
import com.duan.dto.LoginRequestDto;
import com.duan.security.JwtProvider;
import com.duan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> authenticateUser(@RequestBody LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication.getName());
        
        return userService.findByUsername(authentication.getName())
                .map(userDto -> ResponseEntity.ok(new AuthResponseDto(jwt, userDto)))
                .orElse(ResponseEntity.notFound().build());
    }
}
