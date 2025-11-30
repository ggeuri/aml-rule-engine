package main.java.com.amlengine.engine;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.java.com.amlengine.domain.AlertDTO;
import main.java.com.amlengine.domain.AlertStatus;
import main.java.com.amlengine.domain.RiskLevel;
import main.java.com.amlengine.domain.RuleFrequency;
import main.java.com.amlengine.domain.TransactionDTO;
import main.java.com.amlengine.rule.Rule;

public class RuleEngine {
    private final List<Rule> rules ;
    private final List<AlertDTO> alerts = new ArrayList<>();
    private final Set<String> alertKeys = new HashSet<>();

    public RuleEngine(List<Rule> rules){
        this.rules = rules;
    };
    
    private AlertDTO createAlert(TransactionDTO tx, Rule rule){
        AlertDTO alert = new AlertDTO();
        
        String alertId = rule.getRuleName() + ":" + tx.getTxId();
        String mainTxId = tx.getTxId();
        List<String> relatedTxIds = List.of(tx.getTxId());
        String ruleId = rule.getRuleId();
        String ruleName = rule.getRuleName();
        String ruleDescription = rule.getRuleDescription();
        RuleFrequency frequency = rule.getFrequency();
        LocalDateTime detectedAt = LocalDateTime.now() ;
        RiskLevel riskLevel = rule.getRiskLevel();
        AlertStatus status = AlertStatus.PENDING;
        String reviewer = null;
        LocalDateTime reviewAssignedAt = null ;
        
        alert.setAlertId(alertId);
        alert.setMainTxId(mainTxId);
        alert.setRelatedTxIds(relatedTxIds);
        alert.setRuleName(ruleId);
        alert.setRuleName(ruleName);
        alert.setRuleDescription(ruleDescription);
        alert.setFrequency(frequency);
        alert.setDetectedAt(detectedAt);
        alert.setRiskLevel(riskLevel);
        alert.setStatus(status);
        alert.setReviewer(reviewer);
        alert.setReviewAssignedAt(reviewAssignedAt);
        
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
                String key = rule.getRuleName()+":"+tx.getTxId();
                if(alertKeys.contains(key)) continue; 
                alertKeys.add(key);
                AlertDTO alert = createAlert(tx, rule);
                alerts.add(alert);
                
            }
                
            }
            
            return alerts;
        }


    }
    

