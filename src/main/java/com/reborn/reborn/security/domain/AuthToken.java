package com.reborn.reborn.security.domain;

import com.reborn.reborn.member.domain.MemberRole;
import com.reborn.reborn.security.exception.ExpiredTokenException;
import com.reborn.reborn.security.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.Date;

@Slf4j
@Getter
public class AuthToken {

    private final String token;
    private final Key key;

    private static final String AUTHORITIES_KEY = "role";

    public AuthToken(String token, Key key) {
        this.token = token;
        this.key = key;
    }

    public AuthToken(String id, MemberRole role, Date expireDate, Key key) {
        this.key = key;
        this.token = createToken(id, role, expireDate);
    }

    public String createToken(String id, MemberRole memberRole, Date expireDate) {
        return Jwts.builder()
                .setSubject(id)
                .claim(AUTHORITIES_KEY, memberRole.getValue())
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expireDate)
                .compact();
    }

    public boolean validateToken() {
        return this.getTokenClaims() != null;
    }

    public Claims getTokenClaims() {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SecurityException | IllegalArgumentException | UnsupportedJwtException e) {
            throw new InvalidTokenException();
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException();
        } catch (MalformedJwtException e) {
            log.info("guest");
        }
        return null;
    }

}
