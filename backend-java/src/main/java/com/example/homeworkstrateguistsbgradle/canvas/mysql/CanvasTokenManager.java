package com.example.homeworkstrateguistsbgradle.canvas.mysql;

import com.example.homeworkstrateguistsbgradle.canvas.mysql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CanvasTokenManager {

    @Autowired
    private UserRepository userRepository;

    /**
     * Fetches the Canvas token (ivc_token) for a specific user from the DB.
     */
//    public String getCanvasTokenForUser(String googleToken) {
//        return userRepository.findByGoogleToken(googleToken)
//                .map(User::getIvcToken)
//                .orElseThrow(() -> new RuntimeException("User not found or no Canvas token assigned."));
//    }

    /**
     * Updates the Canvas token in the DB (e.g., after a login or refresh).
     */
//    public void saveCanvasToken(String googleToken, String newToken) {
//        User user = userRepository.findByGoogleToken(googleToken)
//                .orElse(new User());
//
//        user.setGoogleToken(googleToken);
//        user.setIvcToken(newToken);
//        userRepository.save(user);
//    }
}