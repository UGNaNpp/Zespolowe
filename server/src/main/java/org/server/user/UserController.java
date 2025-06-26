package org.server.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/users/")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PostMapping("/add-user-by-login")
    public ResponseEntity<?> addUser(@RequestBody String githubUsername) {
        try {
            User newUser = User.fetchFromGithubApi(githubUsername);
            userService.registerUser(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        }
        catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            } else return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{ghLogin}")
    public ResponseEntity<User> deleteUser(@PathVariable String ghLogin) {
        userService.deleteUser(ghLogin);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
