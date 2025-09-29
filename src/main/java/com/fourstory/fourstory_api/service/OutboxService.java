package com.fourstory.fourstory_api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourstory.fourstory_api.model.tglobal.Outbox;
import com.fourstory.fourstory_api.model.tglobal.OutboxEventType;
import com.fourstory.fourstory_api.repository.tglobal.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;

    private final ObjectMapper objectMapper;

    @Transactional(transactionManager = "tglobalTransactionManager")
    public void enqueue(OutboxEventType eventType, Object payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);

            outboxRepository.save(Outbox.builder()
                    .eventType(eventType)
                    .payload(json)
                    .createdAt(Instant.now())
                    .processedAt(null)
                    .attempts(0)
                    .build()
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize outbox payload for event = " + eventType.name(), e);
        }
    }
}
