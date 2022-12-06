package org.resistance.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class SocketBrokerConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableStompBrokerRelay("/queue/,/topic/")
      .setRelayHost("127.0.0.1")
      .setRelayPort(61613)
      .setSystemHeartbeatSendInterval(15000)
      .setSystemHeartbeatReceiveInterval(15000);
    config.setApplicationDestinationPrefixes("/app");
    config.setUserDestinationPrefix("/secured/user");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/stompshake").withSockJS()
      .setWebSocketEnabled(true)
      .setSessionCookieNeeded(true)
      .setClientLibraryUrl("http://cdn.sockjs.org/sockjs-0.3.js");
  }
}