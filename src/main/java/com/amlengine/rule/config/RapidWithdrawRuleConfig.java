package com.amlengine.rule.config;

public class RapidWithdrawRuleConfig {
    private final int windowMinutes;
    private final int count;

    public RapidWithdrawRuleConfig(int windowMinutes, int count){
        this.windowMinutes = windowMinutes; 
        this.count = count;
    }
    

    public int getWindowMinutes() {
        return windowMinutes;
    }

    public int getCount() {
        return count;
    }
    
}
