package main.java.com.amlengine.rule.impl;

import java.util.List;

import main.java.com.amlengine.domain.RiskLevel;
import main.java.com.amlengine.domain.RuleFrequency;
import main.java.com.amlengine.domain.TransactionDTO;
import main.java.com.amlengine.domain.TxTypeHelper;
import main.java.com.amlengine.rule.Rule;

public class Cu001ForeignCountryRule implements Rule{

    private static final String RULE_ID = "CU-001";
    private static final String RULE_NAME = "ForeignCountryRule";

    @Override
    public String getRuleId() {
        return RULE_ID;
    };
    
    @Override
    public String getRuleName() {
        return RULE_NAME;
    };

    @Override
    public String getRuleDescription(){
        return "해외 국가 코드에서 발생하는 출금 거래 탐지";
    };

    @Override
    public RiskLevel getRiskLevel() {
        return RiskLevel.HIGH;
    };

    @Override
    public RuleFrequency getFrequency() {
        return RuleFrequency.REALTIME;
    }

    @Override
    public boolean match(TransactionDTO tx, List<TransactionDTO> history){
        if(!TxTypeHelper.isWithdraw(tx.getType())) return false;
        
        String code = tx.getCountryCode();
        if (code == null || code.isBlank()) return false;  
        
        if(tx.getCountryCode().equalsIgnoreCase("KR")) return false;
        
        return true;

    }
}
