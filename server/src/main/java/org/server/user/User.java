package org.server.user;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

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
