package com.group3.backend.dto;

public class UpdateUserProfileRequest {
    private String fullName;

    public UpdateUserProfileRequest() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}