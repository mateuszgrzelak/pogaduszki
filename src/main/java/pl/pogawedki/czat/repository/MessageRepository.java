package pl.pogawedki.czat.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import pl.pogawedki.czat.model.MessagesDB;

public interface MessageRepository extends MongoRepository<MessagesDB, String> {
    MessagesDB findByName(String username);
}
