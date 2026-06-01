package io.rapa.backendcrossing.security.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "custom.jwt")
public class JwtProperties {

    private final Secrets secrets;
    private final Validations validations;
    private final PayLoads payLoads;

    @Getter
    @RequiredArgsConstructor
    public static class PayLoads{
        private final String issuer;
        private final String subjectAccessToken;
        private final String subjectRefreshToken;
        private final String audiance;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Secrets{
        private final String secretKey;
        private final String vanillaKey;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Validations{
        private final Integer access;
        private final Integer refresh;
    }
}
