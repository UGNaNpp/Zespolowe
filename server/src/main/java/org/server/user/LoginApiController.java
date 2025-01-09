package org.server.user;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/auth")
public class LoginApiController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public LoginApiController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(
            @RequestBody Map<String, String> loginRequest,
            HttpServletResponse response) {
        try {
            String identifier = loginRequest.get("identifier");
            String password = loginRequest.get("password");

            if (identifier == null || password == null) {
                return ResponseEntity.badRequest().body("Missing identifier or password");
            }

            String message = userService.validateUser(identifier, password);
            String jwt = jwtUtil.generateToken(message);

            return ResponseEntity.ok(jwt);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid password");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Map<String, Object> body) {
        String username = body.get("username").toString();
        String password = body.get("password").toString();
        String email = body.get("email").toString();
        try {
            String message = this.userService.registerUser(username, email, password);
            String jwt = jwtUtil.generateToken(message);
            return ResponseEntity.ok(jwt);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @Deprecated
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Usunięcie ciasteczka przez ustawienie 0 czasu życia
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out successfully");
    }
}
