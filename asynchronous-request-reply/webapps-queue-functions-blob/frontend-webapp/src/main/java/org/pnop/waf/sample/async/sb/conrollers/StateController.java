package org.pnop.waf.sample.async.sb.conrollers;

import org.pnop.waf.sample.async.sb.services.BlobService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class StateController {

    private BlobService service;

    public StateController(BlobService service) {
        this.service = service;
    }

    @GetMapping("/api/state/{id}")
    public ResponseEntity<?> checkState(@PathVariable String id) {

        if (service.exists(id)) {
            // rewrite url (docker)
            var uri = service.getUrl(id);
            var builder = UriComponentsBuilder.fromUri(uri);
            builder.host("localhost");
            var headers = new HttpHeaders();
            headers.setLocation(builder.build().toUri());

            log.info("org = {}", uri.toString());
            log.info("rew = {}", headers.getLocation().toString());
            
            var response = new ResponseEntity<>(headers, HttpStatus.FOUND);
            return response;
        }

        return ResponseEntity
            .accepted()
            .build();
    }
}