package com.amlengine.rule.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.amlengine.domain.TransactionDTO;
import com.amlengine.domain.TxTypeHelper;
import com.amlengine.domain.enums.RiskLevel;
import com.amlengine.domain.enums.RuleFrequency;
import com.amlengine.rule.Rule;
import com.amlengine.rule.config.RapidWithdrawRuleConfig;

public class IO003RapidWithdrawRule implements Rule{
    private static final String RULE_ID = "IO-003" ;
    private static final String RULE_NAME = "Rapid Withdraw" ;
    // (windowMinutes, count)를 외부에서 주입받기 위한 설정 객체
    private final RapidWithdrawRuleConfig config;
    
    public IO003RapidWithdrawRule(RapidWithdrawRuleConfig config) {
    if (config.getWindowMinutes() <= 0 || config.getCount() <= 0) {
        throw new IllegalArgumentException("windowMinutes와 count는 0보다 커야 합니다.");
    }
    this.config = config;
    }

    @Override
     public String getRuleId(){
        return RULE_ID;
    }
    @Override
     public String getRuleName(){
        return RULE_NAME;
    }
    
    @Override
    public String getRuleDescription(){
        return "단기간 반복 출금 거래 탐지";
    }

    @Override
    public RiskLevel getRiskLevel() {
        return RiskLevel.MEDIUM;
    }

    @Override
    public RuleFrequency getFrequency(){
        return RuleFrequency.HOURLY;
    }

    @Override
    public boolean match(TransactionDTO tx, List<TransactionDTO> history){
        // 1.	현재 거래가 출금이 아니면 바로 false
        if(!TxTypeHelper.isWithdraw(tx.getTxType())) return false;
        // 2.	현재 거래 시간 now 를 가져온다.
        LocalDateTime now = tx.getTransactedAt();
        // 3.	windowStart = now - windowMinutes
        int windowMinutes = config.getWindowMinutes();
        LocalDateTime windowStart = now.minusMinutes(windowMinutes);

        int withdrawCount = 0;
        long uid = tx.getUid();
        
        // 4.	history
        for (TransactionDTO h : history) {
            LocalDateTime t = h.getTransactedAt();
            //동일유저체크 
            // TODO: 현재는 전체 history에서 uid를 필터링하지만,추후 RuleEngine에서 uid별 history(Map<Long, List<TransactionDTO>>)를 관리하도록 리팩토링!
            if(h.getUid() != uid) continue;
            
            // 그 중 출금 거래만 카운트
            if (t.isBefore(windowStart) || t.isAfter(now)) continue; 
            if(TxTypeHelper.isWithdraw(h.getTxType()))  withdrawCount++;
            

            }
        

        // 5.	withdrawCount >= config.getCount() 이면 true, 아니면 false
        return withdrawCount >= config.getCount();


        


    

    }


    
}
