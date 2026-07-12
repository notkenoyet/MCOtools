package com.mcotools.parser.service;

import java.util.ArrayList;
import java.util.List;

public class ParseSummary {

    private int requestsParsed;
    private int batchesParsed;
    private int depositsParsed;
    private int requestsPushed;
    private int batchesPushed;
    private int depositsPushed;
    private final List<String> errors = new ArrayList<>();

    public int getRequestsParsed() {
        return requestsParsed;
    }

    public void setRequestsParsed(int requestsParsed) {
        this.requestsParsed = requestsParsed;
    }

    public int getBatchesParsed() {
        return batchesParsed;
    }

    public void setBatchesParsed(int batchesParsed) {
        this.batchesParsed = batchesParsed;
    }

    public int getDepositsParsed() {
        return depositsParsed;
    }

    public void setDepositsParsed(int depositsParsed) {
        this.depositsParsed = depositsParsed;
    }

    public int getRequestsPushed() {
        return requestsPushed;
    }

    public void setRequestsPushed(int requestsPushed) {
        this.requestsPushed = requestsPushed;
    }

    public int getBatchesPushed() {
        return batchesPushed;
    }

    public void setBatchesPushed(int batchesPushed) {
        this.batchesPushed = batchesPushed;
    }

    public int getDepositsPushed() {
        return depositsPushed;
    }

    public void setDepositsPushed(int depositsPushed) {
        this.depositsPushed = depositsPushed;
    }

    public List<String> getErrors() {
        return errors;
    }
}
