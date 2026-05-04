package com.infoasso.api.security.jwt;

import com.infoasso.api.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final static Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. Extraire le jeton du Header "Authorization"
            String jwt = parseJwt(request);

            // 2. Si le jeton existe et qu'il est valide
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // 3. Récupération de l'email contenu dans le jeton
                String username = jwtUtils.getUserNameFromToken(jwt);

                // 4. Récupération de l'utilisateur complet via la bdd
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 5. Création de l'objet d'authentification Spring
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. Stockage de l'utilistaion dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Impossible d'authentifier l'utilisateur : {}", e.getMessage());
        }

        // 7. La requete peut continuer vers le Controller
        filterChain.doFilter(request, response);
    }

    // Methode utilitaire pour nettoyer le Header Authorization
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}