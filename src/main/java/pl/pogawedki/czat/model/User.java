package pl.pogawedki.czat.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String username;
    private String password;
    private Map<String, String> friends;
    private List<String> invitations;

    public User(String username, String password, Map<String,String> friends, List<String> invitations) {
        this.username = username;
        this.password = password;
        this.friends = friends;
        this.invitations = invitations;
    }

    public User(){}


}
