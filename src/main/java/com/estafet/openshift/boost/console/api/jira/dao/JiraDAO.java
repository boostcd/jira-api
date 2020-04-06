package com.estafet.openshift.boost.console.api.jira.dao;

import com.estafet.openshift.boost.console.api.jira.JiraUtils;
import com.estafet.openshift.boost.console.api.jira.jms.IssueDetailsProducer;
import com.estafet.openshift.boost.console.api.jira.model.Issue;
import com.estafet.openshift.boost.messages.features.CommitMessage;
import com.estafet.openshift.boost.messages.features.FeatureMessage;
import com.estafet.openshift.boost.messages.features.FeatureStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.naming.AuthenticationException;

@Repository
public class JiraDAO {

    @Autowired
    private IssueDetailsProducer issueDetailsProducer;

    public void getJiraIssueDetails(String issueId, CommitMessage commitMessage) {
//        TODO https://estafet.atlassian.net/rest/api/3/search?jql=key=RHYT-100&fields=key,summary,description,issuetype,summary,updated,status
//        TODO https://estafet.atlassian.net/rest/api/3/issue/RHYT-100?fields=key,summary,description,issuetype,summary,updated,status
//        TODO add system env
        String auth = "iryna.poplavska@estafet.com:gZwb6zR5KrWKjQ01dcQP7D9F";
        String url = "https://estafet.atlassian.net/rest/api/3/issue/" + issueId+"?fields=key,summary,description,issuetype,summary,updated,status";
        String stringIssue = null;
        try {
            stringIssue = JiraUtils.invokeGetMethod(auth,url);
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }

        Issue issue = Issue.fromJSON(stringIssue);

        FeatureMessage featureMessage = mapping(issue, commitMessage, url);
        issueDetailsProducer.sendMessage(featureMessage);
        System.out.println("feature mess = " + featureMessage.toJSON());

    }

    public FeatureMessage mapping(Issue issue, CommitMessage commitMessage, String url) {
        if(issue == null){
            return null;
        }

        FeatureStatus status = getFeatureStatus(issue);

//      TODO update feature value
        return FeatureMessage.builder()
                .setFeatureId(issue.getId())
                .setCommitId(commitMessage.getCommitId())
                .setRepo(commitMessage.getRepo())
                .setTitle(issue.getFields().getTitle())
                .setDescription(issue.getFields().getDescription())
                .setStatus(status)
                .setLastUpdated(issue.getFields().getLastUpdated())
                .setFeatureURL("https://estafet.atlassian.net"+"/browse/"+issue.getId())
                .build();

    }

    private FeatureStatus getFeatureStatus(Issue issue) {
        if(issue.getFields() == null || (issue.getFields()!= null && issue.getFields().getStatus() == null)){
            return null;
        }

        FeatureStatus status = null;

        if(issue.getFields().getStatus().getValue().equals(FeatureStatus.DONE.getValue())){
            status=FeatureStatus.DONE;
        }
        if(issue.getFields().getStatus().getValue().equals(FeatureStatus.IN_PROGRESS.getValue())){
            status=FeatureStatus.IN_PROGRESS;
        }

        if(issue.getFields().getStatus().getValue().equals(FeatureStatus.NOT_STARTED.getValue())){
            status=FeatureStatus.NOT_STARTED;
        }

        if(issue.getFields().getStatus().getValue().equals(FeatureStatus.BLOCKED.getValue())){
            status=FeatureStatus.BLOCKED;
        }
        return status;
    }
}
