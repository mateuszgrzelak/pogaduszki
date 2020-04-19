package pl.pogawedki.czat.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketChatConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${spring.rabbitmq.username}")
    private String userName;
    @Value("${spring.rabbitmq.password}")
    private String password;
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

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
       registry.addEndpoint(endpoint).withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableStompBrokerRelay(stompBrokerRelay).setRelayHost(host).setRelayPort(61613)
                .setClientLogin(userName).setClientPasscode(password);
        registry.setApplicationDestinationPrefixes(destinationPrefix);
    }
}
