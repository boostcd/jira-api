package com.estafet.openshift.boost.console.api.jira.controller;

import com.estafet.openshift.boost.commons.lib.model.API;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JiraController {

    @Value("${app.version}")
    private String appVersion;

    @GetMapping("/api")
    public API getAPI() {
        return new API(appVersion);
    }
}
