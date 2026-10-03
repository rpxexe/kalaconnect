package com.kalaconnect.repository;

import com.kalaconnect.model.ArtisanProfile;
import com.kalaconnect.model.ArtisanSkill;
import com.kalaconnect.model.Skill;

import java.util.List;
import java.util.Optional;

public interface ArtisanRepository {

    ArtisanProfile save(ArtisanProfile profile);

    Optional<ArtisanProfile> findById(Long id);

    Optional<ArtisanProfile> findByUserId(Long userId);

    List<ArtisanProfile> findAll(String district, String state, String approvalStatus);

    void updateApprovalStatus(Long id, String status);

    List<ArtisanSkill> findSkillsByArtisanId(Long artisanId);

    List<String> findSkillNamesByArtisanId(Long artisanId);

    void addSkillToArtisan(ArtisanSkill skill);

    void syncArtisanSkills(Long artisanProfileId, Long artisanId, List<String> skillNames);

    List<Skill> findAllSkills();

    List<ArtisanProfile> searchArtisansWithFilters(
            String searchQuery,
            String craftCategory,
            String skill,
            String state,
            String district,
            String approvalStatus,
            int limit,
            int offset
    );

    long countArtisansWithFilters(
            String searchQuery,
            String craftCategory,
            String skill,
            String state,
            String district,
            String approvalStatus
    );
}
