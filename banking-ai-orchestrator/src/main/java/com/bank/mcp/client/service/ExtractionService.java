package com.bank.mcp.client.service;
import com.bank.mcp.client.dto.IntentExtractionResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ExtractionService {
    private final ChatClient chatClient;
    public ExtractionService(ChatClient.Builder builder) {
        this.chatClient = builder.defaultSystem("You are a specialized financial intent extraction engine.").build();
    }
    public IntentExtractionResponse parseUserPrompt(String rawUserPrompt) {
        return this.chatClient.prompt().user(rawUserPrompt).call().entity(IntentExtractionResponse.class);
    }
}