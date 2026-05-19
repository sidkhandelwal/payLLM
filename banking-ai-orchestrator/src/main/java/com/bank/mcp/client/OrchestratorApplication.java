package com.bank.mcp.client;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class OrchestratorApplication {
    public static void main(String[] args) { SpringApplication.run(OrchestratorApplication.class, args); }
    @Bean public RestClient.Builder restClientBuilder() { return RestClient.builder(); }
}