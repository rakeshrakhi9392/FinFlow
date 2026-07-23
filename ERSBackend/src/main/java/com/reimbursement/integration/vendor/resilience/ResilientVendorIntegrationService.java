package com.reimbursement.integration.vendor.resilience;

import com.reimbursement.integration.vendor.VendorIntegrationException;
import com.reimbursement.integration.vendor.VendorIntegrationService;
import com.reimbursement.integration.vendor.VendorPostingRequest;
import com.reimbursement.integration.vendor.VendorPostingResult;
import com.reimbursement.integration.vendor.config.VendorIntegrationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Decorates a concrete ERP adapter with timeout and retry.
 * Registered as the {@code @Primary} {@link VendorIntegrationService} bean via configuration.
 */
public class ResilientVendorIntegrationService implements VendorIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(ResilientVendorIntegrationService.class);

    private final VendorIntegrationService delegate;
    private final VendorIntegrationProperties properties;
    private final ExecutorService executor = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "vendor-erp-call");
        t.setDaemon(true);
        return t;
    });

    public ResilientVendorIntegrationService(VendorIntegrationService delegate,
                                             VendorIntegrationProperties properties) {
        this.delegate = delegate;
        this.properties = properties;
    }

    @Override
    public String vendorSystem() {
        return delegate.vendorSystem();
    }

    @Override
    public VendorPostingResult postReimbursement(VendorPostingRequest request) {
        int maxAttempts = Math.max(1, properties.getMaxAttempts());
        long timeoutMs = Math.max(100L, properties.getTimeoutMs());
        VendorIntegrationException lastRetryable = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                log.debug("Vendor post attempt {}/{} reimbursementId={} system={}",
                        attempt, maxAttempts, request.reimbursementId(), vendorSystem());
                return invokeWithTimeout(request, timeoutMs);
            } catch (VendorIntegrationException ex) {
                if (!ex.isRetryable() || attempt == maxAttempts) {
                    throw ex;
                }
                lastRetryable = ex;
                backoff(attempt);
            }
        }

        throw lastRetryable != null
                ? lastRetryable
                : new VendorIntegrationException("Vendor post failed after retries", "VENDOR_RETRY_EXHAUSTED", false);
    }

    private VendorPostingResult invokeWithTimeout(VendorPostingRequest request, long timeoutMs) {
        Future<VendorPostingResult> future = executor.submit(() -> delegate.postReimbursement(request));
        try {
            VendorPostingResult result = future.get(timeoutMs, TimeUnit.MILLISECONDS);
            if (result == null) {
                throw new VendorIntegrationException("Vendor returned empty response", "VENDOR_EMPTY", true);
            }
            if (!result.success()) {
                boolean retryable = result.errorCode() == null
                        || !result.errorCode().contains("INVALID");
                throw new VendorIntegrationException(
                        result.errorMessage() != null ? result.errorMessage() : "Vendor rejected posting",
                        result.errorCode() != null ? result.errorCode() : "VENDOR_REJECTED",
                        retryable);
            }
            return result;
        } catch (TimeoutException ex) {
            future.cancel(true);
            throw new VendorIntegrationException(
                    "Vendor ERP call timed out after " + timeoutMs + "ms",
                    "VENDOR_TIMEOUT",
                    true,
                    ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            future.cancel(true);
            throw new VendorIntegrationException("Vendor ERP call interrupted", "VENDOR_INTERRUPTED", true, ex);
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            if (cause instanceof VendorIntegrationException vie) {
                throw vie;
            }
            throw new VendorIntegrationException(
                    "Vendor ERP call failed: " + cause.getMessage(),
                    "VENDOR_EXECUTION",
                    true,
                    cause);
        }
    }

    private void backoff(int attempt) {
        long sleep = properties.getRetryBackoffMs() * attempt;
        if (sleep <= 0) {
            return;
        }
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new VendorIntegrationException("Retry backoff interrupted", "VENDOR_INTERRUPTED", true, e);
        }
    }
}
