package com.josalero.spotify.core.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
@Table(schema = "spotify", name = "user")
@Entity
public class User extends AuditEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
    @EqualsAndHashCode.Include
    UUID id;

    @Column(name = "external_id", nullable = false, updatable = false, columnDefinition = "uuid")
    @EqualsAndHashCode.Include
    UUID externalId;

    @Column(name = "first_name", nullable = false, columnDefinition = "varchar(50)")
    @Size(max = 50, min = 1)
    @EqualsAndHashCode.Include
    String firstname;

    @Column(name = "last_name", nullable = false, columnDefinition = "varchar(50)")
    @Size(max = 50, min = 1)
    @EqualsAndHashCode.Include
    String lastname;

    @Column(name = "email", nullable = false, columnDefinition = "varchar(100)")
    @Size(max = 100, min = 1)
    @EqualsAndHashCode.Include
    String email;

    @Column(name = "username", nullable = false, columnDefinition = "varchar(100)")
    @Size(max = 100, min = 1)
    @EqualsAndHashCode.Include
    String username;

    public String getFullname() {
        return firstname + " " + lastname;
    }

}
