package com.estafet.boostcd.jira.api.jms;

import com.estafet.openshift.boost.messages.features.FeatureMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class IssueDetailsProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendMessage(FeatureMessage message) {
        jmsTemplate.setPubSubDomain(true);
        jmsTemplate.convertAndSend("feature.topic", message.toJSON());
    }
}
