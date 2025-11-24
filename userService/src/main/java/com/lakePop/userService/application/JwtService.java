package com.lakePop.userService.application;

import com.lakePop.userService.domain.User;
import com.lakePop.userService.infrastructure.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    @Value("${spring.jwt.token.lifetime}")
    private Duration lifeTime;

    @Value("${spring.jwt.token.signing-key}")
    private String signingKey;

    SecretKey secretKey;

    @PostConstruct
    private void init() {
        this.secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(signingKey)
        );
    }

    public String generateToken(User user) {
        //data for jwt token
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = user.getRoles().stream().map(role -> "ROLE_" + role.getRoleName()).toList();

        claims.put("type", user.getType());
        claims.put("roles", roles);

        Date issuedAt = new Date();
        Date expireTime = new Date(issuedAt.getTime() + lifeTime.toMillis());

        return Jwts.builder()
                .subject(user.getEmail())
                .claims(claims)
                .issuedAt(issuedAt)
                .expiration(expireTime)
                .signWith(secretKey)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public List<String> getRolesFromToken(String token) {
        Claims claimsFromToken = getClaimsFromToken(token);
        return claimsFromToken.get("roles", List.class);
    }

    public String getUserType(String token) {
        return getClaimsFromToken(token).get("type", String.class);
    }

    public String getPassword(String token) {
        return getClaimsFromToken(token).get("password", String.class);
    }

    public String getUsername(String token) {
        return getClaimsFromToken(token).getSubject();
    }
}
