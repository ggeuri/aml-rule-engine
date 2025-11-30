package main.java.com.amlengine.app;

import java.util.List;
import java.util.Map;

import main.java.com.amlengine.assignment.AlertAssignmentService;
import main.java.com.amlengine.domain.AlertDTO;
import main.java.com.amlengine.domain.AlertStatus;
import main.java.com.amlengine.domain.RiskLevel;
import main.java.com.amlengine.domain.TransactionDTO;
import main.java.com.amlengine.engine.RuleEngine;
import main.java.com.amlengine.io.CsvTransactionLoader;
import main.java.com.amlengine.rule.Rule;
import main.java.com.amlengine.rule.config.RapidWithdrawRuleConfig;
import main.java.com.amlengine.rule.impl.Cu001ForeignCountryRule;
import main.java.com.amlengine.rule.impl.IO003RapidWithdrawRule;
import main.java.com.amlengine.stats.AlertStatsService;

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
        List<String> reviewers = List.of("analyst1", "analyst2","analyst3","analyst4");
        
        int nextIndex = assign.assignRoundRobin(alerts, reviewers, 0 );
        AlertStatsService stats = new AlertStatsService();

        Map<String, Long> byRule = stats.countByRule(alerts);
        Map<RiskLevel, Long> byRisk = stats.countByRiskLevel(alerts);
        Map<AlertStatus, Long> byStatus = stats.countByStatus(alerts);
        Map<String, Long> byReviewer = stats.countByReviewer(alerts);

        System.out.println("\n=== Stats: Rule별 Alert 수 ===");
        for (Map.Entry<String,Long> rule : byRule.entrySet()) {
            System.out.println("[ " + rule.getKey() + " ] 검출건수 : " + rule.getValue());
            
        }

        System.out.println("\n=== Stats: Risk별 Alert 수 ===");
        for (Map.Entry<RiskLevel,Long> rule : byRisk.entrySet()) {
            System.out.println("[ " + rule.getKey() + " ] 검출건수 : " + rule.getValue());
            
        }

        System.out.println("\n=== Stats: Status별 Alert 수 ===");
        for (Map.Entry<AlertStatus,Long> rule : byStatus.entrySet()) {
            System.out.println("[ " + rule.getKey() + " ] 검출건수 : " + rule.getValue());
            
        }

        System.out.println("\n=== Stats: Reviewer별 Alert 수 ===");
        for (Map.Entry<String,Long> rule : byReviewer.entrySet()) {
            System.out.println("[ " + rule.getKey() + " ] 검출건수 : " + rule.getValue());
            
        }


        
        // assign.assignRoundRobin(alerts, reviewers, nextIndex ); 

        for (AlertDTO alert : alerts) {
            System.out.println("[" + alert.getRuleName() + "] " + alert.getMainTxId());
            System.out.println("[배정상태] " + alert.getStatus()+", [담당자] " + alert.getReviewer());
        }
        
        
        
               
    }
    
}
