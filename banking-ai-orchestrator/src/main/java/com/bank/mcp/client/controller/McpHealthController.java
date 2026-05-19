package com.bank.mcp.client.controller;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class McpHealthController {

//     private final McpSyncClient client;

//     public McpHealthController(McpSyncClient client) {
//         this.client = client;
//     }

//     @GetMapping("/mcp/test")
//     public String test() {
//         return "MCP Client Connected";
//     }

//     @GetMapping("/mcp/tools")
// public Object tools() {
//     return client.listTools();
// }
}