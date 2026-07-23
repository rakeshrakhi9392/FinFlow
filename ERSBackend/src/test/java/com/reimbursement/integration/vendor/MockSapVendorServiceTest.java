package com.reimbursement.integration.vendor;

import com.reimbursement.enums.VendorPaymentStatus;
import com.reimbursement.integration.vendor.config.VendorIntegrationProperties;
import com.reimbursement.integration.vendor.mock.MockSapVendorService;
import com.reimbursement.integration.vendor.resilience.ResilientVendorIntegrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MockSapVendorServiceTest {

    private VendorIntegrationProperties properties;
    private MockSapVendorService mockSap;

    @BeforeEach
    void setUp() {
        properties = new VendorIntegrationProperties();
        properties.setMockLatencyMs(0);
        properties.setMockFailEveryNthId(0);
        properties.setMockTimeoutEveryNthId(0);
        properties.setTimeoutMs(2000);
        properties.setMaxAttempts(3);
        properties.setRetryBackoffMs(10);
        mockSap = new MockSapVendorService(properties);
    }

    @Test
    void postsAccountingDocumentAndReference() {
        VendorPostingResult result = mockSap.postReimbursement(request(42));
        assertTrue(result.success());
        assertNotNull(result.accountingDocument());
        assertNotNull(result.referenceNumber());
        assertEquals(LocalDate.now(), result.postingDate());
        assertTrue(result.vendorId().startsWith("V-"));
        assertEquals(VendorPaymentStatus.SCHEDULED_FOR_PAYMENT, result.paymentStatus());
        assertEquals("MOCK_SAP", mockSap.vendorSystem());
    }

    @Test
    void resilientWrapperRetriesThenSucceeds() {
        properties.setMockFailEveryNthId(0);
        VendorIntegrationService resilient = new ResilientVendorIntegrationService(mockSap, properties);
        VendorPostingResult result = resilient.postReimbursement(request(3));
        assertTrue(result.success());
        assertNotNull(result.accountingDocument());
    }

    @Test
    void resilientWrapperSurfacesExhaustedFailures() {
        properties.setMockFailEveryNthId(1);
        properties.setMaxAttempts(2);
        VendorIntegrationService resilient = new ResilientVendorIntegrationService(
                new MockSapVendorService(properties), properties);
        VendorIntegrationException ex = assertThrows(
                VendorIntegrationException.class,
                () -> resilient.postReimbursement(request(1)));
        assertEquals("SAP_FI_LOCK", ex.getErrorCode());
        assertTrue(ex.isRetryable());
    }

    private VendorPostingRequest request(int id) {
        return new VendorPostingRequest(
                id, 125.50, "USD", "Taxi", "employee1", "ENG", "ENG", "TRAVEL", LocalDate.now());
    }
}
