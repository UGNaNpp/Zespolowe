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
    private static final String USERS_FILE = "users.json";  // Ścieżka do pliku JSON
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
                usersFile.createNewFile();  // Tworzymy pusty plik, jeśli nie istnieje
            } catch (IOException e) {
                throw new RuntimeException("Error creating users file", e);
            }
            users = new ArrayList<>();  // Używamy mutowalnej listy
        } else {
            users = readUsersFromFile();  // Wczytujemy użytkowników z pliku, jeśli plik już istnieje
        }
    }

    private List<User> readUsersFromFile() {
        try {
            return objectMapper.readValue(new File(USERS_FILE), objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));
        } catch (IOException e) {
            return new ArrayList<>();  // Jeśli wystąpi błąd, zwróć pustą listę
        }
    }

    private void writeUsersToFile(List<User> users) {
        try {
            objectMapper.writeValue(new File(USERS_FILE), users);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file", e);
        }
    }

    public String registerUser(String username, String email, String password) {
        if (users.stream().anyMatch(user -> user.getEmail().equals(email))) {
            throw new IllegalArgumentException("Email already registered");
        } else if (users.stream().anyMatch(user -> user.getUsername().equals(username))) {
            throw new IllegalArgumentException("Username already registered");
        }

        String passwordHash = User.hashPassword(password);
        User newUser = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordHash)
                .build();

        users.add(newUser);  // Teraz lista jest mutowalna
        writeUsersToFile(users);

        return newUser.toJson();
    }

    public String validateUser(String identifier, String password) {
        boolean isEmail = identifier.contains("@");

        return users.stream()
                .filter(user -> isEmail ? user.getEmail().equals(identifier) : user.getUsername().equals(identifier))
                .findFirst()
                .map(user -> {
                    if (user.verifyPassword(password)) {
                        return user.toJson();
                    } else {
                        throw new IllegalArgumentException("Invalid password");
                    }
                })
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    public void deleteUser(Long userId) {
        Optional<User> userOptional = users.stream().filter(user -> user.getUserId() == userId).findFirst();

        if (userOptional.isPresent()) {
            users = users.stream()
                    .filter(user -> user.getUserId() != userId)
                    .collect(Collectors.toList());
            writeUsersToFile(users);
        } else {
            throw new NoSuchElementException("User not found");
        }
    }
}
