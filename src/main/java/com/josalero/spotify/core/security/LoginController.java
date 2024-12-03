package com.josalero.spotify.core.security;

import com.josalero.spotify.core.security.domain.LoginRequest;
import com.josalero.spotify.core.security.domain.LoginResponse;
import com.josalero.spotify.core.security.service.LoginService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@CrossOrigin(origins = "*")
@RequestMapping("/public")
@Slf4j
public class LoginController {

    LoginService loginService;

    @PostMapping("/login")
    @Tags(value = {
        @Tag(name="App Access"),
        @Tag(name="Public")
    })
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = loginService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

}
