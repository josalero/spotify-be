package com.josalero.spotify.core.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
@SuperBuilder
public class AuditEntity {

  @NotNull
  @Column(name = "created_date")
  @JsonFormat(pattern="yyyy-MM-dd HH:mm")
  private LocalDateTime createdDate;

  @NotNull
  @Column(name = "created_by")
  private String createdBy;
}
