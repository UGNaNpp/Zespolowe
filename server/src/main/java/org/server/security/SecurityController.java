package org.server.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Arrays;

@RestController
@RequestMapping("/api/security")
public class SecurityController {

    @PostMapping("/verify")
    public ResponseEntity<String> verifyGithubToken(HttpServletRequest request) throws URISyntaxException, IOException, InterruptedException {
        String token = request.getHeader("authorization");
        System.out.println(token);

        if (token != null) {
            HttpClient client = HttpClient.newHttpClient();

            java.net.http.HttpRequest verifyGhTokenReq = java.net.http.HttpRequest.newBuilder()
                    .uri(new URI("https://api.github.com/user"))
                    .header("Authorization", token)
                    .build();

            HttpResponse<String> response = client.send(verifyGhTokenReq, HttpResponse.BodyHandlers.ofString());

            System.out.println("Response status: " + response.statusCode());
            System.out.println("Response body: " + response.body());
        } else  {
            return new ResponseEntity<>("No authorization header", HttpStatus.UNAUTHORIZED);
        }


        return new ResponseEntity<String>(HttpStatusCode.valueOf(200));
    }
}
