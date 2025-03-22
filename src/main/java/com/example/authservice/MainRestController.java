package com.example.authservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class MainRestController {

    @Autowired
    AuthDataRepo authDataRepo;

    @Autowired
    TokenService tokenService;

    @Autowired
    TokenRepo tokenRepository;


    @PostMapping("/signup")
    public ResponseEntity<?> hello(@RequestBody AuthData authData) {
        authDataRepo.save(authData);
        return ResponseEntity.ok("User signed up successfully");
    }

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthData authData) {
        AuthData existingAuthData = authDataRepo.findById(authData.getUsername()).orElse(null);
        if (existingAuthData != null && existingAuthData.getPassword().equals(authData.getPassword())) {
            return ResponseEntity.ok().
                    header("Authorization", tokenService.generateToken(existingAuthData.getUsername()).getTokenid().toString()).
                    body("User logged in successfully");
        } else {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String headerToken) {
        String[] tokenArray = headerToken.split(" ");
        String token = tokenArray[1];
        if (tokenService.validateToken(token)) {
            return ResponseEntity.ok("valid");
        } else {
            return ResponseEntity.status(401).body("invalid");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) throws JsonProcessingException {
        String[] tokenArray = token.split(" ");
        Token token1 = tokenService.tokenRepository.findById(Integer.parseInt(tokenArray[1])).orElse(null);
        if(token1 == null)
        {
            return ResponseEntity.badRequest().body("invalid");
        }
        tokenRepository.updateStatusByTokenid("invalid", Integer.valueOf(tokenArray[1]));
        String username = tokenRepository.findById(Integer.valueOf(tokenArray[1])).get().getUsername();
        return ResponseEntity.ok("logged out successfully");
    }
}
