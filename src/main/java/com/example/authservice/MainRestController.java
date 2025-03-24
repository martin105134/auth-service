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

    @Autowired
    KafkaProducer kafkaProducer;

    @PostMapping("/signup")
    public ResponseEntity<?> hello(@RequestBody AuthData authData) throws JsonProcessingException {
        authDataRepo.save(authData);
        kafkaProducer.sendMessage(authData.getUsername(),"REGISTER");
        return ResponseEntity.ok("User signed up successfully");
    }

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthData authData) throws JsonProcessingException {
        AuthData existingAuthData = authDataRepo.findById(authData.getUsername()).orElse(null);
        if (existingAuthData != null && existingAuthData.getPassword().equals(authData.getPassword())) {
            kafkaProducer.sendMessage(authData.getUsername(),"LOGIN");
            return ResponseEntity.ok().
                    header("Authorization", tokenService.generateToken(existingAuthData.getUsername()).getTokenid().toString()).
                    body("User logged in successfully");
        } else {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String headerToken) throws JsonProcessingException {
        String[] tokenArray = headerToken.split(" ");
        String token = tokenArray[1];
        String username = tokenRepository.findById(Integer.valueOf(token)).get().getUsername();
        if (tokenService.validateToken(token)) {
            kafkaProducer.sendMessage(username,"VALIDATE");
            return ResponseEntity.ok("valid");
        } else {
            return ResponseEntity.ok("invalid");
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
        kafkaProducer.sendMessage(username,"LOGOUT");
        return ResponseEntity.ok("logged out successfully");
    }
}
