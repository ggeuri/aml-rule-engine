package com.amlengine.generator;

import java.time.LocalDateTime;
import java.util.Map;

import com.amlengine.domain.TransactionType;

public class GeneratorConfig {
    private int userCount; 
    private int txPerUser;
    private LocalDateTime startAt ; 
    private int durationMinutes ; 
    private Map<TransactionType, Double> typeRatio;
    private double foreignCountryRatio;

    public GeneratorConfig(){
        this.userCount = 10;          
        this.txPerUser = 20;          
        this.startAt = LocalDateTime.now();
        this.durationMinutes = 60;    
        this.foreignCountryRatio = 0.1; 
        
        this.typeRatio = Map.of(
            TransactionType.KRW_DEPOSIT, 0.25,
            TransactionType.KRW_WITHDRAW, 0.25,
            TransactionType.COIN_DEPOSIT, 0.2,
            TransactionType.COIN_WITHDRAW, 0.2,
            TransactionType.TRADE_BUY, 0.05,
            TransactionType.TRADE_SELL, 0.05
        );
    }

    public int getUserCount() {
        return userCount;
    }
    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }
    public int getTxPerUser() {
        return txPerUser;
    }
    public void setTxPerUser(int txPerUser) {
        this.txPerUser = txPerUser;
    }
    public LocalDateTime getStartAt() {
        return startAt;
    }
    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }
    public int getDurationMinutes() {
        return durationMinutes;
    }
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    public Map<TransactionType, Double> getTypeRatio() {
        return typeRatio;
    }
    public void setTypeRatio(Map<TransactionType, Double> typeRatio) {
        this.typeRatio = typeRatio;
    }
    public double getForeignCountryRatio() {
        return foreignCountryRatio;
    }
    public void setForeignCountryRatio(double foreignCountryRatio) {
        this.foreignCountryRatio = foreignCountryRatio;
    } 


    
}
