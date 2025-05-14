package com.twelvenexus.oneplan.subscription.config;

import com.twelvenexus.oneplan.subscription.service.SubscriptionService;
import com.twelvenexus.oneplan.subscription.service.PaymentService;
import com.twelvenexus.oneplan.subscription.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingConfiguration {

    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;
    private final InvoiceService invoiceService;

    @Scheduled(cron = "0 0 0 * * ?") // Daily at midnight
    public void processSubscriptionRenewals() {
        log.info("Processing subscription renewals");
        subscriptionService.processSubscriptionRenewals();
    }

    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void processExpiredTrials() {
        log.info("Processing expired trials");
        subscriptionService.processExpiredTrials();
    }

    @Scheduled(cron = "0 0 3 * * ?") // Daily at 3 AM
    public void processExpiredSubscriptions() {
        log.info("Processing expired subscriptions");
        subscriptionService.processExpiredSubscriptions();
    }

    @Scheduled(cron = "0 0 * * * ?") // Every hour
    public void processStalePayments() {
        log.info("Processing stale payments");
        paymentService.processStalePayments();
    }

    @Scheduled(cron = "0 0 4 * * ?") // Daily at 4 AM
    public void processOverdueInvoices() {
        log.info("Processing overdue invoices");
        invoiceService.processOverdueInvoices();
    }
}
