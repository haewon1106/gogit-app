package com.gogit.gogit_app.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddRepositoryRequest {
    private String name;
    private String description;
    @JsonProperty("private")
    private Boolean isPrivate;

    public AddRepositoryRequest(String name, String description, Boolean isPrivate) {
        this.name = name;
        this.description = description;
        this.isPrivate = isPrivate;
    }
}
