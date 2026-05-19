package com.bank.mcp.client.controller;
import com.bank.mcp.client.service.GuardedIntentRouter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/banking")
@CrossOrigin(origins = "*")
public class ChatController {
    private final GuardedIntentRouter router;

    public ChatController(GuardedIntentRouter router) { 
        this.router = router; 
    }

    public record ChatRequest(String message) {}
    public record ChatResponse(String status, String reply) {}

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> handleBankingChat(
            @RequestBody ChatRequest request,
            @RequestHeader(value = "Authorization", defaultValue = "Bearer MOCK_JWT") String authHeader) {
        String jwtToken = authHeader.replace("Bearer ", "").trim();
        String hardcodedSessionId = "324223423";
        
        // Routes through the Two-Pass Guarded Intent Router first
        String aiReply = router.handleIncomingQuery(request.message(), jwtToken,hardcodedSessionId);
        
        return ResponseEntity.ok(new ChatResponse("SUCCESS", aiReply));
    }
}