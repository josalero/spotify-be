package com.josalero.spotify.core.user.service;

import com.josalero.spotify.core.exception.ForbiddenAccessException;
import com.josalero.spotify.core.exception.ResourceNotFoundException;
import com.josalero.spotify.core.security.service.PrincipalService;
import com.josalero.spotify.core.user.domain.UserCreateRequest;
import com.josalero.spotify.core.user.model.User;
import com.josalero.spotify.core.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserService {

    UserRepository userRepository;
    PrincipalService principalService;

    @Transactional(readOnly = true)
    public Optional<User> getUserById(UUID uuid) {
        return userRepository.findById(uuid);
    }

    @Transactional(readOnly = true)
    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    private User validateAndReturnPublisher(UUID publisherId) {
        User user = getUserById(publisherId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Optional.ofNullable(principalService.isMe(user.getUsername()))
                .orElseThrow(() -> new ForbiddenAccessException("User is not authorized to execute operation"));
        return user;
    }

    private void validatePublisherEmail(String email) {
        userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Email exist in our records"));
    }


    @Transactional
    public User getUser(UserCreateRequest userCreateRequest) {
        Optional<User> userOptional = userRepository.findByUsername(userCreateRequest.getUsername());

        User user = User.builder()
                .firstname(userCreateRequest.getFirstname())
                .lastname(userCreateRequest.getLastname())
                .email(userCreateRequest.getEmail())
                .username(userCreateRequest.getUsername())
                .externalId(userCreateRequest.getExternalId())
                .createdBy(StringUtils.isBlank(principalService.getPrincipal()) ? userCreateRequest.getUsername() : principalService.getPrincipal())
                .createdDate(LocalDateTime.now())
                .build();

        if (userOptional.isPresent() ) {
            return userOptional.get();
        }

        return userRepository.save(user);
    }

}
