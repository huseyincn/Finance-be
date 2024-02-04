package com.huseyincan.financeportfolio.controller.web;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Random;

@Controller
public class WebSocketController {

    Random rand;
    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public Integer sendRandomNumber() {
        return rand.nextInt();
    }
}
