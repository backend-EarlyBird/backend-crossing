package io.rapa.backendcrossing.security.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.security.constants.TokenType;
import io.rapa.backendcrossing.security.domain.dto.JwtProperties;
import io.rapa.backendcrossing.security.domain.dto.KeyPair;
import io.rapa.backendcrossing.security.domain.dto.TokenBody;
import io.rapa.backendcrossing.users.constants.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtProperties jwtProperties;
    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtProperties.getSecrets().getSecretKey().getBytes());
    }

    private String issueRefreshToken(String email){
        return Jwts
                .builder()
                .subject(jwtProperties.getPayLoads().getSubjectRefreshToken())
                .issuer(jwtProperties.getPayLoads().getIssuer())
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtProperties.getValidations().getRefresh()))
                .signWith(getSecretKey())
                .compact();
    }

    private String issueAccessToken(
            String email,
            Role role
    ){
        return Jwts
                .builder()
                .subject(jwtProperties.getPayLoads().getSubjectAccessToken())
                .issuer(jwtProperties.getPayLoads().getIssuer())
                .claim("email", email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtProperties.getValidations().getRefresh()))
                .signWith(getSecretKey())
                .compact();
    }

    public KeyPair issueKeyPair(
            String email,
            Role role
    ){
        return KeyPair.builder()
                .accessToken(issueAccessToken(email,role))
                .refreshToken(issueRefreshToken(email))
                .build();
    }

    public boolean validate(String token){
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch(ExpiredJwtException e){
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch(MalformedJwtException e){
            throw new CustomException(ErrorCode.ABNORMAL_TOKEN);
        } catch(JwtException e){
            throw new CustomException(ErrorCode.ERROR_FROM_TOKEN);
        }
    }

    public Jws<Claims> parseClaims(String token){
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token);
    }
    public TokenBody parseJwt(String token, TokenType tokenType){
        Jws<Claims> claimsJws = parseClaims(token);
        return switch (tokenType){
            case TokenType.ACCESS_TOKEN -> TokenBody.builder()
                    .email(String.valueOf(claimsJws.getPayload().get("email")))
                    .role(Role.valueOf(claimsJws.getPayload().get("role").toString()))
                    .build();
            case TokenType.REFRESH_TOKEN -> TokenBody.builder()
                    .email(String.valueOf(claimsJws.getPayload().get("email")))
                    .build();
        };
    }
}
