package com.example.testfrontendbackenddb.dto;


public class ActivityLogDto {
    private String action;
    private String pageUrl;
    private String details;

    // Getters and Setters
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getPageUrl() { return pageUrl; }
    public void setPageUrl(String pageUrl) { this.pageUrl = pageUrl; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
