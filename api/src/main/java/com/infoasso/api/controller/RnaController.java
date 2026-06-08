package com.infoasso.api.controller;

import com.infoasso.api.service.IRnaService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Data
@RequiredArgsConstructor
@RequestMapping("api/rna")
public class RnaController {

    private static final Logger logger = LoggerFactory.getLogger(RnaController.class);
    private final IRnaService rnaService;

    @GetMapping("/{rnaNumber}")
    ResponseEntity<?> checkRna(@PathVariable String rnaNumber){
        logger.info("GET / rnaNumber : Requete vérification pour le RNA : {}:", rnaNumber);
        Map<String, String> rnaData = rnaService.getAssociationData(rnaNumber);
        if(rnaData == null){
            return ResponseEntity.notFound().build();
        }
        if("false".equalsIgnoreCase(rnaData.get("active"))){
            return ResponseEntity.badRequest().body(Map.of("message", "Cette association est dissoute ou non active."));
        }
        return ResponseEntity.ok(rnaData);
    }
}

