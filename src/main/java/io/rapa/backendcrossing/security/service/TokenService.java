package io.rapa.backendcrossing.security.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.security.repository.RefreshTokenRepository;
import io.rapa.backendcrossing.security.domain.RefreshToken;
import io.rapa.backendcrossing.security.constants.TokenType;
import io.rapa.backendcrossing.security.domain.dto.JwtProperties;
import io.rapa.backendcrossing.security.domain.dto.KeyPair;
import io.rapa.backendcrossing.security.domain.dto.TokenBody;
import io.rapa.backendcrossing.users.constants.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenService {
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository repository;
    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtProperties.getSecrets().getSecretKey().getBytes());
    }


    private String issueRefreshToken(String email){
        String refreshToken = Jwts
                .builder()
                .subject(jwtProperties.getPayLoads().getSubjectRefreshToken())
                .issuer(jwtProperties.getPayLoads().getIssuer())
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtProperties.getValidations().getRefresh()))
                .signWith(getSecretKey())
                .compact();
        repository.save(
            RefreshToken.builder()
                    .refreshToken(refreshToken)
                    .email(email)
                    .build()
        );
        return refreshToken;
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
    public TokenBody parseJwt(String token){
        Jws<Claims> claimsJws = parseClaims(token);
        return  TokenBody.builder()
                .email(String.valueOf(claimsJws.getPayload().get("email")))
                .build();
    }
}
