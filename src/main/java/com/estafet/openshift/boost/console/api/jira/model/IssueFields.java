package com.estafet.openshift.boost.console.api.jira.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueFields {

    @JsonProperty("summary")
    private String title;

    private String description;

    @JsonProperty("updated")
    private String lastUpdated;

    @JsonProperty("issuetype")
    @SerializedName("issuetype")
    private IssueType issueType;

    @SerializedName("status")
    private IssueStatus status;

    @SerializedName("parent")
    private ParentIssue parent;

    public IssueFields(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }


    public IssueType getIssueType() {
        return issueType;
    }

    public void setIssueType(IssueType issueType) {
        this.issueType = issueType;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public ParentIssue getParent() {
        return parent;
    }

    public void setParent(ParentIssue parent) {
        this.parent = parent;
    }
}
