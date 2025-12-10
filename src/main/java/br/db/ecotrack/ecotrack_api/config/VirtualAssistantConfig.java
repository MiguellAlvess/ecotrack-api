package br.db.ecotrack.ecotrack_api.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class VirtualAssistantConfig {

  @Bean
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
