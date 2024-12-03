package com.josalero.spotify.core.album.repository;

import com.josalero.spotify.core.album.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrackRepository extends JpaRepository<Track, UUID> {

  Optional<Track> findByIsrc(String isrc);

}
