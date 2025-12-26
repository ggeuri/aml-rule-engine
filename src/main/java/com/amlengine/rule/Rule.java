package com.amlengine.rule;

import java.util.List;

import com.amlengine.domain.RiskLevel;
import com.amlengine.domain.RuleFrequency;
import com.amlengine.domain.TransactionDTO;

public interface Rule {
    String getRuleId();
    String getRuleName();
    String getRuleDescription();
    RiskLevel getRiskLevel();
    RuleFrequency getFrequency();
    

    boolean match(TransactionDTO tx, List<TransactionDTO> history);

}
