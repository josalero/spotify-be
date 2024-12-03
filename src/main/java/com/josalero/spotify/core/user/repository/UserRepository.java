package com.josalero.spotify.core.user.repository;

import com.josalero.spotify.core.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByEmail(String email);

  Optional<User> findByExternalId(UUID externalId);

  Optional<User> findByUsername(String username);
}
