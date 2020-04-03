package com.estafet.openshift.boost.console.api.jira.service;

import com.estafet.openshift.boost.messages.features.CommitMessage;

public interface JiraService {
    void getJiraIssueDetails(String issueId, CommitMessage commitMessage);
}
