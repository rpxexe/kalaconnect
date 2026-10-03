package com.kalaconnect.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AiProductContentResponseDto {

    private String description;
    private String caption;
    private List<String> hashtags = new ArrayList<>();

    public AiProductContentResponseDto() {
    }

    public AiProductContentResponseDto(String description, String caption, List<String> hashtags) {
        this.description = description;
        this.caption = caption;
        this.hashtags = hashtags != null ? hashtags : new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags != null ? hashtags : new ArrayList<>();
    }
}
