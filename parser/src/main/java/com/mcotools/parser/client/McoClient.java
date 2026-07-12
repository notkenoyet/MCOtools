package com.mcotools.parser.client;

import com.mcotools.parser.dto.BatchDto;
import com.mcotools.parser.dto.DepositDto;
import com.mcotools.parser.dto.RequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Talks to the "mco" microservice (models/repository/controllers project)
 * over plain REST. This is the only coupling point between the two
 * services: the parser never touches the mco database directly.
 */
@Component
public class McoClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public McoClient(RestTemplate restTemplate,
                      @Value("${mco.service.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public void pushRequest(RequestDto dto) {
        restTemplate.postForEntity(baseUrl + "/api/requests", dto, RequestDto.class);
    }

    public void pushBatch(BatchDto dto) {
        restTemplate.postForEntity(baseUrl + "/api/batches", dto, BatchDto.class);
    }

    public void pushDeposit(DepositDto dto) {
        restTemplate.postForEntity(baseUrl + "/api/deposits", dto, DepositDto.class);
    }
}
