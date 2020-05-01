package pl.com.pogaduszki.chat.controller;

import com.google.gson.Gson;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;
import pl.com.pogaduszki.chat.model.Message;
import pl.com.pogaduszki.chat.model.MessagesDB;
import pl.com.pogaduszki.chat.model.User;
import pl.com.pogaduszki.chat.repository.MessageRepository;
import pl.com.pogaduszki.chat.repository.UserRepository;

import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class CommunicationController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    DateTimeFormatter formatter;


    public CommunicationController(SimpMessagingTemplate messagingTemplate,
                                   UserRepository repository, MessageRepository messageRepository) {
        this.messagingTemplate = messagingTemplate;
        this.userRepository = repository;
        this.messageRepository = messageRepository;
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd | HH:mm");
    }

    @MessageMapping("/czat")
    public void message(@NotNull Message message, Principal principal) {
        User destinationUser = userRepository.findByUsername(message.getTo());
        message.setDate(LocalDateTime.now().format(formatter));

        if (message.getContent() == null) {
            return;
        }
        if (destinationUser == null || !message.getFrom().equals(principal.getName())) {
            return;
        }
        if (!destinationUser.getFriends().containsKey(principal.getName())) {
            return;
        }
        MessagesDB messDB = messageRepository.findByName(destinationUser.getFriends().get(principal.getName()));
        if (messDB == null) {
            return;
        }
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        messDB.getMessages().add(message);
        messageRepository.save(messDB);
        messagingTemplate.convertAndSend("/topic/" + message.getTo(), new Message(message.getFrom(),
                null, message.getContent(), message.getDate()));
        messagingTemplate.convertAndSend("/topic/" + message.getFrom(), new Message(message.getFrom(),
                null, message.getContent(), message.getDate()));
    }

    @GetMapping("/messages")
    @ResponseBody
    public String getMessages(@RequestParam(name = "user") String destinationName, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        MessagesDB messDB = messageRepository.findByName(user.getFriends().get(destinationName));
        if (messDB == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.toJson(messDB.getMessages());
    }

}
