package com.ead.authuser.publishers;

import com.ead.authuser.dtos.UserEventDTO;
import com.ead.authuser.enums.ActionType;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value(value = "${ead.broker.exchange.userEvent}")
    private String exchangeUserEvent;

    public void publisherUserEvent(UserEventDTO userEventDTO, ActionType actionType) {
        userEventDTO.setActionType(actionType.toString());
        rabbitTemplate.convertAndSend(exchangeUserEvent,"", userEventDTO);
    }
}
