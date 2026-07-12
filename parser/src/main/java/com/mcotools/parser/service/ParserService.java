package com.mcotools.parser.service;

import com.mcotools.parser.client.McoClient;
import com.mcotools.parser.dto.BatchDto;
import com.mcotools.parser.dto.DepositDto;
import com.mcotools.parser.dto.RequestDto;
import com.mcotools.parser.parsing.ParsingResult;
import com.mcotools.parser.parsing.ReportParser;
import org.springframework.stereotype.Service;

@Service
public class ParserService {

    private final ReportParser reportParser;
    private final McoClient mcoClient;

    public ParserService(ReportParser reportParser, McoClient mcoClient) {
        this.reportParser = reportParser;
        this.mcoClient = mcoClient;
    }

    /**
     * Parses the raw report text into Request/Batch/Deposit rows, then
     * pushes each row to the mco service via REST. A failure on one row
     * (e.g. mco service down, duplicate id) is recorded in the summary
     * but does not stop the rest of the batch.
     */
    public ParseSummary parseAndPush(String rawContent) {
        ParsingResult parsed = reportParser.parse(rawContent);
        ParseSummary summary = new ParseSummary();

        summary.setRequestsParsed(parsed.getRequests().size());
        summary.setBatchesParsed(parsed.getBatches().size());
        summary.setDepositsParsed(parsed.getDeposits().size());

        for (RequestDto request : parsed.getRequests()) {
            try {
                mcoClient.pushRequest(request);
                summary.setRequestsPushed(summary.getRequestsPushed() + 1);
            } catch (Exception e) {
                summary.getErrors().add("Request " + request.getReqId() + ": " + e.getMessage());
            }
        }

        for (BatchDto batch : parsed.getBatches()) {
            try {
                mcoClient.pushBatch(batch);
                summary.setBatchesPushed(summary.getBatchesPushed() + 1);
            } catch (Exception e) {
                summary.getErrors().add("Batch " + batch.getBatId() + ": " + e.getMessage());
            }
        }

        for (DepositDto deposit : parsed.getDeposits()) {
            try {
                mcoClient.pushDeposit(deposit);
                summary.setDepositsPushed(summary.getDepositsPushed() + 1);
            } catch (Exception e) {
                summary.getErrors().add("Deposit " + deposit.getDepositId() + ": " + e.getMessage());
            }
        }

        return summary;
    }
}
