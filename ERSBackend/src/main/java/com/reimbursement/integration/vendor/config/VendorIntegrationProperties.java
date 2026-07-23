package com.reimbursement.integration.vendor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Tunables for vendor ERP calls (timeout, retries, mock behavior).
 */
@ConfigurationProperties(prefix = "ers.vendor")
public class VendorIntegrationProperties {

    /** Active bean: mock-sap | sap | oracle | exchange-rate (future). */
    private String provider = "mock-sap";

    /** Per-attempt timeout in milliseconds. */
    private long timeoutMs = 3000L;

    /** Max attempts including the first call. */
    private int maxAttempts = 3;

    /** Base backoff between retries in milliseconds. */
    private long retryBackoffMs = 250L;

    /** Mock only: artificial latency per call. */
    private long mockLatencyMs = 150L;

    /**
     * Mock only: reimbursement IDs divisible by this value force a transient failure
     * (0 disables). Default 7 ≈ ~14% of claims.
     */
    private int mockFailEveryNthId = 7;

    /**
     * Mock only: reimbursement IDs divisible by this value simulate a timeout
     * (0 disables). Default 13.
     */
    private int mockTimeoutEveryNthId = 13;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public long getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public long getRetryBackoffMs() {
        return retryBackoffMs;
    }

    public void setRetryBackoffMs(long retryBackoffMs) {
        this.retryBackoffMs = retryBackoffMs;
    }

    public long getMockLatencyMs() {
        return mockLatencyMs;
    }

    public void setMockLatencyMs(long mockLatencyMs) {
        this.mockLatencyMs = mockLatencyMs;
    }

    public int getMockFailEveryNthId() {
        return mockFailEveryNthId;
    }

    public void setMockFailEveryNthId(int mockFailEveryNthId) {
        this.mockFailEveryNthId = mockFailEveryNthId;
    }

    public int getMockTimeoutEveryNthId() {
        return mockTimeoutEveryNthId;
    }

    public void setMockTimeoutEveryNthId(int mockTimeoutEveryNthId) {
        this.mockTimeoutEveryNthId = mockTimeoutEveryNthId;
    }
}
