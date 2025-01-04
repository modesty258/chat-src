package com.auggie.student_server.service;

import com.auggie.student_server.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 生成用户Token
     * @param userId 用户ID
     * @param userType 用户类型 (student/teacher/admin)
     * @return JWT Token
     */
    public String generateUserToken(String userId, String userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("userType", userType);
        claims.put("createTime", System.currentTimeMillis());
        return jwtUtil.generateToken(claims);
    }

    /**
     * 验证Token
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    /**
     * 从Token中获取用户ID
     * @param token JWT Token
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        return jwtUtil.getUserIdFromToken(token);
    }
}