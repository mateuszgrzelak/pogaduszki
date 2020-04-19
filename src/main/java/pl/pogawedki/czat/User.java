package pl.pogawedki.czat;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String username;
    private String password;
    private List<String> friends;
    private List<String> invitations;

    public User(String userName, String password, List<String> friends, List<String> invitations) {
        this.username = userName;
        this.password = password;
        this.friends = friends;
        this.invitations = invitations;
    }

    public User(){}


}
