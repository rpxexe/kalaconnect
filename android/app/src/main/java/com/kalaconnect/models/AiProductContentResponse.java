package com.kalaconnect.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AiProductContentResponse implements Serializable {

    @SerializedName("description")
    private String description;

    @SerializedName("caption")
    private String caption;

    @SerializedName("hashtags")
    private List<String> hashtags = new ArrayList<>();

    public AiProductContentResponse() {
    }

    public AiProductContentResponse(String description, String caption, List<String> hashtags) {
        this.description = description;
        this.caption = caption;
        this.hashtags = hashtags != null ? hashtags : new ArrayList<>();
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCaption() {
        return caption != null ? caption : "";
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public List<String> getHashtags() {
        return hashtags != null ? hashtags : new ArrayList<>();
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags != null ? hashtags : new ArrayList<>();
    }

    public String getHashtagsAsString() {
        if (hashtags == null || hashtags.isEmpty()) {
            return "";
        }
        return String.join(" ", hashtags);
    }
}
