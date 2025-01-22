package com.mars.user;

import com.mars.common.entity.User;
import com.mars.common.interfaces.UserService;
import com.mars.user.dto.ChangePasswordRequest;
import com.mars.user.dto.UpdateProfileRequest;
import com.mars.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, 
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public String registerUser(User user, String role) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return "Username is already taken";
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email is already registered";
        }

        if (role == null || role.isEmpty()) {
            role = "customer"; 
        }

        user.setRole(role); 
        user.setPassword(passwordEncoder.encode(user.getPassword())); 
        userRepository.save(user);
        return "User registered successfully";
    }

@Override
public String updateRole(String username, String newRole) {
    User user = findByUsername(username);
    
    
    if (!newRole.equals("customer") && !newRole.equals("clerk")) {
        throw new IllegalArgumentException("Invalid role. Must be either 'customer' or 'clerk'");
    }
    
    user.setRole(newRole);
    userRepository.save(user);
    
    
    return jwtUtil.generateToken(username, newRole);
}

    @Override
    public User authenticateUser(String username, String password) {
        User user = findByUsername(username);
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        throw new RuntimeException("Invalid password");
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public String updateProfile(String username, UpdateProfileRequest request) {
        
        User user = findByUsername(username);
        
        
        if (!username.equals(request.getUsername()) && 
            userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        
        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already taken");
        }
        
        
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        
        
        userRepository.save(user);
        
        
        return jwtUtil.generateToken(request.getUsername(), user.getRole()); 
    }

    @Override
    public String changePassword(String username, ChangePasswordRequest request) {
        
        User user = findByUsername(username);
        
        
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        
        
        userRepository.save(user);
        return "Password changed successfully";
    }
}
