package com.infoasso.api.security.jwt;

import com.infoasso.api.model.User;
import com.infoasso.api.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${infoasso.app.jwtSecret}")
    private String jwtSecret;

    @Value("${infoasso.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Autowired
    private UserRepository userRepository;

    // 1. Générer la clé de signature
    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // 2. Créer le jeton
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        System.out.println(userPrincipal.getUsername());
        User user = userRepository.findByEmail(userPrincipal.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("id", user.getId())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }
    // 3. Extraire le nom du User depuis le jeton
    public String getUserNameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 4. Verifier si le jeton est valide
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Jeton JWT invalide: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Le jeton JWT est expiré: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Le jeton JWT n'est pas supporté: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("La chaîne de caractères JWT est vide: {}", e.getMessage());
        }
        return false;
        }
    }