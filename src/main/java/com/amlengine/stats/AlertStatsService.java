package com.amlengine.stats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amlengine.domain.AlertDTO;
import com.amlengine.domain.enums.AlertStatus;
import com.amlengine.domain.enums.RiskLevel;

public class AlertStatsService {

    //룰별 집계 
public Map<String, Long> countByRule(List<AlertDTO> alerts){
    Map<String, Long> resultByRule = new HashMap<>();
    if(alerts == null || alerts.isEmpty()) return resultByRule; 

    for (AlertDTO alert : alerts) {
        if(alert == null) continue; 
        String ruleId = alert.getRuleId();
        if(ruleId == null) continue; 

        long count = resultByRule.getOrDefault(ruleId, 0L); 
        resultByRule.put(ruleId,count+1);
    }

    return resultByRule; 
}


// 리스크레벨별 집계  
public Map<RiskLevel, Long> countByRiskLevel(List<AlertDTO> alerts){
     Map<RiskLevel, Long> resultByRisk = new HashMap<>();
     if(alerts == null || alerts.isEmpty()) return resultByRisk; 

     for (AlertDTO alert : alerts) {
        if(alert == null) continue; 
        RiskLevel riskLevel = alert.getRiskLevel(); 
        if(riskLevel == null) continue; 

        long count = resultByRisk.getOrDefault(riskLevel, 0L);
        resultByRisk.put(riskLevel,count+1); 

     }

    return resultByRisk; 
}

// 상태별 집계 
public Map<AlertStatus, Long> countByStatus(List<AlertDTO> alerts){
    Map<AlertStatus, Long> resultByStatus = new HashMap<>();

    if(alerts == null || alerts.isEmpty()) return resultByStatus; 

    for (AlertDTO alert : alerts) {
        if(alert == null) continue; 
        AlertStatus status = alert.getStatus(); 
        if(status == null) continue; 
        
        long count = resultByStatus.getOrDefault(status, 0L); 
        resultByStatus.put(status,count+1);
    }
    return resultByStatus;
}


// 담당자별 집계 
public Map<String, Long> countByReviewer(List<AlertDTO> alerts){
    Map<String, Long>  resultByReviewer = new HashMap<>();
    if(alerts == null || alerts.isEmpty()) return resultByReviewer; 

    for (AlertDTO alert : alerts) {
        if(alert == null) continue; 
        String reviewer = alert.getReviewer(); 
        if(reviewer == null) continue; 

        long count = resultByReviewer.getOrDefault(reviewer, 0L);
        resultByReviewer.put(reviewer, count+1); 
        
    }

    return resultByReviewer; 
}

}
