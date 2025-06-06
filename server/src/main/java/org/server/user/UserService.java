package org.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Value("${filepath.users}")
    private String USERS_FILE;

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
            System.out.print("Loaded users:\n");
            for (User user : users) {
                System.out.print(user.toJson());
            }
        }
    }

    private List<User> readUsersFromFile() {
        try {
            return objectMapper.readValue(new File(USERS_FILE), objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));
        } catch (IOException e) {
            System.out.println("Error reading users file");
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

    public void registerUser(User newUser) {
        if (users.stream().anyMatch(user -> user.getEmail().equals(newUser.getEmail()))) {
            throw new IllegalArgumentException("Email already registered");
        } else if (users.stream().anyMatch(user -> user.getGithubId() == newUser.getGithubId())) {
            throw new IllegalArgumentException("Username already registered");
        }
        users.add(newUser);
        writeUsersToFile(users);
    }

    public boolean validateUser(int githubId) {
        return users.stream()
                .anyMatch(user -> user.getGithubId() == githubId);
    }

}
