package pl.com.pogaduszki.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.com.pogaduszki.chat.model.User;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
}
