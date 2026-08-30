package com.mcotools.parser.client;

import com.mcotools.parser.dto.BatchDto;
import com.mcotools.parser.dto.DepositDto;
import com.mcotools.parser.dto.RequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Talks to the "mco" microservice (models/repository/controllers project)
 * over plain REST. This is the only coupling point between the two
 * services: the parser never touches the mco database directly.
 */
@Component
public class McoClient {

    private static final Logger log = LoggerFactory.getLogger(McoClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public McoClient(RestTemplate restTemplate,
                      @Value("${mco.service.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public void pushRequest(RequestDto dto) {
        log.debug("Envoi du request {} vers {}/api/requests", dto.getReqId(), baseUrl);
        try {
            restTemplate.postForEntity(baseUrl + "/api/requests", dto, RequestDto.class);
        } catch (RestClientException e) {
            log.error("Erreur reseau lors de l'envoi du request {} vers mco ({}): {}", dto.getReqId(), baseUrl, e.getMessage(), e);
            throw e;
        }
    }

    public void pushBatch(BatchDto dto) {
        log.debug("Envoi du batch {} vers {}/api/batches", dto.getBatId(), baseUrl);
        try {
            restTemplate.postForEntity(baseUrl + "/api/batches", dto, BatchDto.class);
        } catch (RestClientException e) {
            log.error("Erreur reseau lors de l'envoi du batch {} vers mco ({}): {}", dto.getBatId(), baseUrl, e.getMessage(), e);
            throw e;
        }
    }

    public void pushDeposit(DepositDto dto) {
        log.debug("Envoi du deposit {} vers {}/api/deposits", dto.getDepositId(), baseUrl);
        try {
            restTemplate.postForEntity(baseUrl + "/api/deposits", dto, DepositDto.class);
        } catch (RestClientException e) {
            log.error("Erreur reseau lors de l'envoi du deposit {} vers mco ({}): {}", dto.getDepositId(), baseUrl, e.getMessage(), e);
            throw e;
        }
    }
}
