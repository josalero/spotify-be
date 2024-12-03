package com.josalero.spotify.core.album.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.josalero.spotify.core.user.model.AuditEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(schema = "spotify", name = "track")
@Entity
public class Track extends AuditEntity {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
  UUID id;

  @Column(name = "name", nullable = false, columnDefinition = "varchar(50)")
  @Size(max = 50, min = 1)
  String name;

  @Column(name = "external_id", nullable = false, columnDefinition = "varchar(30)")
  @Size(max = 30, min = 1)
  String externalId;

  @Column(name = "artist_name", nullable = false, columnDefinition = "varchar(50)")
  @Size(max = 50, min = 1)
  String artistName;

  @Column(name = "track_name", nullable = false, columnDefinition = "varchar(50)")
  @Size(max = 50, min = 1)
  String albumName;

  @Column(name = "isrc", nullable = false, columnDefinition = "varchar(20)")
  @Size(max = 20, min = 1)
  String isrc;

  @Column(name = "explicit_content_indicator", nullable = false, columnDefinition = "boolean default true")
  boolean explicitContentIndicator;

  @Column(name = "playback_seconds", nullable = false)
  int playbackSeconds;

  @OneToMany(
          mappedBy = "track",
          orphanRemoval = true,
          cascade = {CascadeType.ALL},
          fetch = FetchType.EAGER)
  @ToString.Exclude
  @JsonIgnore
  List<Image> images;

  public void addImage(Image image) {
    if (images == null) {
      images = new ArrayList<>();
    }
    image.setTrack(this);
    images.add(image);
  }

  public void removeImage(Image image) {
    image.setTrack(null);
    images.remove(image);
  }

}
