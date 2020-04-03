package com.estafet.openshift.boost.console.api.jira.jms;

import com.estafet.openshift.boost.messages.features.UnmatchedCommitMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class UnmatchedCommitProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendMessage(UnmatchedCommitMessage message) {
        jmsTemplate.setPubSubDomain(true);
        jmsTemplate.convertAndSend("unmatched.commit.topic", message.toJSON());
    }
    
}
