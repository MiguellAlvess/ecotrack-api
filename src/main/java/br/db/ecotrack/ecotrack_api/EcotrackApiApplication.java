package br.db.ecotrack.ecotrack_api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

@SpringBootApplication
public class EcotrackApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(EcotrackApiApplication.class, args);
  }

  @Bean
  @ConditionalOnBean(ChatClient.Builder.class) 
  public ChatClient chatClient(
      ChatClient.Builder chatClientBuilder,
      @Value("classpath:system-prompt.txt") Resource resource) throws IOException {
    String systemPrompt = new String(
        resource.getInputStream().readAllBytes(),
        StandardCharsets.UTF_8);
    return chatClientBuilder
        .defaultSystem(systemPrompt)
        .build();
  }
}
