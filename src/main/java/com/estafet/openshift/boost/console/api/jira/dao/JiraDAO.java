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

    private IssueDetailsProducer issueDetailsProducer;

    public final static String JIRA_ISSUE_API_BASE_URL = "https://estafet.atlassian.net/rest/api/3/issue/";
    public final static String JIRA_ISSUE_API_FIELDS ="?fields=key,summary,description,issuetype,summary,updated,status,parent";

    public void getJiraIssueDetails(String issueId, CommitMessage commitMessage) {

        String auth = getJiraUsername() +":"+getJiraAccessToken();
        String url = getUrl(issueId);
        String stringIssue = getIssueDetails(auth, url);

        if(stringIssue == null){
            return;
        }
        Issue issue = getIssue(stringIssue);

        if(isValidIssue(issue.getFields().getIssueType().getValue())){
            processIssue(commitMessage, issue, auth);
        }
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
                String stringParentIssue = getIssueDetails(auth, getUrl(parentKey));
                if(stringParentIssue == null){
                    return;
                }
                Issue storyIssue = getIssue(stringParentIssue);
                processIssue(commitMessage, storyIssue, auth);
            }
        }
    }

    private Boolean isValidIssue(String issueType){
        return issueType.equals("Story") || issueType.equals("Bug") || issueType.equals("Sub-task");
    }

    private Boolean isStoryOrBug(String issueType){
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
        return JIRA_ISSUE_API_BASE_URL + issueId+JIRA_ISSUE_API_FIELDS;
    }

    private FeatureMessage mapping(Issue issue, CommitMessage commitMessage) {
        if(issue == null || issue.getFields()==null){
            return null;
        }

        FeatureStatus status = getFeatureStatus(issue);

        return FeatureMessage.builder()
                .setFeatureId(issue.getId())
                .setCommitId(commitMessage.getCommitId())
                .setRepo(commitMessage.getRepo())
                .setTitle(issue.getFields().getTitle())
                .setDescription(issue.getFields().getDescription())
                .setStatus(status)
                .setLastUpdated(issue.getFields().getLastUpdated())
                .setFeatureURL(getJiraBaseUrl()+"/browse/"+issue.getId())
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

    private String getJiraUsername() {
        return System.getenv("JIRA_USERNAME");
    }

    private String getJiraAccessToken() {
        return System.getenv("JIRA_ACCESS_TOKEN");
    }

    private String getJiraBaseUrl() {
        return System.getenv("JIRA_BASE_URL");
    }

    @Autowired
    public void setIssueDetailsProducer(IssueDetailsProducer issueDetailsProducer) {
        this.issueDetailsProducer = issueDetailsProducer;
    }

}
