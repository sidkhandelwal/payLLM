package com.bank.mcp.client.dto;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
public record IntentExtractionResponse(
    @JsonPropertyDescription("The strict authorized action category. MUST use UNKNOWN if out of domain.") 
    BankingIntent intent,
    String sourceAccountAlias,
    String beneficiaryAlias,
    Double amount,
    String currency
) {}