package com.mcotools.treatment.client;

import java.util.Arrays;
import java.util.List;
import com.mcotools.treatment.dto.BatchDto;
import com.mcotools.treatment.dto.DepositDto;
import com.mcotools.treatment.dto.RequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * Talks to the "mco" microservice (models/repository/controllers project)
 * over plain REST, to fetch the Request/Batch/Deposit records that
 * treatment needs to work on.
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

    public Optional<RequestDto> fetchRequest(Long reqId) {
        log.debug("Recuperation du request {} depuis {}/api/requests", reqId, baseUrl);
        try {
            RequestDto dto = restTemplate.getForObject(baseUrl + "/api/requests/" + reqId, RequestDto.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Request {} introuvable dans mco", reqId);
            return Optional.empty();
        } catch (RestClientException e) {
            log.error("Erreur reseau lors de la recuperation du request {} depuis mco ({}): {}", reqId, baseUrl, e.getMessage(), e);
            throw e;
        }
    }

    public Optional<BatchDto> fetchBatch(Long batId) {
        log.debug("Recuperation du batch {} depuis {}/api/batches", batId, baseUrl);
        try {
            BatchDto dto = restTemplate.getForObject(baseUrl + "/api/batches/" + batId, BatchDto.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Batch {} introuvable dans mco", batId);
            return Optional.empty();
        } catch (RestClientException e) {
            log.error("Erreur reseau lors de la recuperation du batch {} depuis mco ({}): {}", batId, baseUrl, e.getMessage(), e);
            throw e;
        }
    }

    public Optional<DepositDto> fetchDeposit(String depositId) {
        log.debug("Recuperation du deposit {} depuis {}/api/deposits", depositId, baseUrl);
        try {
            DepositDto dto = restTemplate.getForObject(baseUrl + "/api/deposits/" + depositId, DepositDto.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Deposit {} introuvable dans mco", depositId);
            return Optional.empty();
        } catch (RestClientException e) {
            log.error("Erreur reseau lors de la recuperation du deposit {} depuis mco ({}): {}", depositId, baseUrl, e.getMessage(), e);
            throw e;
        }
        
    }
        public List<RequestDto> fetchAllRequests() {
        log.debug("Recuperation de tous les requests depuis {}/api/requests", baseUrl);
        RequestDto[] all = restTemplate.getForObject(baseUrl + "/api/requests", RequestDto[].class);
        return all == null ? List.of() : Arrays.asList(all);
    }

    public List<BatchDto> fetchAllBatches() {
        log.debug("Recuperation de tous les batches depuis {}/api/batches", baseUrl);
        BatchDto[] all = restTemplate.getForObject(baseUrl + "/api/batches", BatchDto[].class);
        return all == null ? List.of() : Arrays.asList(all);
    }

    public List<DepositDto> fetchAllDeposits() {
        log.debug("Recuperation de tous les deposits depuis {}/api/deposits", baseUrl);
        DepositDto[] all = restTemplate.getForObject(baseUrl + "/api/deposits", DepositDto[].class);
        return all == null ? List.of() : Arrays.asList(all);
    }

    /** Requests dont le champ source_file correspond au fichier .txt d'origine donne. */
    public List<RequestDto> fetchRequestsBySourceFile(String sourceFile) {
        log.debug("Recuperation des requests du fichier source {} depuis {}/api/requests/source", sourceFile, baseUrl);
        RequestDto[] all = restTemplate.getForObject(baseUrl + "/api/requests/source/{sourceFile}", RequestDto[].class, sourceFile);
        return all == null ? List.of() : Arrays.asList(all);
    }

    /** Batches dont le champ source_file correspond au fichier .txt d'origine donne. */
    public List<BatchDto> fetchBatchesBySourceFile(String sourceFile) {
        log.debug("Recuperation des batches du fichier source {} depuis {}/api/batches/source", sourceFile, baseUrl);
        BatchDto[] all = restTemplate.getForObject(baseUrl + "/api/batches/source/{sourceFile}", BatchDto[].class, sourceFile);
        return all == null ? List.of() : Arrays.asList(all);
    }

    /** Deposits dont le champ source_file correspond au fichier .txt d'origine donne. */
    public List<DepositDto> fetchDepositsBySourceFile(String sourceFile) {
        log.debug("Recuperation des deposits du fichier source {} depuis {}/api/deposits/source", sourceFile, baseUrl);
        DepositDto[] all = restTemplate.getForObject(baseUrl + "/api/deposits/source/{sourceFile}", DepositDto[].class, sourceFile);
        return all == null ? List.of() : Arrays.asList(all);
    }
}
