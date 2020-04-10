package com.estafet.openshift.boost.console.api.jira.jms;

import com.estafet.openshift.boost.console.api.jira.service.JiraService;
import com.estafet.openshift.boost.messages.features.CommitMessage;
import io.opentracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommitConsumer {

    public final static String TOPIC = "commit.topic";
    public final static String TASK_MANAGER_VALUE = "jira";

    private Tracer tracer;
    private JiraService jiraService;

    @JmsListener(destination = TOPIC, containerFactory = "myFactory")
    public void onMessage(String message) {
    	if (System.getenv("TASK_MANAGER").equals(TASK_MANAGER_VALUE)) {
            CommitMessage commitMessage = CommitMessage.fromJSON(message);
            String issueId = getIssueId(commitMessage.getMessage());
            if(issueId != null){
            	try {
                    jiraService.getJiraIssueDetails(issueId, commitMessage);
                } finally {
                    if (tracer.activeSpan() != null) {
                        tracer.activeSpan().close();
                    }
                }
            }   		
    	}
    }

    public String getIssueId(String message) {
        String regex= "^(\\[JIRA):{1}[^:]+\\]$";
        String[] splitString = (message.split("\\s+"));
        List<String> matchesList = new ArrayList<>();

        for (String string : splitString) {
            if(string.matches(regex)){
                String newString = string.replaceAll("\\[|\\]","");
                matchesList.add(newString);
            }
        }
        if (matchesList.size()==1){
            String[] stringMass = (matchesList.get(0).split(":"));
            if(stringMass.length==2){
                for (String mass : stringMass) {
                    if (!mass.equals(TASK_MANAGER_VALUE.toUpperCase())) {
                        return mass;
                    }
                }
            }
        }
        return null;
    }

    @Autowired
    public void setTracer(Tracer tracer) {
        this.tracer = tracer;
    }

    @Autowired
    public void setJiraService(JiraService jiraService){
        this.jiraService = jiraService;
    }

}
