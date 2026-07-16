package com.infoasso.api.controller;

import com.infoasso.api.model.DayOfWeek;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/enums")
public class EnumController {

    private static final Logger logger = LoggerFactory.getLogger(EnumController.class);

    @GetMapping("/daysOfWeek")
    public ResponseEntity<DayOfWeek[]> getDaysOfWeek() {
        logger.info("GET: / : Request received for days of week");
        DayOfWeek[] values = DayOfWeek.values();
        logger.info("GET: / : Response 200 OK : {} days found.", values.length);
        return ResponseEntity.ok(values);
    }

}
