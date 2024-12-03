package com.josalero.spotify.core.album.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.josalero.spotify.core.user.model.AuditEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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

import java.util.UUID;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
@Table(schema = "spotify", name = "image")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Image extends AuditEntity {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
  UUID id;

  @Column(name = "url", nullable = false, columnDefinition = "varchar(250)")
  @Size(max = 250, min = 1)
  String url;

  @Column(name = "width", nullable = false)
  int width;

  @Column(name = "height", nullable = false)
  int height;

  @OneToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "album_id", referencedColumnName = "id")
  @JsonBackReference
  @JsonIgnore
  Track track;

}
