package br.db.ecotrack.ecotrack_api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import io.modelcontextprotocol.client.McpSyncClient;

@SpringBootApplication
public class EcotrackApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(EcotrackApiApplication.class, args);
  }

  @Bean
  public ChatClient chatClient(
      ChatClient.Builder chatClientBuilder,
      List<McpSyncClient> mcpSyncClients,
      @Value("classpath:system-prompt.txt") Resource resource) throws IOException {
    String systemPrompt = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    return chatClientBuilder
        .defaultSystem(systemPrompt)
        .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClients))
        .defaultAdvisors(MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
        .build();
  }

}
