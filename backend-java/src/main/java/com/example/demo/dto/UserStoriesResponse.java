package com.example.demo.dto;

import java.util.List;

public class UserStoriesResponse {
    private List<String> userStories;

    public UserStoriesResponse() {}

    public UserStoriesResponse(List<String> userStories) {
        this.userStories = userStories;
    }

    public List<String> getUserStories() {
        return userStories;
    }

    public void setUserStories(List<String> userStories) {
        this.userStories = userStories;
    }
}
