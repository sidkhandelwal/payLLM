package com.bank.mcp.client.config;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class McpClientConfig {

    // @Value("${spring.ai.mcp.client.streamable-http.connections.governance-hub.url}")
    // private String mcpServerUrl;

    // @Bean
    // public McpSyncClient mcpClient() {
    //     WebFluxSseClientTransport transport = WebFluxSseClientTransport.builder(
    //         WebClient.builder().baseUrl(mcpServerUrl)
    //     ).build();

    //     McpSyncClient syncClient = McpClient.sync(transport).build();
    //     syncClient.initialize();
    //     return syncClient;
    // }
}