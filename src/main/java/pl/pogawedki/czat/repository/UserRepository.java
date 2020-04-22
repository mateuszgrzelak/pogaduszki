package pl.pogawedki.czat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.pogawedki.czat.model.User;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
}
