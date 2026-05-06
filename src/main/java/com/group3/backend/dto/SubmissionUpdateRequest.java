package com.group3.backend.dto;

/** Update an existing submission code and optionally re-run Gemini review. */
public class SubmissionUpdateRequest {
    private String code;
    private String language = "python";
    private Integer timeTaken;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Integer timeTaken) {
        this.timeTaken = timeTaken;
    }
}
