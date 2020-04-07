package com.estafet.openshift.boost.console.api.jira.service;

import com.estafet.openshift.boost.console.api.jira.dao.JiraDAO;
import com.estafet.openshift.boost.messages.features.CommitMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JiraServiceImpl implements JiraService {

    private JiraDAO jiraDAO;

    @Override
    public void getJiraIssueDetails(String issueId, CommitMessage commitMessage) {
        jiraDAO.getJiraIssueDetails(issueId,commitMessage);
    }

    @Override
    public void sendUnmatchedCommit(CommitMessage commitMessage) {
        jiraDAO.sendUnmatchedCommit(commitMessage);
    }

    @Autowired
    public void setJiraDAO(JiraDAO jiraDAO) {
        this.jiraDAO = jiraDAO;
    }
}
