package com.fourstory.fourstory_api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourstory.fourstory_api.event.VerificationResentEvent;
import com.fourstory.fourstory_api.model.tglobal.Outbox;
import com.fourstory.fourstory_api.model.tglobal.OutboxEventType;
import com.fourstory.fourstory_api.repository.tglobal.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxServiceTest {

    @InjectMocks
    private OutboxService outboxService;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    public void enqueue_shouldThrowIllegalStateException_whenJsonProcessingException() throws JsonProcessingException {
        VerificationResentEvent event = new VerificationResentEvent("email@email.cz", "rawToken");


        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("Error"){});
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> outboxService.enqueue(OutboxEventType.EMAIL_VERIFICATION_REQUEST, event));

        assertEquals("Failed to serialize outbox payload for event = " + OutboxEventType.EMAIL_VERIFICATION_REQUEST.name(), exception.getMessage());
        verify(objectMapper).writeValueAsString(event);
    }

    @Test
    public void enqueue_shouldSaveEvent() throws JsonProcessingException {
        VerificationResentEvent event = new VerificationResentEvent("email@email.cz", "rawToken");


        when(objectMapper.writeValueAsString(event)).thenReturn("json");


        outboxService.enqueue(OutboxEventType.EMAIL_VERIFICATION_REQUEST, event);

        verify(objectMapper).writeValueAsString(event);
        verify(outboxRepository).save(any(Outbox.class));
    }
}