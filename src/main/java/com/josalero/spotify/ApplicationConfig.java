package com.josalero.spotify;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.josalero.spotify.core.security.SecurityConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@ComponentScan(basePackages = {"com.josalero.spotify"})
@EnableJpaRepositories(basePackages = {"com.josalero.spotify"})
@EntityScan(basePackages = {"com.josalero.spotify"})
@Slf4j
@Import(value = {SecurityConfig.class})
public class ApplicationConfig {

  private static ExchangeFilterFunction logRequest() {
    return ExchangeFilterFunction.ofRequestProcessor(
            clientRequest -> {
              log.debug("Request: method: {} URL: {}", clientRequest.method(), clientRequest.url());
              return Mono.just(clientRequest);
            });
  }

  private static ExchangeFilterFunction logResponse() {
    return ExchangeFilterFunction.ofResponseProcessor(
            clientResponse -> {
              log.debug("Response: statusCode: {}", clientResponse.statusCode());
              return Mono.just(clientResponse);
            });
  }

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    ObjectMapper result = new ObjectMapper();
    result.enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
    result.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    result.registerModule(new JavaTimeModule());
    result.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    return result;
  }


  @Bean("authWebClient")
  @Primary
  public WebClient authClient(@Value("${token.api.url}") String baseUrl) {

    return WebClient.builder()
            .clientConnector(WebClientUtil.getReactorClientHttpConnector())
            .baseUrl(baseUrl)
            .exchangeStrategies(exchangeStrategies())
            .filter(logRequest())
            .filter(logResponse())
            .build();
  }


  @Bean("spotifyWebClient")
  public WebClient spotifyWebClient(@Value("${spotify.api.url}") String baseUrl) {

    return WebClient.builder()
            .clientConnector(WebClientUtil.getReactorClientHttpConnector())
            .baseUrl(baseUrl)
            .exchangeStrategies(exchangeStrategies())
            .filter(logRequest())
            .filter(logResponse())
            .build();
  }

  @Bean("spotifyAuthWebClient")
  public WebClient spotifyAuthWebClient(@Value("${spotify.auth.api.url}") String baseUrl) {

    return WebClient.builder()
            .clientConnector(WebClientUtil.getReactorClientHttpConnector())
            .baseUrl(baseUrl)
            .exchangeStrategies(exchangeStrategies())
            .filter(logRequest())
            .filter(logResponse())
            .build();
  }

  @Bean("fixedThreadPool")
  public ExecutorService fixedThreadPool() {
    return Executors.newFixedThreadPool(8);
  }

  private ExchangeStrategies exchangeStrategies() {
    return ExchangeStrategies.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(250000))
            .build();
  }

}
