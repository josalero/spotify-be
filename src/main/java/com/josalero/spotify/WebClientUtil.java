package com.josalero.spotify;

import lombok.experimental.UtilityClass;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@UtilityClass
public class WebClientUtil {

  public static ReactorClientHttpConnector getReactorClientHttpConnector() {
    ConnectionProvider connectionProvider = ConnectionProvider.builder("custom-connection-provider")
            .maxConnections(20)
            .maxIdleTime(Duration.ofSeconds(60))
            .maxLifeTime(Duration.ofSeconds(120))
            .pendingAcquireTimeout(Duration.ofSeconds(60))
            .build();
    return new ReactorClientHttpConnector(HttpClient.create(connectionProvider)
            .compress(true)
            .wiretap(true)
            .disableRetry(true)
            .keepAlive(true));
  }
}
