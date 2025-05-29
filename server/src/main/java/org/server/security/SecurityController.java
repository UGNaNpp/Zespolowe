package org.server.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.integration.http.dsl.Http;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Arrays;

@RestController
@RequestMapping("/api/security")
public class SecurityController {
    @Value("${front.mainPageUrl}")
    private String frontMainPage;

    private final JwtUtil jwtUtil;

    public SecurityController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/verify")
    public ResponseEntity<String> verifyGithubToken(HttpServletRequest request, HttpServletResponse response) throws URISyntaxException, IOException, InterruptedException {
        String token = request.getHeader("authorization");

        if (token != null) {
            HttpClient client = HttpClient.newHttpClient();

            java.net.http.HttpRequest verifyGhTokenReq = java.net.http.HttpRequest.newBuilder()
                    .uri(new URI("https://api.github.com/user"))
                    .header("Authorization", token)
                    .build();

            HttpResponse<String> ghResponse = client.send(verifyGhTokenReq, HttpResponse.BodyHandlers.ofString());
            System.out.println(ghResponse);
            System.out.println(token);

            if (ghResponse.statusCode() == 200) {

                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(ghResponse.body());

                int id = node.get("id").asInt();
                System.out.println(id);

                response.addCookie(jwtUtil.generateJwtHttpCookie(token));
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, frontMainPage)
                        .build();
            } else {
                return new ResponseEntity<>("Incorrect GitHub Token", HttpStatus.UNAUTHORIZED);
            }

        } else  {
            return new ResponseEntity<>("No authorization header", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/test-endpoint")
    public String testEndpoint(){
        return "It works!";
    }
}
