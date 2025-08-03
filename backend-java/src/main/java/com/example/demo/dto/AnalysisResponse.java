package com.example.demo.dto;

import java.util.List;

public class AnalysisResponse {
    private List<String> userStories;
    private String lld;

    public AnalysisResponse() {}

    public AnalysisResponse(List<String> userStories, String lld) {
        this.userStories = userStories;
        this.lld = lld;
    }

    public List<String> getUserStories() {
        return userStories;
    }

    public void setUserStories(List<String> userStories) {
        this.userStories = userStories;
    }

    public String getLld() {
        return lld;
    }

    public void setLld(String lld) {
        this.lld = lld;
    }
}
