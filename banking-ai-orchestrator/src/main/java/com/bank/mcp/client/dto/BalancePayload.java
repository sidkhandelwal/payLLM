// BalancePayload.java
package com.bank.mcp.client.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record BalancePayload(
    @JsonPropertyDescription("Target funding account alias string") String accountAlias
) implements BankingPayload {}