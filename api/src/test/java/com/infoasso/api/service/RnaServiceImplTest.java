package com.infoasso.api.service;

import com.infoasso.api.service.impl.RnaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(RnaServiceImpl.class)
@Import(RestTemplate.class)
class RnaServiceImplTest {

    @Autowired
    private RnaServiceImpl rnaService;

    @Autowired
    private RestTemplate restTemplate;


    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        // Initialisation du serveur mocké attaché au RestTemplate du service
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void shouldReturnAssociationDataWhenApiSucceeds() {
        // Given : une réponse JSON simulée de l'API gouv.fr
        String jsonResponse = """
            {
              "results": [
                {
                  "nom_complet": "ASSOCIATION SPORTIVE LOOS"
                }
              ]
            }
            """;

        mockServer.expect(requestTo("https://recherche-entreprises.api.gouv.fr/search?q=W591234567"))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // When
        Map<String, String> result = rnaService.getAssociationData("W591234567");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("officialName")).isEqualTo("ASSOCIATION SPORTIVE LOOS");
        assertThat(result.get("active")).isEqualTo("true");
    }

    @Test
    void shouldReturnNullWhenAssociationNotFound404() {
        // Given : l'API répond un 404 Not Found
        mockServer.expect(requestTo("https://recherche-entreprises.api.gouv.fr/search?q=W999999999"))
                .andRespond(withStatus(org.springframework.http.HttpStatus.NOT_FOUND));

        // When
        Map<String, String> result = rnaService.getAssociationData("W999999999");

        // Then
        assertThat(result).isNull();
    }

    @Test
    void shouldThrowExceptionWhenApiFails500() {
        // Given : l'API externe plante (500 Internal Server Error)
        mockServer.expect(requestTo("https://recherche-entreprises.api.gouv.fr/search?q=W591234567"))
                .andRespond(withServerError());

        // When & Then : le service doit lever une RuntimeException comme prévu dans ton catch
        assertThatThrownBy(() -> rnaService.getAssociationData("W591234567"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Service de vérification indisponible");
    }

    @Test
    void shouldReturnBypassDataForTestRna() {
        // Cas particulier de ton code ("W000000000") qui ne tape pas l'API externe
        Map<String, String> result = rnaService.getAssociationData("W000000000");

        assertThat(result).isNotNull();
        assertThat(result.get("officialName")).isEqualTo("ASSOCIATION TEST LOOS (OFFICIEL)");
    }
}