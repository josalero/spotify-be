package com.josalero.spotify.core.album;

import com.josalero.spotify.core.album.model.Track;
import com.josalero.spotify.core.album.service.TrackService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.constraints.NotNull;
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
public class TrackRestController {

  TrackService trackService;

  @PostMapping("/track")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public ResponseEntity<Track> postTracK(@RequestParam("isrc") @NotNull String isrc) {

    return ResponseEntity.ok(trackService.save(isrc));
  }

  @GetMapping("/track")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public ResponseEntity<Track> getTracK(@RequestParam("isrc") String isrc) {

    return ResponseEntity.ok(trackService.retrieve(isrc));
  }

}
