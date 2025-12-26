package com.amlengine.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDTO {
    private long uid;
    private TransactionType txType;
    private long amountKrw;
    private LocalDateTime transactedAt;
    
    // 코인/매매 전용(assetQuantity*quotePriceKrw = amountKrw)
    private String assetSymbol;
    private BigDecimal assetQuantity;
    private BigDecimal quotePriceKrw;  // 코인 1단위 기준 KRW 시세 
    
    
    // Coin 입출고 전용
    private String fromAddress;
    private String toAddress;
    
    //긴건 뒤로 빼기 
    private String countryCode; //거래 필수조건 아님 뒤로 
    private String txId;
    private String ipAddress;

    public long getUid() {
        return uid;
    }
    public void setUid(long uid) {
        this.uid = uid;
    }
    public TransactionType getTxType() {
        return txType;
    }
    public void setType(TransactionType txType) {
        this.txType = txType;
    }
    public long getAmountKrw() {
        return amountKrw;
    }
    public void setAmountKrw(long amountKrw) {
        this.amountKrw = amountKrw;
    }
    public LocalDateTime getTransactedAt() {
        return transactedAt;
    }
    public void setTransactedAt(LocalDateTime transactedAt) {
        this.transactedAt = transactedAt;
    }
    public String getAssetSymbol() {
        return assetSymbol;
    }
    public void setAssetSymbol(String assetSymbol) {
        this.assetSymbol = assetSymbol;
    }
    public BigDecimal getAssetQuantity() {
        return assetQuantity;
    }
    public void setAssetQuantity(BigDecimal assetQuantity) {
        this.assetQuantity = assetQuantity;
    }
    public BigDecimal getQuotePriceKrw() {
        return quotePriceKrw;
    }
    public void setQuotePriceKrw(BigDecimal quotePriceKrw) {
        this.quotePriceKrw = quotePriceKrw;
    }
    public String getFromAddress() {
        return fromAddress;
    }
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }
    public String getToAddress() {
        return toAddress;
    }
    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }
    public String getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    public String getTxId() {
        return txId;
    }
    public void setTxId(String txId) {
        this.txId = txId;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }


}


