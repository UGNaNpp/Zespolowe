package org.server.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@Deprecated
@RequestMapping("/public/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/validate")
    public ResponseEntity<String> validateUser(@RequestBody Map<String, Object> body) {
        try {
            String identifier = body.get("identifier").toString();
            String password = body.get("password").toString();

            String message = userService.validateUser(identifier, password);
            return ResponseEntity.ok(message);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
