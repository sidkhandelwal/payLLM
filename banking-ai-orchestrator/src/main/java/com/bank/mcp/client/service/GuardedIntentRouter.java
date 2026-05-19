package com.bank.mcp.client.service;
import com.bank.mcp.client.dto.BankingIntent;
import com.bank.mcp.client.dto.IntentExtractionResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class GuardedIntentRouter {
    private final ChatClient routerClient;
    private final TransactionalChatOrchestrator transactionOrchestrator;
    private static final Pattern INJECTION_GUARDRAIL = Pattern.compile("(?i)(drop\\s+table|bypass\\s+entitlement|override\\s+auth)");

    public GuardedIntentRouter(ChatClient.Builder builder, TransactionalChatOrchestrator orchestrator) {
        this.routerClient = builder.defaultSystem("You are a strict Security Ingress Router. Map input to a valid BankingIntent. If out of domain, use UNKNOWN.").build();
        this.transactionOrchestrator = orchestrator;
    }

    public String handleIncomingQuery(String rawUserPrompt, String userToken, String sessionId) {
        if (INJECTION_GUARDRAIL.matcher(rawUserPrompt).find()) {
            return "Transaction Terminated: Safety Policy Violation Detected.";
        }

        IntentExtractionResponse classification = this.routerClient.prompt()
            .user(rawUserPrompt)
            .call()
            .entity(IntentExtractionResponse.class);

        if (classification == null || classification.intent() == BankingIntent.UNKNOWN || classification.intent() == null) {
            return "Intent Denied: I am only authorized to assist with transactional banking functions such as transfers and balance checks.";
        }

        // If safe, pass to the execution orchestrator
        return transactionOrchestrator.processChatIntent(rawUserPrompt, userToken,sessionId);
    }
}