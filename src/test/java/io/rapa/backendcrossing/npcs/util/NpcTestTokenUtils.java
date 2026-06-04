package io.rapa.backendcrossing.npcs.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.rapa.backendcrossing.security.domain.dto.JwtProperties;
import io.rapa.backendcrossing.users.constants.Role;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * packageName    : io.rapa.backendcrossing.npcs.util
 * fileName       : NpcTestTokenUtils
 * author         : Admin
 * date           : 26. 6. 3.
 * description    : NPC 테스트용 AccessToken 생성 유틸 (Redis 없이 직접 생성)
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 3.        Admin       최초 생성
 */
@Component
public class NpcTestTokenUtils {

    private final JwtProperties jwtProperties;

    public NpcTestTokenUtils(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateAccessToken(String email, Role role) {
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecrets().getSecretKey().getBytes());
        return Jwts.builder()
                .subject(jwtProperties.getPayLoads().getSubjectAccessToken())
                .issuer(jwtProperties.getPayLoads().getIssuer())
                .claim("email", email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtProperties.getValidations().getAccess()))
                .signWith(secretKey)
                .compact();
    }
}
