package com.eventhub.app;

import java.util.Date;
import java.util.Map;

public class GoldenGateEvent {
    String opType;
    String table;
    private Date opTimestamp;
    private Date currentTimestamp;
    String pos;
    Map<String, Object> before;
    Map<String, Object> after;

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public Date getOpTimestamp() {
        return opTimestamp;
    }

    public void setOpTimestamp(Date opTimestamp) {
        this.opTimestamp = opTimestamp;
    }

    public Date getCurrentTimestamp() {
        return currentTimestamp;
    }

    public void setCurrentTimestamp(Date currentTimestamp) {
        this.currentTimestamp = currentTimestamp;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public Map<String, Object> getBefore() {
        return before;
    }

    public void setBefore(Map<String, Object> before) {
        this.before = before;
    }

    public Map<String, Object> getAfter() {
        return after;
    }

    public void setAfter(Map<String, Object> after) {
        this.after = after;
    }

    public void setTable(String s) {
    }
}
