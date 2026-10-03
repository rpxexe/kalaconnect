package com.kalaconnect.repository;

import com.kalaconnect.model.ArtisanProfile;
import com.kalaconnect.model.NgoProfile;
import com.kalaconnect.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAll();

    void createArtisanProfile(ArtisanProfile profile);

    void createNgoProfile(NgoProfile profile);
}
