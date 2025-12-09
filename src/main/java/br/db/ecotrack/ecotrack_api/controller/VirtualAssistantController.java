package br.db.ecotrack.ecotrack_api.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.db.ecotrack.ecotrack_api.controller.dto.openai.VirtualAssistantRequest;
import br.db.ecotrack.ecotrack_api.controller.dto.openai.VirtualAssistantResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@ConditionalOnBean(ChatClient.class)
@RestController
@RequestMapping("/api/virtual-assistant")
@RequiredArgsConstructor
public class VirtualAssistantController {

  private final ChatClient chatClient;
  private static final String CHAT_MEMORY_SESSION_KEY = "chatMemory";

  @PostMapping("/prompt")
  public VirtualAssistantResponse handleChatPrompt(
      @RequestBody VirtualAssistantRequest request,
      HttpSession session) {
    ChatMemory chatMemory = (ChatMemory) session.getAttribute(CHAT_MEMORY_SESSION_KEY);
    if (chatMemory == null) {
      chatMemory = MessageWindowChatMemory.builder().build();
      session.setAttribute(CHAT_MEMORY_SESSION_KEY, chatMemory);
    }
    String userMessage = request.getMessage();
    String aiResponse = chatClient
        .prompt()
        .user(userMessage)
        .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
        .call()
        .content();
    return new VirtualAssistantResponse(aiResponse);
  }
}
