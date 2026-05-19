package com.bank.mcp.server.tool;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CorporatePaymentTools {
    private final JaroWinklerDistance jaroWinkler = new JaroWinklerDistance();
    private static final double MIN_MATCH = 0.82;

    public record AccountResponse(String accountId, String accountAlias, Double availableBalance, String currency) {}
    public record BeneficiaryResponse(String beneficiaryId, String legalName, String status) {}
    public record PaymentStatusResponse(String trackingId, String status, String message) {}
    public record TransactionHistoryResponse(String referenceId, String date, String description, Double amount, String type) {}

    private final List<AccountResponse> mockAccounts = List.of(
        new AccountResponse("ACC-440912398", "mybusiness account", 25000.00, "USD"),
        new AccountResponse("ACC-110293847", "pune payroll operating", 1250000.00, "INR")
    );
    private final List<BeneficiaryResponse> mockBeneficiaries = List.of(
        new BeneficiaryResponse("BEN-99812", "Ramesh Kumar Sharma", "ACTIVE"),
        new BeneficiaryResponse("BEN-11203", "Suresh Patel", "ACTIVE")
    );
    private final List<TransactionHistoryResponse> mockTransactions = List.of(
        new TransactionHistoryResponse("TXN-77102", "2026-05-10", "Vendor Settlement", 4500.00, "DEBIT")
    );

    @McpTool(name = "view_entitled_accounts", description = "Retrieves user accounts and balances.")
    public List<AccountResponse> viewEntitledAccounts(@McpToolParam(description = "User token", required = true) String userToken) {
        return mockAccounts;
    }

    @McpTool(name = "search_beneficiaries", description = "Fuzzy matches corporate beneficiaries.")
    public List<BeneficiaryResponse> searchBeneficiaries(
            @McpToolParam(description = "User token", required = true) String userToken,
            @McpToolParam(description = "Target alias", required = true) String searchAlias) {
        return mockBeneficiaries.stream()
            .filter(ben -> jaroWinkler.apply(searchAlias.toLowerCase().trim(), ben.legalName().toLowerCase().trim()) >= MIN_MATCH)
            .collect(Collectors.toList());
    }

    @McpTool(name = "initiate_payment_staging", description = "Stages an asset transfer using exact structural IDs.")
    public PaymentStatusResponse initiatePaymentStaging(
            @McpToolParam(description = "User token", required = true) String userToken,
            @McpToolParam(description = "Source account ID", required = true) String sourceAccountId,
            @McpToolParam(description = "Recipient ID", required = true) String beneficiaryId,
            @McpToolParam(description = "Transaction amount", required = true) Double amount,
            @McpToolParam(description = "Currency", required = true) String currency) {
        
        AccountResponse account = mockAccounts.stream().filter(acc -> acc.accountId().equals(sourceAccountId)).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid source account ID."));

        if (account.availableBalance() < amount) {
            return new PaymentStatusResponse("ERR-LIQ", "REJECTED", "Insufficient funding availability.");
        }
        return new PaymentStatusResponse("TX-STG-" + UUID.randomUUID().toString().substring(0,8).toUpperCase(), 
            "PENDING_MAKER_CHECKER_APPROVAL", "Staged successfully for workflow authorizers.");
    }

    @McpTool(name = "view_recent_transactions", description = "Fetches historic transaction log records.")
    public List<TransactionHistoryResponse> viewRecentTransactions(@McpToolParam(description = "User token", required = true) String userToken) {
        return mockTransactions;
    }
}