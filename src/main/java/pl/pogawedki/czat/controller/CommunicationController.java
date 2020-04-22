package pl.pogawedki.czat.controller;


import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;
import pl.pogawedki.czat.model.Message;
import pl.pogawedki.czat.model.MessagesDB;
import pl.pogawedki.czat.model.User;
import pl.pogawedki.czat.repository.MessageRepository;
import pl.pogawedki.czat.repository.UserRepository;

import javax.validation.constraints.NotNull;
import java.security.Principal;

@Controller
public class CommunicationController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;


    public CommunicationController(SimpMessagingTemplate messagingTemplate,
                                   UserRepository repository, MessageRepository messageRepository) {
        this.messagingTemplate = messagingTemplate;
        this.userRepository = repository;
        this.messageRepository = messageRepository;
    }

    @MessageMapping("/czat")
    public void message(@NotNull Message message, Principal principal) {
        User destinationUser = userRepository.findByUsername(message.getTo());
        if(message.getContent()==null){
            return;
        }
        if (destinationUser == null || !message.getFrom().equals(principal.getName())) {
            return;
        }
        if (!destinationUser.getFriends().containsKey(principal.getName())) {
            return;
        }
        MessagesDB messDB = messageRepository.findByName(destinationUser.getFriends().get(principal.getName()));
        if(messDB==null){
            return;
        }
        messDB.getMessages().add(message);
        messageRepository.save(messDB);
        messagingTemplate.convertAndSend("/topic/" + message.getTo(), new Message(message.getFrom(),
                null, HtmlUtils.htmlEscape(message.getContent())));
    }

    @GetMapping("/messages")
    @ResponseBody
    public String getMessages(@RequestParam(name = "user")String destinationName, Principal principal){
        User user = userRepository.findByUsername(principal.getName());
        MessagesDB messDB = messageRepository.findByName(user.getFriends().get(destinationName));
        if(messDB==null){
            return null;
        }
        Gson gson = new Gson();
        return gson.toJson(messDB.getMessages());
    }

}
