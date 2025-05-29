package org.server.user;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private int githubId;
    private String email;
    private String githubLogin;
    private Date addedDate; // data przydzielenia uprawnień do systemu

    private static final ObjectMapper mapper = new ObjectMapper();

    public User (int githubId, String email, String githubLogin) {
        this.githubId = githubId;
        this.email = email;
        this.githubLogin = githubLogin;
        this.addedDate = new Date();
    }

    public int getGithubId() {
        return githubId;
    }

    public String getEmail() {
        return email;
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
