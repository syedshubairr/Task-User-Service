package com.shah.controller;

import com.shah.config.JwtProvider;
import com.shah.dto.request.loginRequest;
import com.shah.dto.response.AuthResponse;
import com.shah.modal.User;
import com.shah.repository.UserRepository;
import com.shah.service.CustomUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomUserServiceImpl customUserService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception {
        String email = user.getEmail();
        String password = user.getPassword();
        String fullName = user.getFullName();
        String role = user.getRole();
        User alreadyRegistered = userRepository.findByEmail(email);
        if (alreadyRegistered != null) {
            throw new Exception("User already Registered.");
        }
        User createdUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .role(role)
                .build();
        User savedUser = userRepository.save(createdUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = JwtProvider.generateToken(authentication);
        AuthResponse authResponse = AuthResponse.builder()
                .jwt(token)
                .message("Register Success")
                .status(true)
                .build();
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signIn(@RequestBody loginRequest req) {
        String username = req.getEmail();
        String password = req.getPassword();
        System.out.println(username + "----------" + password);
        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = JwtProvider.generateToken(authentication);
        AuthResponse authResponse = AuthResponse.builder()
                .message("Login Successful")
                .status(true)
                .jwt(token)
                .build();
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customUserService.loadUserByUsername(username);
        System.out.println("Sign-In User Details" + userDetails);
        if (userDetails == null) {
            System.out.println("Sign-in User Details - null");
            throw new BadCredentialsException("Invalid Username or Password");
        }
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            System.out.println("Sign-in User Details - incorrect password " + userDetails);
            throw new BadCredentialsException("Invalid Username or Password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
