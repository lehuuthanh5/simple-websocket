package com.example.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@RestController
public class MessageController {

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/chat")
    @SendToUser("/queue/messages")
    public OutputMessage send(Message message, SimpMessageHeaderAccessor headerAccessor) {
        String time = getCurrentTime();
        String sessionId = Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("sessionId").toString();
        return new OutputMessage(message.getFrom(), message.getText() + " - " + sessionId, time);
    }

    @GetMapping("/sendTo")
    public void sendTo(@RequestParam String text, @RequestParam String id) {
        String time = getCurrentTime();
        this.template.convertAndSendToUser(id, "/queue/messages", new OutputMessage("ROBOT", text, time));
    }

    @GetMapping("/sendToAll")
    public void sendTo(@RequestParam String text) {
        String time = getCurrentTime();
        WSStatus.SESSIONS.forEach(id -> template.convertAndSendToUser(id, "/queue/messages", new OutputMessage("ROBOT", text, time)));
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm").format(new Date());
    }
}
