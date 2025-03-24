package com.example.authservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducer {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String username, String description) throws JsonProcessingException {
        log.debug("Entering KafkaProducer.sendMessage");
        Analytic analytical = new Analytic();
        analytical.setPrincipal(username);
        analytical.setDescription(description);
        analytical.setType("auth");
        ObjectMapper mapper = new ObjectMapper();
        log.debug(mapper.writeValueAsString(analytical));
        kafkaTemplate.send("auth-event",mapper.writeValueAsString(analytical));
        log.debug("Exiting KafkaProducer.sendMessage");
    }
}
