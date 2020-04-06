package com.estafet.openshift.boost.console.api.jira.dao;

import com.estafet.openshift.boost.console.api.jira.JiraUtils;
import com.estafet.openshift.boost.console.api.jira.jms.IssueDetailsProducer;
import com.estafet.openshift.boost.console.api.jira.jms.UnmatchedCommitProducer;
import com.estafet.openshift.boost.console.api.jira.model.Issue;
import com.estafet.openshift.boost.messages.features.CommitMessage;
import com.estafet.openshift.boost.messages.features.FeatureMessage;
import com.estafet.openshift.boost.messages.features.FeatureStatus;
import com.estafet.openshift.boost.messages.features.UnmatchedCommitMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.naming.AuthenticationException;

@Repository
public class JiraDAO {

    @Autowired
    private IssueDetailsProducer issueDetailsProducer;
    @Autowired
    private UnmatchedCommitProducer unmatchedCommitProducer;

    public void getJiraIssueDetails(String issueId, CommitMessage commitMessage) {
//        TODO add system env
        String auth = "iryna.poplavska@estafet.com:gZwb6zR5KrWKjQ01dcQP7D9F";
        String url = getUrl(issueId);
        String stringIssue = getIssueDetails(auth, url);
        Issue issue = getIssue(stringIssue);

        if(issue!=null && issue.getFields()!=null && issue.getFields().getIssueType()!=null ){
            if(isValidIssue(issue.getFields().getIssueType().getValue())){
                processIssue(commitMessage, issue, auth);
            } else {
                sendUnmatchedCommit(commitMessage);
            }
        }
    }

    private void sendUnmatchedCommit(CommitMessage commitMessage) {
        unmatchedCommitProducer.sendMessage(UnmatchedCommitMessage.builder()
                .setCommitId(commitMessage.getCommitId())
                .setRepo(commitMessage.getRepo())
                .build());
    }


    private Issue getIssue(String stringIssue) {
        return Issue.fromJSON(stringIssue);
    }

    private void processIssue(CommitMessage commitMessage, Issue issue, String auth) {
        String issueType = issue.getFields().getIssueType().getValue();
        if(isStoryOrBug(issueType)){
            FeatureMessage featureMessage = mapping(issue, commitMessage);
            issueDetailsProducer.sendMessage(featureMessage);
        }
        if(issueType.equals("Sub-task")){
            if( issue.getFields().getParent()!=null && issue.getFields().getParent().getId()!=null){
                String parentKey = issue.getFields().getParent().getId();
                String stringStoryIssue = getIssueDetails(auth, getUrl(parentKey));
                Issue storyIssue = getIssue(stringStoryIssue);
                processIssue(commitMessage, storyIssue, auth);
            }
        }
    }

    public Boolean isValidIssue(String issueType){
        return issueType.equals("Story") || issueType.equals("Bug") || issueType.equals("Sub-task");
    }

    public Boolean isStoryOrBug(String issueType){
        return issueType.equals("Story") || issueType.equals("Bug");
    }

    private String getIssueDetails(String auth, String url) {
        String stringIssue = null;
        try {
            stringIssue = JiraUtils.invokeGetMethod(auth,url);
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
        return stringIssue;
    }

    private String getUrl(String issueId) {
        return "https://estafet.atlassian.net/rest/api/3/issue/" + issueId+"?fields=key,summary,description,issuetype," +
                "summary,updated,status,parent";
    }

    public FeatureMessage mapping(Issue issue, CommitMessage commitMessage) {
        if(issue == null || issue.getFields()==null){
            return null;
        }

        FeatureStatus status = getFeatureStatus(issue);

//      TODO update featureURL value
        return FeatureMessage.builder()
                .setFeatureId(issue.getId())
                .setCommitId(commitMessage.getCommitId())
                .setRepo(commitMessage.getRepo())
                .setTitle(issue.getFields().getTitle())
                .setDescription("")
                .setStatus(status)
                .setLastUpdated(issue.getFields().getLastUpdated())
                .setFeatureURL("https://estafet.atlassian.net"+"/browse/"+issue.getId())
                .build();

    }

    private FeatureStatus getFeatureStatus(Issue issue) {
        if(issue.getFields().getStatus() == null){
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
