package pl.pogawedki.czat.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "messages")
public class MessagesDB {

    @Id
    private String id;
    private String name;
    private List<Message> messages;

    public MessagesDB(String name, List<Message> messages) {
        this.name = name;
        this.messages = messages;
    }

    public MessagesDB(){}
}
