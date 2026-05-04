package com.infoasso.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RnaService {

    private final static Logger logger = LoggerFactory.getLogger(RnaService.class);
    private final String API_URL = "https://entreprise.data.gouv.fr/api/rna/v1/id/";

    private final RestTemplate restTemplate;

    public RnaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, String> getAssociationData(String rnaNumber) {
        if ("W000000000".equals(rnaNumber)) {
            return Map.of("officialName", "ASSOCIATION TEST LOOS (OFFICIEL)",
                    "siret", "12345678900012",
                    "active", "true",
                    "objet", "Objet de test pour l'application Loos"
            );
        }

        try {
            Map<String, Object> response = restTemplate.getForObject(API_URL + rnaNumber, Map.class);

            if (response != null && response.containsKey("association")) {
                Map<String, Object> data = (Map<String, Object>) response.get("association");
                Map<String, Object> identite = (Map<String, Object>) data.get("identite");
                Map<String, Object> activites = (Map<String, Object>) data.get("activites");

                String siren = identite.get("id_siren") != null ? String.valueOf(identite.get("id_siren")) : "N/A";

                return Map.of(
                        "officialName", (String) identite.get("nom"),
                        "objet", (String) activites.get("objet"),
                        "active", String.valueOf(identite.get("active")),
                        "siren", siren
                );
            }
        } catch (HttpClientErrorException.NotFound e) {
            logger.error("RNA {} non trouvé (404)", rnaNumber);
            return null;
        } catch (HttpServerErrorException e) {
            logger.error("L'API Data Gouv est indisponible (500)");
            throw new RuntimeException("Service de vérification indisponible");
        } catch (Exception e) {
            logger.error("Erreur inattendue : {}", e.getMessage());
        return null;
        }
        return null;

    }
}

