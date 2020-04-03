package com.estafet.openshift.boost.console.api.jira.jms;

import com.estafet.openshift.boost.console.api.jira.service.JiraService;
import com.estafet.openshift.boost.messages.features.CommitMessage;
import com.estafet.openshift.boost.messages.features.UnmatchedCommitMessage;
import io.opentracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommitConsumer {

    public final static String TOPIC = "commit.topic";

    private Tracer tracer;
    private JiraService jiraService;
    private UnmatchedCommitProducer unmatchedCommitProducer;

    @JmsListener(destination = TOPIC, containerFactory = "myFactory")
    public void onMessage(String message) {
//    	if (System.getenv("TASK_MANAGER").equals("jira")) {
            CommitMessage commitMessage = CommitMessage.fromJSON(message);
            String issueId = getIssueId(commitMessage.getMessage());
            if(issueId==null){
                sendUnmatchedCommit(commitMessage);
            } else {
                try {
                    jiraService.getJiraIssueDetails(issueId, commitMessage);
                } finally {
                    if (tracer.activeSpan() != null) {
                        tracer.activeSpan().close();
                    }
                }
            }    		
//    	}
    }

    public String getIssueId(String message) {
        // dneneisinsisn  kffkfkfkf [JIRA FG_8877]
        return "RHYT-100";
/*        String regex = "";
        String[] splitString = (message.split("\\s+"));
        List<String> matchesList = new ArrayList<>();
        for (String string : splitString) {
            if(string.matches(regex)){
                matchesList.add(string);
            }
        }
        if (matchesList.size()==1){
            String url = matchesList.get(0);
            return url+".json?";
        } else {
            return null;
        }*/
    }

    private void sendUnmatchedCommit(CommitMessage commitMessage) {
        unmatchedCommitProducer.sendMessage(UnmatchedCommitMessage.builder()
                .setCommitId(commitMessage.getCommitId())
                .setRepo(commitMessage.getRepo())
                .build());
    }

    @Autowired
    public void setTracer(Tracer tracer) {
        this.tracer = tracer;
    }

    @Autowired
    public void setJiraService(JiraService jiraService){
        this.jiraService = jiraService;
    }

    @Autowired
    public void setUnmatchedCommitProducer(UnmatchedCommitProducer unmatchedCommitProducer) {
        this.unmatchedCommitProducer = unmatchedCommitProducer;
    }
}
