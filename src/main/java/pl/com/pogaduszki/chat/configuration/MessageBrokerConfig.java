package pl.com.pogaduszki.chat.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class MessageBrokerConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private int port;
    @Value("${endpoint}")
    private String endpoint;
    @Value("${destination.prefix}")
    private String destinationPrefix;
    @Value("${stomp.broker.relay}")
    private String stompBrokerRelay;
    @Value("${rabbitmq.login}")
    private String login;
    @Value("${rabbitmq.password}")
    private String password;
    @Value("${IPAddress}")
    private String ip;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(endpoint)
                //Which addresses can connect through web
                //If you use it only locally delete this line
                .setAllowedOrigins(ip)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableStompBrokerRelay(stompBrokerRelay)
                .setRelayHost(host)
                .setRelayPort(port)
                .setSystemLogin(login)
                .setSystemPasscode(password)
                .setClientLogin(login)
                .setClientPasscode(password);
        registry.setApplicationDestinationPrefixes(destinationPrefix);
    }
}
