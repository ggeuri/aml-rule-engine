package com.amlengine.engine;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.amlengine.domain.AlertDTO;
import com.amlengine.domain.AlertStatus;
import com.amlengine.domain.RiskLevel;
import com.amlengine.domain.RuleFrequency;
import com.amlengine.domain.TransactionDTO;
import com.amlengine.rule.Rule;

public class RuleEngine {
    private final List<Rule> rules ;
    private final List<AlertDTO> alerts = new ArrayList<>();
    private final Set<String> alertKeys = new HashSet<>();

    public RuleEngine(List<Rule> rules){
        this.rules = rules;
    }
    
    private AlertDTO createAlert(TransactionDTO tx, Rule rule){
        AlertDTO alert = new AlertDTO();
        
        String alertId = rule.getRuleId() + ":" + tx.getTxId();
        String mainTxId = tx.getTxId();
        List<String> relatedTxIds = List.of(tx.getTxId());
        String ruleId = rule.getRuleId();
        String ruleName = rule.getRuleName();
        String ruleDescription = rule.getRuleDescription();
        RuleFrequency frequency = rule.getFrequency();
        LocalDateTime detectedAt = tx.getTransactedAt();  // 단순화: 거래시각 = 탐지시각
        RiskLevel riskLevel = rule.getRiskLevel();
        AlertStatus status = AlertStatus.PENDING;
        String reviewer = null;
        LocalDateTime reviewAssignedAt = null ;
        
        
        
        alert.setAlertId(alertId);
        alert.setMainTxId(mainTxId);
        alert.setRelatedTxIds(relatedTxIds);
        alert.setRuleId(ruleId);
        alert.setRuleName(ruleName);
        alert.setRuleDescription(ruleDescription);
        alert.setFrequency(frequency);
        alert.setDetectedAt(detectedAt);
        alert.setRiskLevel(riskLevel);
        alert.setStatus(status);
        alert.setReviewer(reviewer);
        alert.setReviewAssignedAt(reviewAssignedAt);
        alert.setUid(tx.getUid());
        alert.setAmountKrw(tx.getAmountKrw());
        alert.setTxType(tx.getTxType().name());      // enum이면 .name()
        alert.setCountryCode(tx.getCountryCode());
        alert.setAssetSymbol(tx.getAssetSymbol());
        
        return alert ; 
        
    }
    
    public List<AlertDTO> run(List<TransactionDTO> txList) {
        txList.sort((a,b) -> a.getTransactedAt().compareTo(b.getTransactedAt()));
        alerts.clear();
        alertKeys.clear();
        List<TransactionDTO> history = new ArrayList<>();
        
        for (TransactionDTO tx : txList) {
            history.add(tx);
            
            for (Rule rule : rules) {
                if(!rule.match(tx, history)) continue; 
                String key = rule.getRuleId() + ":" + tx.getTxId();
                if(alertKeys.contains(key)) continue; 
                alertKeys.add(key);
                AlertDTO alert = createAlert(tx, rule);
                alerts.add(alert);
                
            }
                
            }
            
            return alerts;
        }


    }
    

