package main.java.com.amlengine.app;

import java.util.List;

import main.java.com.amlengine.assignment.AlertAssignmentService;
import main.java.com.amlengine.domain.AlertDTO;
import main.java.com.amlengine.domain.TransactionDTO;
import main.java.com.amlengine.engine.RuleEngine;
import main.java.com.amlengine.io.CsvTransactionLoader;
import main.java.com.amlengine.rule.Rule;
import main.java.com.amlengine.rule.config.RapidWithdrawRuleConfig;
import main.java.com.amlengine.rule.impl.Cu001ForeignCountryRule;
import main.java.com.amlengine.rule.impl.IO003RapidWithdrawRule;

public class AppMain {
    public static void main(String[] args) {
        System.out.println("start");
    
        List<Rule> rules = List.of(
            new Cu001ForeignCountryRule(),
            new IO003RapidWithdrawRule(new RapidWithdrawRuleConfig(10, 3))
        );
        
        RuleEngine engine = new RuleEngine(rules);
        // Rule rule = new Cu001ForeignCountryRule();
        
        List<TransactionDTO> txList = CsvTransactionLoader.load("/Users/rimu/Projects/MyProjects/csvTest/tx_mixed_sample.csv");
        List<AlertDTO> alerts = engine.run(txList);
        AlertAssignmentService assign = new AlertAssignmentService();
        List<String> reviewers = List.of("analysist1", "analysist2","analysist3","analysist4");

        assign.assignRoundRobin(alerts, reviewers );
        for (AlertDTO alert : alerts) {
            System.out.println("[" + alert.getRuleName() + "] " + alert.getMainTxId());
            System.out.println(alert.getStatus()+alert.getReviewer());
        }
        
        
               
    }
    
}
