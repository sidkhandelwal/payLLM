package com.bank.mcp.server.config;

import com.bank.mcp.server.tool.CorporatePaymentTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpToolRegistrationConfig {

    // @Bean
    // public ToolCallbackProvider corporatePaymentToolCallbacks(CorporatePaymentTools tools) {
    //     return MethodToolCallbackProvider.builder()
    //         .toolObjects(tools)
    //         .build();
    // }
}