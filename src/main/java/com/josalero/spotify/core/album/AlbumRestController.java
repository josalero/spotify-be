package com.josalero.spotify.core.album;

import com.josalero.spotify.core.album.model.Track;
import com.josalero.spotify.core.album.service.AlbumService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/spotify")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tags(value = {
    @Tag(name="Admin")
})
public class AlbumRestController {

  AlbumService albumService;

  @PostMapping("/track")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public ResponseEntity<Track> postTracK(@RequestParam("isrc") String isrc) {

    return ResponseEntity.ok(albumService.save(isrc));
  }

  @GetMapping("/track")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public ResponseEntity<Track> getTracK(@RequestParam("isrc") String isrc) {

    return ResponseEntity.ok(albumService.save(isrc));
  }

}