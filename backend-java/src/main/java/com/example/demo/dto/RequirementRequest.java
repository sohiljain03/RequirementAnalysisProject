package com.example.demo.dto;

public class RequirementRequest {
    private String requirementsText;

    public RequirementRequest() {}

    public RequirementRequest(String requirementsText) {
        this.requirementsText = requirementsText;
    }

    public String getRequirementsText() {
        return requirementsText;
    }

    public void setRequirementsText(String requirementsText) {
        this.requirementsText = requirementsText;
    }
}
