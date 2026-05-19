// PaymentPayload.java
package com.bank.mcp.client.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record PaymentPayload(
    @JsonPropertyDescription("Source bank account alias") String sourceAccountAlias,
    @JsonPropertyDescription("Recipient individual name") String beneficiaryAlias,
    @JsonPropertyDescription("Total transactional volume") Double amount,
    @JsonPropertyDescription("The 3-letter currency code") String currency
) implements BankingPayload {}