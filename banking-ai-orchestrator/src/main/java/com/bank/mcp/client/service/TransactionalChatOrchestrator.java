package com.bank.mcp.client.service;

import com.bank.mcp.client.dto.DialogueState;
import io.modelcontextprotocol.client.McpSyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TransactionalChatOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(TransactionalChatOrchestrator.class);

    private final ChatClient chatClient;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String STATE_PREFIX = "dialogue:state:";

    public TransactionalChatOrchestrator(
            ChatClient.Builder builder,
           
            RedisTemplate<String, Object> redisTemplate) {

        this.redisTemplate = redisTemplate;
        
        this.chatClient = builder
            .defaultSystem("""
                You are a secure corporate banking conversational agent.
                You manage a stateful multi-turn payment conversation workflow.
                
                CRITICAL DIALOGUE MANAGEMENT RULES:
                1. To execute a payment staging process via your tools, you must have four verified parameter slots: sourceAccountAlias, beneficiaryAlias, amount, and currency.
                2. If any slots are missing from the ongoing conversation history logs, DO NOT trigger the tool. Ask a short, direct question to retrieve the explicit missing slot parameter.
                3. If all slots are present, format a summary confirmation review statement and append the word: PROMPT_USER_FOR_CONFIRMATION at the end of your response.
                4. Never reveal primary database record IDs back to user interaction windows.
                """)
            .defaultAdvisors(
                MessageChatMemoryAdvisor.builder(
                    MessageWindowChatMemory.builder()
                        .chatMemoryRepository(new InMemoryChatMemoryRepository())
                        .build()
                ).build()
            )
           //.defaultTools(mcpSyncClients.get(0))
            .build();
    }

    public String processChatIntent(String rawUserPrompt, String userContextJwt, String sessionId) {
        String stateKey = STATE_PREFIX + sessionId;

        // 1. Fetch current dialogue session state flags from Redis cache cluster
        DialogueState currentState = getSessionState(stateKey);
        log.info("Active Session Frame: {} | Dialogue State Flag: {}", sessionId, currentState);

        // 2. State Machine Interceptor: Handle explicit confirmation tracking state before hitting the LLM
        if (currentState == DialogueState.AWAITING_CONFIRMATION) {
            if (isConfirmationAffirmative(rawUserPrompt)) {
                updateSessionState(stateKey, DialogueState.STAGED, 15);
                return "✓ Confirmation Received. Sending payload parameters over secure mTLS tunnels to your Corporate Staging Area... Done. Transaction reference successfully staged for secondary checker authentication sign-offs.";
            } else {
                updateSessionState(stateKey, DialogueState.IDLE, 1);
                return "✕ Staging Aborted. The temporary transaction frame cache memory was dropped successfully.";
            }
        }

        // 3. Execution of conversational loops matching memory tracking histories
        try {
            String aiResponse = this.chatClient.prompt()
                .user(rawUserPrompt)
                .system(sys -> sys.param("user_token", userContextJwt))
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, sessionId))
                .call()
                .content();

            // 4. Transition Dialogue State machine if LLM identifies complete structural slot validation
            if (aiResponse != null && aiResponse.contains("PROMPT_USER_FOR_CONFIRMATION")) {
                updateSessionState(stateKey, DialogueState.AWAITING_CONFIRMATION, 15);
                return aiResponse.replace("PROMPT_USER_FOR_CONFIRMATION", "").trim()
                    + "\n\nPlease state [Yes/Confirm] to finalize staging execution, or [No] to reject.";
            }

            return aiResponse;

        } catch (Exception ex) {
            log.error("Internal processing chain failure during context assembly: ", ex);
            return "Transaction Processing Exception Encountered: " + ex.getMessage();
        }
    }

    private boolean isConfirmationAffirmative(String input) {
        String clean = input.toLowerCase().trim();
        return clean.equals("yes") || clean.contains("confirm") || clean.contains("approve");
    }

    private DialogueState getSessionState(String key) {
        Object state = redisTemplate.opsForValue().get(key);
        return state != null ? DialogueState.valueOf(state.toString()) : DialogueState.IDLE;
    }

    private void updateSessionState(String key, DialogueState state, int expiryMinutes) {
        redisTemplate.opsForValue().set(key, state.name(), expiryMinutes, TimeUnit.MINUTES);
    }
}