package com.mars.common.interfaces;

import com.mars.common.entity.User;
import com.mars.user.dto.ChangePasswordRequest;
import com.mars.user.dto.UpdateProfileRequest;

public interface UserService {
    
    String registerUser(User user, String role);
    User authenticateUser(String username, String password);
    User findByUsername(String username);
    String updateProfile(String username, UpdateProfileRequest request);
    String changePassword(String username, ChangePasswordRequest request);
    String updateRole(String username, String newRole);
}
