package main.java.com.amlengine.domain;

import java.time.LocalDateTime;
import java.util.List;

public class AlertDTO {
    
    private String alertId;
    private String mainTxId;
    private List<String> relatedTxIds;
    private String ruleId;
    private String ruleName;
    private String ruleDescription;
    private RuleFrequency frequency;
    private LocalDateTime detectedAt;
    private RiskLevel riskLevel;
    private AlertStatus status;
    private String reviewer;
    private LocalDateTime reviewAssignedAt;
    
    public String getAlertId() {
        return alertId;
    }
    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }
    public String getMainTxId() {
        return mainTxId;
    }
    public void setMainTxId(String mainTxId) {
        this.mainTxId = mainTxId;
    }
    public List<String> getRelatedTxIds() {
        return relatedTxIds;
    }
    public void setRelatedTxIds(List<String> relatedTxIds) {
        this.relatedTxIds = relatedTxIds;
    }
    public String getRuleId() {
        return ruleId;
    }
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }
    public String getRuleName() {
        return ruleName;
    }
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
    public String getRuleDescription() {
        return ruleDescription;
    }
    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }
    public RuleFrequency getFrequency() {
        return frequency;
    }
    public void setFrequency(RuleFrequency frequency) {
        this.frequency = frequency;
    }
    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }
    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }
    public RiskLevel getRiskLevel() {
        return riskLevel;
    }
    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }
    public AlertStatus getStatus() {
        return status;
    }
    public void setStatus(AlertStatus status) {
        this.status = status;
    }
    public String getReviewer() {
        return reviewer;
    }
    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }
    public LocalDateTime getReviewAssignedAt() {
        return reviewAssignedAt;
    }
    public void setReviewAssignedAt(LocalDateTime reviewAssignedAt) {
        this.reviewAssignedAt = reviewAssignedAt;
    }



    
}
