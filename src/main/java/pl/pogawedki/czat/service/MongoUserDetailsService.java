package pl.pogawedki.czat.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import pl.pogawedki.czat.model.User;
import pl.pogawedki.czat.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

@Component
public class MongoUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    public MongoUserDetailsService(UserRepository repository) {
      this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUsername(username);

        if(user==null){
                throw new UsernameNotFoundException("User not found");
            }

            List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("user"));

            return new org.springframework.security.core.userdetails.User(user.getUsername(),
                  user.getPassword(),authorities);

      }
}
