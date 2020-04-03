package com.estafet.openshift.boost.console.api.jira.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Issue {

    @JsonProperty("key")
    private String id;

    @JsonProperty("summary") //
    private String title;

    private String description; //

    @JsonProperty("updated")
    private String lastUpdated; //

    private String status;

    public Issue() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static Issue fromJSON(String message) {
        try {
            return (new ObjectMapper()).readValue(message, Issue.class);
        } catch (IOException var2) {
            throw new RuntimeException(var2);
        }
    }
}
