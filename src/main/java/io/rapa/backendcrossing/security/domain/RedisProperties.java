package io.rapa.backendcrossing.security.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@RequiredArgsConstructor
@ConfigurationProperties(value = "spring.data.redis")
public class RedisProperties {
    private String host;
    private String port;
}
