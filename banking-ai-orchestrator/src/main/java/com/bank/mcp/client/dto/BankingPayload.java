package com.bank.mcp.client.dto;

public sealed interface BankingPayload permits PaymentPayload, BalancePayload, SearchPayload {}
