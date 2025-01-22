package com.mars.user;

import com.mars.common.entity.User;
import com.mars.common.interfaces.UserService;
import com.mars.user.dto.AuthResponse;
import com.mars.user.dto.ChangePasswordRequest;
import com.mars.user.dto.LoginRequest;
import com.mars.user.dto.UpdateProfileRequest;
import com.mars.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @Validated @RequestBody User user,
            @RequestParam(required = false, defaultValue = "customer") String role) {
         role = role.toLowerCase();
        String response = userService.registerUser(user, role);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.authenticateUser(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

            
            String redirectUrl = (user.getRole().equals("customer")) ? "/customer-dashboard.html" : "/clerkhome.html";
            
            return ResponseEntity.ok(new AuthResponse(token, user, redirectUrl));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            if (!jwtUtil.validateToken(jwtToken)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }
            String username = jwtUtil.extractUsername(jwtToken);
            User user = userService.findByUsername(username);

            if (user == null) {
                return ResponseEntity.status(404).body("User not found");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("role", user.getRole()); 
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Error: " + e.getMessage());
        }
    }

    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            jwtUtil.invalidateToken(jwtToken);
            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during logout");
        }
    }

    
    @PostMapping("/profile/update")
    public ResponseEntity<AuthResponse> updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody UpdateProfileRequest request) {
        try {
            String jwtToken = token.substring(7);
            if (!jwtUtil.validateToken(jwtToken)) {
                return ResponseEntity.badRequest().body(null);
            }
            String username = jwtUtil.extractUsername(jwtToken);

            
            String newToken = userService.updateProfile(username, request);

            
            User updatedUser = userService.findByUsername(request.getUsername());
            
            return ResponseEntity.ok(new AuthResponse(newToken, updatedUser));
        } catch (Exception e) {
            e.printStackTrace();  
            return ResponseEntity.badRequest().body(null);
        }
    }

    
    @PostMapping("/profile/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody ChangePasswordRequest request) {
        try {
            String jwtToken = token.substring(7);
            if (!jwtUtil.validateToken(jwtToken)) {
                return ResponseEntity.badRequest().body("Invalid token");
            }

            String username = jwtUtil.extractUsername(jwtToken);

            
            User user = userService.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(404).body("User not found");
            }

            
            if (request.getCurrentPassword().equals(request.getNewPassword())) {
                return ResponseEntity.badRequest().body("Current password cannot be the same as the new password");
            }

            
            String response = userService.changePassword(username, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
