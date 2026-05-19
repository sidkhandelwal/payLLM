// SearchPayload.java
package com.bank.mcp.client.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record SearchPayload(
    @JsonPropertyDescription("The name or search term pattern for filtering") String searchTerms
) implements BankingPayload {}