package pl.pogawedki.czat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;
import pl.pogawedki.czat.model.ClassWithOnlyOneFieldString;
import pl.pogawedki.czat.model.MessagesDB;
import pl.pogawedki.czat.model.User;
import pl.pogawedki.czat.model.UserDTO;
import pl.pogawedki.czat.repository.MessageRepository;
import pl.pogawedki.czat.repository.UserRepository;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Controller
public class UserHandlingController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private BCryptPasswordEncoder encoder;

    public UserHandlingController(SimpMessagingTemplate messagingTemplate, UserRepository repository, MessageRepository messageRepository) {
        this.messagingTemplate = messagingTemplate;
        this.userRepository = repository;
        this.messageRepository = messageRepository;
        encoder = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A, 10);
    }

    @PostMapping(value = "/invite")
    public @ResponseBody
    ResponseEntity<?> invite(@Valid ClassWithOnlyOneFieldString friendsLogin,
                             BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return ResponseEntity.ok("User doesn't exists.");
        }
        User user = userRepository.findByUsername(friendsLogin.getValue());
        if (user == null) {
            return ResponseEntity.ok("User doesn't exists.");
        }
        boolean resending = user.getInvitations().contains(principal.getName());
        boolean alreadySent = userRepository.findByUsername(principal.getName()).
                getInvitations().contains(friendsLogin.getValue());
        if (resending || alreadySent) {
            return ResponseEntity.ok("The invitation has already been sent.");
        }
        boolean alreadyFriends = user.getFriends().containsKey(principal.getName());
        if (alreadyFriends) {
            return ResponseEntity.ok("You are already friends");
        }
        if (principal.getName().equals(friendsLogin.getValue())) {
            return ResponseEntity.ok("You can't invite yourself");
        }

        user.getInvitations().add(principal.getName());

        userRepository.save(user);

        messagingTemplate.convertAndSend("/topic/" + friendsLogin.getValue() + "?inv"
                , new ClassWithOnlyOneFieldString(principal.getName()));

        return ResponseEntity.ok("The invitation has been sent");
    }

    @PostMapping("/invite/accept")
    public ResponseEntity<?> acceptInvitation(@Valid ClassWithOnlyOneFieldString friendsLogin,
                                              BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User invitedUser = userRepository.findByUsername(principal.getName());
        if (!invitedUser.getInvitations().contains(friendsLogin.getValue())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        invitedUser.getFriends().put(friendsLogin.getValue(), (principal.getName()+friendsLogin.getValue()));
        invitedUser.getInvitations().remove(friendsLogin.getValue());
        userRepository.save(invitedUser);

        User userWhoInvites = userRepository.findByUsername(friendsLogin.getValue());

        if (userWhoInvites == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        userWhoInvites.getFriends().put(invitedUser.getUsername(), (principal.getName()+friendsLogin.getValue()));
        userRepository.save(userWhoInvites);
        messagingTemplate.convertAndSend("/topic/" + invitedUser.getUsername() + "?conv"
                , new User(userWhoInvites.getUsername(), null, userWhoInvites.getDescription(), null, null));
        messagingTemplate.convertAndSend("/topic/" + userWhoInvites.getUsername() + "?conv"
                , new User(invitedUser.getUsername(), null, invitedUser.getDescription(), null, null));

        messageRepository.insert(new MessagesDB((principal.getName()+friendsLogin.getValue()), new LinkedList<>()));


        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/invite/delete")
    public ResponseEntity<?> deleteInvitation(@Valid ClassWithOnlyOneFieldString friendsLogin,
                                              BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User loggedUser = userRepository.findByUsername(principal.getName());
        if (!loggedUser.getInvitations().contains(friendsLogin.getValue())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        loggedUser.getInvitations().remove(friendsLogin.getValue());
        userRepository.save(loggedUser);

        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @GetMapping("/register")
    public String getRegister(UserDTO userDTO) {
        return "singUp";
    }

    @PostMapping("/register")
    public String registration(@Valid UserDTO userDTO, BindingResult result) {

        User user = userRepository.findByUsername(userDTO.getUsername());

        if (user != null) {
            FieldError error = new FieldError("userDTO",
                    "username",
                    userDTO.getUsername(),
                    false,
                    new String[]{"User already exists."},
                    new Object[]{},
                    "User already exists.");
            result.addError(error);
        }

        if (result.hasErrors()) {
            return "singUp";
        }
        userRepository.insert(new User(userDTO.getUsername(), encoder.encode(userDTO.getPassword()), HtmlUtils.htmlEscape(userDTO.getDescription()), new HashMap<>(), new LinkedList<>()));
        return "login";
    }

    @GetMapping("/")
    public String getCzat(Principal principal, Model model) {
        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("invitations", user.getInvitations());
        List<User> users= new LinkedList<>();
        for(String login: user.getFriends().keySet()){
            users.add(userRepository.findByUsername(login));
        }
        model.addAttribute("friends", user.getFriends().keySet());
        model.addAttribute("users", users);
        return "czat";
    }

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

}
