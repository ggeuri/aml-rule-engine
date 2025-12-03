package main.java.com.amlengine.rule;

import java.util.List;

import main.java.com.amlengine.domain.RiskLevel;
import main.java.com.amlengine.domain.RuleFrequency;
import main.java.com.amlengine.domain.TransactionDTO;

public interface Rule {
    String getRuleId();
    String getRuleName();
    String getRuleDescription();
    RiskLevel getRiskLevel();
    RuleFrequency getFrequency();
    

    boolean match(TransactionDTO tx, List<TransactionDTO> history);

}
