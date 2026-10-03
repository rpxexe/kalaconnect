package com.kalaconnect.model;

import java.time.OffsetDateTime;

public class ArtisanSkill {
    private Long id;
    private Long artisanProfileId;
    private Long artisanId;
    private Long skillId;
    private String skillName;
    private String proficiencyLevel = "INTERMEDIATE";
    private Integer yearsOfExperience = 0;
    private OffsetDateTime createdAt;

    public ArtisanSkill() {
    }

    public ArtisanSkill(Long id, Long artisanProfileId, Long artisanId, Long skillId, String skillName,
                        String proficiencyLevel, Integer yearsOfExperience, OffsetDateTime createdAt) {
        this.id = id;
        this.artisanProfileId = artisanProfileId;
        this.artisanId = artisanId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.proficiencyLevel = proficiencyLevel;
        this.yearsOfExperience = yearsOfExperience;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArtisanProfileId() {
        return artisanProfileId;
    }

    public void setArtisanProfileId(Long artisanProfileId) {
        this.artisanProfileId = artisanProfileId;
    }

    public Long getArtisanId() {
        return artisanId;
    }

    public void setArtisanId(Long artisanId) {
        this.artisanId = artisanId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(String proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
