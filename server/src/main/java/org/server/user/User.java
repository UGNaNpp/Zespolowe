package org.server.user;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.Serializable;
import java.util.Date;

import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class User implements Serializable {
    private final int githubId;
    private final String avatarUrl;
    private final String login; // Jest niezmienny dla githuba
    private final String name; // Można zmieniać w githubie
    private final Date addedDate; // data przydzielenia uprawnień do systemu
    private static final ObjectMapper mapper = new ObjectMapper();

    public User (int githubId, String avatarUrl,String githubLogin, String name) {
        this.githubId = githubId;
        this.avatarUrl = avatarUrl;
        this.login = githubLogin;
        this.name = name;
        this.addedDate = new Date();
    }

    public static User fetchFromGithubApi(String githubLogin)
            throws JsonProcessingException, IllegalArgumentException {

        String url = "https://api.github.com/users/" + githubLogin;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github.v3+json");

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("GitHub user not found: " + githubLogin);
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());

        int githubId = root.get("id").asInt();
        String avatarUrl = root.get("avatar_url").asText();
        String login = root.get("login").asText();
        String name = root.hasNonNull("name") ? root.get("name").asText() : null;

        return new User(githubId, avatarUrl, login, name);
    }

    public int getGithubId() {
        return githubId;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public String toJson() {
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
