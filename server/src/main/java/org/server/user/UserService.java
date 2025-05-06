package org.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final String USERS_FILE = "users.json";
    private final ObjectMapper objectMapper;
    private List<User> users;

    public UserService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        File usersFile = new File(USERS_FILE);
        if (!usersFile.exists()) {
            try {
                usersFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Error creating users file", e);
            }
            users = new ArrayList<>();
        } else {
            users = readUsersFromFile();
            System.out.print("Loaded users: ");
            for (User user : users) {
                System.out.print(user.toJson());
            }
        }
    }

    private List<User> readUsersFromFile() {
        try {
            return objectMapper.readValue(new File(USERS_FILE), objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void writeUsersToFile(List<User> users) {
        try {
            objectMapper.writeValue(new File(USERS_FILE), users);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file", e);
        }
    }

    public String registerUser(User newUser) {
        if (users.stream().anyMatch(user -> user.getEmail().equals(newUser.getEmail()))) {
            throw new IllegalArgumentException("Email already registered");
        } else if (users.stream().anyMatch(user -> user.getGithubId() == newUser.getGithubId())) {
            throw new IllegalArgumentException("Username already registered");
        }

        users.add(newUser);
        writeUsersToFile(users);

        return newUser.toJson();
    }

    public boolean validateUser(int githubId) {
        return users.stream()
                .filter(user -> user.getGithubId() == githubId)
                .findFirst()
                .isPresent();
    }

//    public void deleteUser(Long userId) {
//        Optional<User> userOptional = users.stream().filter(user -> user.getUserId() == userId).findFirst();
//
//        if (userOptional.isPresent()) {
//            users = users.stream()
//                    .filter(user -> user.getUserId() != userId)
//                    .collect(Collectors.toList());
//            writeUsersToFile(users);
//        } else {
//            throw new NoSuchElementException("User not found");
//        }
//    }

//    public User handleOauth2Login (int githubId) {
//    }

//    public User handleOauth2Login () {
        //TOdo czy my chcemy mieć obiekty usera?
//    }
}
