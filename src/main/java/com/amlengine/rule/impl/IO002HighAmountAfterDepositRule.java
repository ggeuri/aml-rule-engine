package main.java.com.amlengine.rule.impl;

import java.util.List;

import main.java.com.amlengine.domain.RiskLevel;
import main.java.com.amlengine.domain.RuleFrequency;
import main.java.com.amlengine.domain.TransactionDTO;
import main.java.com.amlengine.domain.TxTypeHelper;
import main.java.com.amlengine.rule.Rule;
import main.java.com.amlengine.rule.config.HighAmountAfterDepositRuleConfig;

public class IO002HighAmountAfterDepositRule implements Rule{
    private static final String RULE_ID = "IO-002";
    private static final String RULE_NAME = "HighAmountAfterDepositRule";
    private final HighAmountAfterDepositRuleConfig config;

    public IO002HighAmountAfterDepositRule(HighAmountAfterDepositRuleConfig config) {
        this.config = config;
    }

    @Override
    public String getRuleId(){
        return RULE_ID; 
    };

    @Override
    public String getRuleName(){
        return RULE_NAME; 
    };

    @Override
    public String getRuleDescription(){
        return "입금 직후 고액 출금";
    };

    @Override
    public RiskLevel getRiskLevel(){
        return RiskLevel.HIGH;
    };

    @Override
    public RuleFrequency getFrequency(){
        return RuleFrequency.DAILY;
    };

    @Override
    public boolean match(TransactionDTO tx, List<TransactionDTO> history){
        if(!TxTypeHelper.isWithdraw(tx.getType())) return false;
        
        long uid = tx.getUid();
        // TODO: 기준 입금 찾고, windowMinutes 안의 출금 누적합 계산하기
        // - config.getPercentThreshold()
        // - config.getAbsoluteThresholdKrw()
        // - config.getWindowMinutes()
        return false;

    };
}
