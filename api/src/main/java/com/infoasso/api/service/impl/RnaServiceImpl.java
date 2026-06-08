package com.infoasso.api.service.impl;

import com.infoasso.api.service.IRnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class RnaServiceImpl implements IRnaService {

    private final static Logger logger = LoggerFactory.getLogger(RnaServiceImpl.class);
    private final String API_URL = "https://recherche-entreprises.api.gouv.fr/search?q=";

    private final RestTemplate restTemplate;

    public RnaServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Map<String, String> getAssociationData(String rnaNumber) {
        // 1. Bouchon de Test
        if ("W000000000".equals(rnaNumber)) {
            return Map.of(
                    "officialName", "ASSOCIATION TEST LOOS (OFFICIEL)",
                    "active", "true",
                    "objet", "Objet de test pour l'application Loos"
            );
        }

        try {
            // L'API renvoie un Map classique
            Map<String, Object> response = restTemplate.getForObject(API_URL + rnaNumber, Map.class);

            // 💡 La nouvelle API renvoie une liste dans la clé "results"
            if (response != null && response.containsKey("results")) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

                // Si la liste est vide, c'est que le RNA n'existe pas
                if (results.isEmpty()) {
                    logger.warn("Aucun résultat trouvé pour le RNA {}", rnaNumber);
                    return null;
                }

                // On récupère le premier résultat trouvé
                Map<String, Object> checkResult = results.get(0);

                // Extraction du nom officiel (nom_complet dans cette API)
                String officialName = (String) checkResult.get("nom_complet");

                // 💡 L'API de recherche entreprise ne donne pas l'état d'activité directement à la racine,
                // mais si elle retourne l'association via son RNA, elle est considérée comme valide.
                // Par sécurité, on peut chercher une clé d'activité ou forcer "true" car les dissoutes n'apparaissent pas ainsi.
                String isActive = "true";

                return Map.of(
                        "officialName", officialName != null ? officialName : "Nom inconnu",
                        "objet", "Consultable sur le JOAFE", // L'API recherche-entreprises ne fournit pas l'objet complet textuel par défaut
                        "active", isActive
                );
            }
        } catch (HttpClientErrorException.NotFound e) {
            logger.error("RNA {} non trouvé (404)", rnaNumber);
            return null;
        } catch (HttpServerErrorException e) {
            logger.error("L'API Data Gouv est indisponible (500)");
            throw new RuntimeException("Service de vérification indisponible");
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la lecture du RNA : {}", e.getMessage(), e);
            return null;
        }
        return null;
    }
}