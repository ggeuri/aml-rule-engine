package com.amlengine.rule.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import com.amlengine.domain.TransactionDTO;
import com.amlengine.domain.TxTypeHelper;
import com.amlengine.domain.enums.RiskLevel;
import com.amlengine.domain.enums.RuleFrequency;
import com.amlengine.rule.Rule;
import com.amlengine.rule.config.HighAmountAfterDepositRuleConfig;

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
        if(!TxTypeHelper.isWithdraw(tx.getTxType())) return false;
        
        long uid = tx.getUid();
        LocalDateTime end = tx.getTransactedAt(); 
        LocalDateTime start = end.minusMinutes(config.getWindowMinutes());
        long sumDeposit = 0; 
        long sumWithdraw = 0; 

        for (TransactionDTO h : history) {
            if(h == null) continue;
            if(h.getUid() != uid) continue;

            LocalDateTime t = h.getTransactedAt();
            if (t.isBefore(start) || t.isAfter(end)) continue; 
            
            if(TxTypeHelper.isDeposit(h.getTxType())){
                sumDeposit += h.getAmountKrw();
            }
    
            if(TxTypeHelper.isWithdraw(h.getTxType())){
                    sumWithdraw += h.getAmountKrw();
            }
            
        }
        
        if (sumDeposit <= 0) return false;
        if (sumWithdraw < config.getAbsoluteThresholdKrw()) return false;
        BigDecimal withdraw = BigDecimal.valueOf(sumWithdraw);
        BigDecimal deposit = BigDecimal.valueOf(sumDeposit);
        
        BigDecimal ratio = withdraw.divide(deposit, 8, RoundingMode.HALF_UP);

        return ratio.compareTo(config.getPercentThreshold()) >= 0;

    };
}
