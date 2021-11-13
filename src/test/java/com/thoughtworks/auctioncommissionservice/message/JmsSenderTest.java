package com.thoughtworks.auctioncommissionservice.message;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@SpringBootTest
public class JmsSenderTest {
    @Mock
    private JmsTemplate jmsTemplate;

    @Test //工序4
    void should_call_jmsTemplate_to_send_message() {
        doNothing().when(jmsTemplate).convertAndSend(anyString(), anyLong());
        JmsSender jmsSender = new JmsSender(jmsTemplate);
        jmsSender.sendEvaluationRequest(123L);

        ArgumentCaptor<String> destination = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> id = ArgumentCaptor.forClass(Long.class);
        verify(jmsTemplate).convertAndSend(destination.capture(), id.capture());
        assertThat(destination.getValue()).isEqualTo("evaluation.center");
        assertThat(id.getValue()).isEqualTo(123L);
    }
}
