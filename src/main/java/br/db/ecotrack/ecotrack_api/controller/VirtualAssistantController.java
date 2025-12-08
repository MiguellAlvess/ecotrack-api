package br.db.ecotrack.ecotrack_api.controller;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.db.ecotrack.ecotrack_api.controller.dto.openai.VirtualAssistantRequest;
import br.db.ecotrack.ecotrack_api.controller.dto.openai.VirtualAssistantResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/virtual-assistant")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class VirtualAssistantController {

  private final ChatClient chatClient;
  private static final String CHAT_MEMORY_SESSION_KEY = "chatMemory";
  private static final String JWT_CONTEXT_KEY = "jwtToken";

  @PostMapping("/prompt")
  public VirtualAssistantResponse handleChatPrompt(@RequestBody VirtualAssistantRequest request, HttpSession session,
      Authentication authentication) {
    ChatMemory chatMemory = (ChatMemory) session.getAttribute(CHAT_MEMORY_SESSION_KEY);
    if (chatMemory == null) {
      chatMemory = MessageWindowChatMemory.builder().build();
      session.setAttribute(CHAT_MEMORY_SESSION_KEY, chatMemory);
    }
    String jwtTokenString = null;
    if (authentication instanceof JwtAuthenticationToken jwtAuth) {
      jwtTokenString = jwtAuth.getToken().getTokenValue();
    }
    String userMessage = String.format(request.getMessage());
    final Map<String, Object> contextMap = jwtTokenString != null ? Map.of(JWT_CONTEXT_KEY, jwtTokenString) : Map.of();
    String aiResponse = chatClient.prompt()
        .user(userMessage)
        .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
        .toolContext(contextMap)
        .call()
        .content();

    return new VirtualAssistantResponse(aiResponse);
  }
}
