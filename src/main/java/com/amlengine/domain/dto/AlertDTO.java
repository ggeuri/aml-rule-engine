package com.amlengine.domain.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.amlengine.domain.enums.AlertStatus;
import com.amlengine.domain.enums.RiskLevel;
import com.amlengine.domain.enums.RuleFrequency;

import lombok.Data;

@Data
public class AlertDTO {
    private Long alertPk;              
    private String alertId; 

    private String mainTxId;   
    private List<String> relatedTxIds; 

    private String ruleId;
    private String ruleName;
    private String ruleDescription;
    private RuleFrequency frequency;

    private LocalDateTime detectedAt;
    private LocalDate detectedDate; 

    private RiskLevel riskLevel;
    private AlertStatus status;

    private String reviewer;        
    private LocalDateTime reviewAssignedAt;

    private long uid;
    private String assetSymbol;
    private long amountKrw;
    private String txType;       
    private String countryCode;

    private LocalDateTime createdAt;   
    private LocalDateTime updatedAt;   
    
}
