package com.mcotools.parser.parsing;

import com.mcotools.parser.dto.BatchDto;
import com.mcotools.parser.dto.DepositDto;
import com.mcotools.parser.dto.RequestDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds every row extracted from a raw report dump, grouped by target table.
 */
public class ParsingResult {

    private final List<RequestDto> requests = new ArrayList<>();
    private final List<BatchDto> batches = new ArrayList<>();
    private final List<DepositDto> deposits = new ArrayList<>();

    public List<RequestDto> getRequests() {
        return requests;
    }

    public List<BatchDto> getBatches() {
        return batches;
    }

    public List<DepositDto> getDeposits() {
        return deposits;
    }
}
