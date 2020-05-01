package pl.com.pogaduszki.chat.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import pl.com.pogaduszki.chat.model.MessagesDB;

public interface MessageRepository extends MongoRepository<MessagesDB, String> {
    MessagesDB findByName(String username);
}
