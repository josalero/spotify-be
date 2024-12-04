package com.josalero.spotify.core.album.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.josalero.spotify.core.exception.ApiCallException;
import com.josalero.spotify.core.exception.ResourceNotFoundException;
import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Base64;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Transactional
public class SpotifyService {

  @Autowired
  @Qualifier("spotifyAuthWebClient")
  WebClient spotifyAuthWebClient;

  @Autowired
  @Qualifier("spotifyWebClient")
  WebClient spotifyWebClient;

  @Value("${spotify.client.id}")
  String clientId;

  @Value("${spotify.client.secret}")
  String clientSecret;


  private String getToken() {

    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("grant_type", "client_credentials");

    String credentials = clientId + ":" + clientSecret;

    JsonNode response = spotifyAuthWebClient
        .post()
        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes()))
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData(formData))
        .exchange()
        .block()
        .bodyToMono(JsonNode.class)
        .block();

    return response.get("access_token").asText();

  }

  @Transactional(readOnly = true)
  public JsonNode getTrack(String isrc) {

    String searchUrl = "/search?offset=0&limit=1&query=isrc:%s&type=track";

    JsonNode response =
        Failsafe.with(getPolicy())
          .get(() -> spotifyWebClient.
              get()
              .uri(String.format(searchUrl, isrc))
              .header("Authorization", "Bearer " + getToken())
              .retrieve()
              .bodyToMono(JsonNode.class)
              .block());

    return  getAlbumDetails(response);
  }

  private JsonNode getAlbumDetails(JsonNode response) {
    if (response == null || response.isEmpty()) {
      throw new ResourceNotFoundException("ISRC code not found");
    }

    JsonNode jsonNode = response.get("tracks").get("items").get(0);

    if (jsonNode == null || jsonNode.isEmpty()){
      throw new ResourceNotFoundException("ISRC code not found");
    }

    ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
    //Track name
    objectNode.put("name", jsonNode.get("name"));
    //Track id
    objectNode.put("albumId", jsonNode.get("album").get("id"));
    //Artist name
    objectNode.put("artistName", jsonNode.get("artists").get(0).get("name"));
    //Track name
    objectNode.put("albumName", jsonNode.get("album").get("name"));
    //explicitContentIndicator
    objectNode.put("explicitContentIndicator", jsonNode.get("explicit"));
    //playbackSeconds
    objectNode.put("playbackSeconds", jsonNode.get("duration_ms"));
    //images
    objectNode.put("images", jsonNode.get("album").get("images"));
    return objectNode;
  }

  private RetryPolicy<JsonNode> getPolicy(){
    return RetryPolicy.<JsonNode>builder()
            .withDelay(Duration.ofSeconds(5))
            .withMaxRetries(5)
            .onFailure(r -> log.error(r.getResult().toPrettyString() + " " + r.getException().getMessage()))
            .build();
  }
}
