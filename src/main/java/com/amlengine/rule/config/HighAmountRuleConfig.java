package com.amlengine.rule.config;

public class HighAmountRuleConfig {
    private final long thresholdKrw;

    public HighAmountRuleConfig(long thresholdKrw){
        this.thresholdKrw = thresholdKrw;
    };

    public long getHighAmountRuleThreshold(){
        return thresholdKrw;
    }
}
