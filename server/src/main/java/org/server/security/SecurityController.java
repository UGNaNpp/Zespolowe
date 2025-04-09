package org.server.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.server.util.JwtUtil;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.http.dsl.Http;
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

    private final JwtUtil jwtUtil;

    public SecurityController(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    };

    @PostMapping("/verify")
    public ResponseEntity<String> verifyGithubToken(HttpServletRequest request, HttpServletResponse response) throws URISyntaxException, IOException, InterruptedException {
        String token = request.getHeader("authorization");
        System.out.println(token);

        if (token != null) {
            HttpClient client = HttpClient.newHttpClient();

            java.net.http.HttpRequest verifyGhTokenReq = java.net.http.HttpRequest.newBuilder()
                    .uri(new URI("https://api.github.com/user"))
                    .header("Authorization", token)
                    .build();

            HttpResponse<String> ghResponse = client.send(verifyGhTokenReq, HttpResponse.BodyHandlers.ofString());

            if (ghResponse.statusCode() == 200) {
                response.addCookie(jwtUtil.generateJwtHttpCookie(token));
                System.out.println("Udało się autoryzować");
                return new ResponseEntity<>("Authorized", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Incorrect GitHub Token", HttpStatus.UNAUTHORIZED);
            }

        } else  {
            return new ResponseEntity<>("No authorization header", HttpStatus.UNAUTHORIZED);
        }

    }
}
