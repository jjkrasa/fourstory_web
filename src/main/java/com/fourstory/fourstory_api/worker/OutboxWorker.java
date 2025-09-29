package com.fourstory.fourstory_api.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourstory.fourstory_api.event.EmailChangeRequestedEvent;
import com.fourstory.fourstory_api.event.PasswordChangedEvent;
import com.fourstory.fourstory_api.event.PasswordResetRequestedEvent;
import com.fourstory.fourstory_api.event.VerificationResentEvent;
import com.fourstory.fourstory_api.model.tglobal.Outbox;
import com.fourstory.fourstory_api.repository.tglobal.OutboxRepository;
import com.fourstory.fourstory_api.service.EmailDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxWorker {

    private final OutboxRepository outboxRepository;

    private final ObjectMapper objectMapper;

    private final EmailDispatcher emailDispatcher;

    @Value("${app.outbox.batch-size}")
    private int batchSize;

    @Value("${app.outbox.max-attempts}")
    private int maxAttempts = 10;

    @Scheduled(fixedDelayString = "${app.outbox.poll-delay-ms}")
    @Transactional(transactionManager = "tglobalTransactionManager")
    public void drain() {
        List<Outbox> batch = outboxRepository.fetchBatch(batchSize);
        if (batch.isEmpty()) {
            return;
        }

        for (Outbox outbox : batch) {
            try {
                switch (outbox.getEventType()) {
                    case EMAIL_VERIFICATION_REQUEST -> emailDispatcher.sendVerifyEmail(objectMapper.readValue(outbox.getPayload(), VerificationResentEvent.class));
                    case PASSWORD_RESET_REQUEST -> emailDispatcher.sendForgotPassword(objectMapper.readValue(outbox.getPayload(), PasswordResetRequestedEvent.class));
                    case EMAIL_CHANGE_REQUEST -> emailDispatcher.sendChangeEmail(objectMapper.readValue(outbox.getPayload(), EmailChangeRequestedEvent.class));
                    case PASSWORD_CHANGED -> emailDispatcher.sendPasswordChangedNotification(objectMapper.readValue(outbox.getPayload(), PasswordChangedEvent.class));
                    default -> log.warn("Unknown outbox type={}, id={}", outbox.getEventType().name(), outbox.getId());
                }
                outbox.setProcessedAt(Instant.now());
                outboxRepository.save(outbox);
            } catch (Exception ex) {
                outbox.setAttempts(outbox.getAttempts() + 1);
                log.warn("Outbox send failed (id={}, type={}, attempts={}): {}",
                        outbox.getId(), outbox.getEventType().name(), outbox.getAttempts(), ex.getMessage()
                );

                if (outbox.getAttempts() >= maxAttempts) {
                    log.error("Outbox message moved to dead-letter (id={})", outbox.getId());
                    outbox.setProcessedAt(Instant.now());
                }
            }
        }
    }
}
