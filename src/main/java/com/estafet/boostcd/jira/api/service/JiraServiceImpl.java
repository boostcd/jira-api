package com.estafet.boostcd.jira.api.service;

import com.estafet.boostcd.jira.api.dao.JiraDAO;
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

    @Autowired
    public void setJiraDAO(JiraDAO jiraDAO) {
        this.jiraDAO = jiraDAO;
    }
}
