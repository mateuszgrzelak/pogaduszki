package pl.pogawedki.czat.configuration;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.pogawedki.czat.service.MongoUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final MongoUserDetailsService userDetailsService;

    public WebSecurityConfig(MongoUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
        //permit to send static files to client
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
        .antMatchers("/register").permitAll()
        .anyRequest().authenticated()
        .and()
        .formLogin()
        .loginPage("/login").permitAll()
        .and()
        .logout()
        .deleteCookies("username")
        .logoutUrl("/logout")
        .permitAll();
    }

//  @Override
//  public void configure(WebSecurity web) throws Exception {
//    web.ignoring()
//        .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/img/**", "/icon/**");
//  }

//  @Bean
//  @Override
//  protected UserDetailsService userDetailsService() {
//    UserDetails user = User.withDefaultPasswordEncoder()
//        .username("user")
//        .password("user1")
//        .roles("USER")
//        .build();
//    return new InMemoryUserDetailsManager(user);
//  }

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A,10);
    }

}
