package com.amlengine.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.amlengine.domain.TransactionType;

import lombok.Data;

@Data
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

}


