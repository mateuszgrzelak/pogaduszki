package pl.pogawedki.czat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.LinkedList;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository repository;
    private BCryptPasswordEncoder encoder;

    public ChatController(SimpMessagingTemplate messagingTemplate, UserRepository repository) {
        this.messagingTemplate = messagingTemplate;
        this.repository = repository;
        encoder = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A, 10);
    }

    @MessageMapping("/czat")
    public void message(@NotNull Message message, Principal principal) {
        User user = repository.findByUsername(message.getTo());
        if (user == null) {
            return;
        }
        if (!user.getFriends().contains(principal.getName())) {
            return;
        }
        messagingTemplate.convertAndSend("/topic/" + message.getTo(), new Message(message.getFrom(),
                null, HtmlUtils.htmlEscape(message.getContent())));
    }

    @PostMapping(value = "/invite")
    public @ResponseBody
    ResponseEntity<?> invite(@Valid ClassWithOnlyOneFieldString friendsLogin,
                             BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return ResponseEntity.ok("User doesn't exists.");
        }
        User user = repository.findByUsername(friendsLogin.getValue());
        if (user == null) {
            return ResponseEntity.ok("User doesn't exists.");
        }
        boolean resending = user.getInvitations().contains(principal.getName());
        boolean alreadySent = repository.findByUsername(principal.getName()).
                getInvitations().contains(friendsLogin.getValue());
        if (resending || alreadySent) {
            return ResponseEntity.ok("The invitation has already been sent.");
        }
        boolean alreadyFriends = user.getFriends().contains(principal.getName());
        if (alreadyFriends) {
            return ResponseEntity.ok("You are already friends");
        }
        if (principal.getName().equals(friendsLogin.getValue())) {
            return ResponseEntity.ok("You can't invite yourself");
        }

        user.getInvitations().add(principal.getName());

        repository.save(user);

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

        User loggedUser = repository.findByUsername(principal.getName());
        if (!loggedUser.getInvitations().contains(friendsLogin.getValue())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        loggedUser.getFriends().add(friendsLogin.getValue());
        loggedUser.getInvitations().remove(friendsLogin.getValue());
        repository.save(loggedUser);

        User userWhoInvites = repository.findByUsername(friendsLogin.getValue());

        if (userWhoInvites == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        userWhoInvites.getFriends().add(loggedUser.getUsername());
        repository.save(userWhoInvites);
        messagingTemplate.convertAndSend("/topic/" + friendsLogin.getValue() + "?conv"
                , new ClassWithOnlyOneFieldString(principal.getName()));

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/invite/delete")
    public ResponseEntity<?> deleteInvitation(@Valid ClassWithOnlyOneFieldString friendsLogin,
                                              BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User loggedUser = repository.findByUsername(principal.getName());
        if (!loggedUser.getInvitations().contains(friendsLogin.getValue())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        loggedUser.getInvitations().remove(friendsLogin.getValue());
        repository.save(loggedUser);

        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @GetMapping("/register")
    public String getRegister(UserDTO userDTO) {
        return "singUp";
    }

    @PostMapping("/register")
    public String registration(@Valid UserDTO userDTO, BindingResult result) {

        User user = repository.findByUsername(userDTO.getUsername());

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
        repository.insert(new User(userDTO.getUsername(), encoder.encode(userDTO.getPassword()),
                new LinkedList<>(), new LinkedList<>()));
        return "login";
    }

    @GetMapping("/")
    public String getCzat(Principal principal, Model model) {
        User user = repository.findByUsername(principal.getName());
        model.addAttribute("invitations", user.getInvitations());
        model.addAttribute("friends", user.getFriends());
        return "czat";
    }

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

}
