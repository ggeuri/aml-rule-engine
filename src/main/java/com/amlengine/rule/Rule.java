package com.amlengine.rule;

import java.util.List;

import com.amlengine.domain.TransactionDTO;
import com.amlengine.domain.enums.RiskLevel;
import com.amlengine.domain.enums.RuleFrequency;

public interface Rule {
    String getRuleId();
    String getRuleName();
    String getRuleDescription();
    RiskLevel getRiskLevel();
    RuleFrequency getFrequency();
    

    boolean match(TransactionDTO tx, List<TransactionDTO> history);

}
