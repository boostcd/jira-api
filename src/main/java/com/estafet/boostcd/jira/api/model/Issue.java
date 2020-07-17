package com.estafet.boostcd.jira.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Issue {

    @JsonProperty("key")
    private String id;

    @SerializedName("fields")
    private IssueFields fields;

    public Issue() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public IssueFields getFields() {
        return fields;
    }

    public void setFields(IssueFields fields) {
        this.fields = fields;
    }

    public static Issue fromJSON(String message) {
        try {
            return (new ObjectMapper()).readValue(message, Issue.class);
        } catch (IOException var2) {
            throw new RuntimeException(var2);
        }
    }
}
