package com.josalero.spotify.core.album.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.josalero.spotify.core.album.model.Track;
import com.josalero.spotify.core.album.model.Image;
import com.josalero.spotify.core.album.repository.TrackRepository;
import com.josalero.spotify.core.exception.ResourceAlreadyExistException;
import com.josalero.spotify.core.security.service.PrincipalService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class TrackService {
  TrackRepository trackRepository;
  SpotifyService spotifyService;
  PrincipalService principalService;

  public Track retrieve(String isrc) {
    Optional<Track> track = trackRepository.findByIsrc(isrc);

    if (track.isEmpty()) {
      throw new ResourceAlreadyExistException("Track not found in our records");
    }

    return track.get();
  }

  public Track save(String isrc) {

    Optional<Track> track = trackRepository.findByIsrc(isrc);

    if (track.isPresent()) {
      throw new ResourceAlreadyExistException("Track already exist in our records");
    }

    JsonNode response = spotifyService.getTrack(isrc);

    Track trackToSave = Track.builder()
            .artistName(response.get("artistName").asText())
            .albumId(response.get("albumId").asText())
            .name(response.get("name").asText())
            .explicitContentIndicator(response.get("explicitContentIndicator").asBoolean())
            .playbackSeconds(response.get("playbackSeconds").asInt() / 1000) //seconds
            .albumName(response.get("albumName").asText())
            .isrc(isrc)
            .createdBy(principalService.getPrincipal())
            .createdDate(LocalDateTime.now())
            .build();


    ArrayNode imagesNode = (ArrayNode)response.get("images");
    int size = imagesNode.size();
    for (int i = 0; i < size; i++){
      JsonNode imageNode = imagesNode.get(i);
      Image image = Image.builder()
              .url(imageNode.get("url").asText())
              .width(imageNode.get("width").asInt())
              .height(imageNode.get("height").asInt())
              .createdBy(principalService.getPrincipal())
              .createdDate(LocalDateTime.now())
              .build();

      trackToSave.addImage(image);
    }

    return trackRepository.save(trackToSave);
  }
}
