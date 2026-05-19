package com.bank.mcp.client.service;
import com.bank.mcp.client.dto.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class DynamicExtractionOrchestrator {
    private final ChatClient chatClient;
    public DynamicExtractionOrchestrator(ChatClient.Builder builder) { this.chatClient = builder.build(); }

    public BankingPayload extractIntentSpecificJson(String prompt) {
        BankingIntent intent = this.chatClient.prompt()
            .system("Categorize intention into the enum.")
            .user(prompt).call().entity(BankingIntent.class);
            
        if(intent == null) return null;
        
        return switch (intent) {
            case MAKE_PAYMENT -> this.chatClient.prompt().user(prompt).call().entity(PaymentPayload.class);
            case VIEW_BALANCE -> this.chatClient.prompt().user(prompt).call().entity(BalancePayload.class);
            case SEARCH_BENEFICIARIES -> this.chatClient.prompt().user(prompt).call().entity(SearchPayload.class);
            case UNKNOWN -> throw new IllegalArgumentException("Unknown domain intent.");
        };
    }
}