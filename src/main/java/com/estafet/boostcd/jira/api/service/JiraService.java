package com.estafet.boostcd.jira.api.service;

import com.estafet.openshift.boost.messages.features.CommitMessage;

public interface JiraService {
    void getJiraIssueDetails(String issueId, CommitMessage commitMessage);

}
