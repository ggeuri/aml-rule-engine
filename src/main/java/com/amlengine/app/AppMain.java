package main.java.com.amlengine.app;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import main.java.com.amlengine.assignment.AlertAssignmentService;
import main.java.com.amlengine.domain.AlertDTO;
import main.java.com.amlengine.domain.AlertStatus;
import main.java.com.amlengine.domain.RiskLevel;
import main.java.com.amlengine.domain.TransactionDTO;
import main.java.com.amlengine.engine.RuleEngine;
import main.java.com.amlengine.io.AlertJsonExporter;
import main.java.com.amlengine.io.CsvTransactionLoader;
import main.java.com.amlengine.rule.Rule;
import main.java.com.amlengine.rule.config.HighAmountAfterDepositRuleConfig;
import main.java.com.amlengine.rule.config.RapidWithdrawRuleConfig;
import main.java.com.amlengine.rule.impl.Cu001ForeignCountryRule;
import main.java.com.amlengine.rule.impl.IO002HighAmountAfterDepositRule;
import main.java.com.amlengine.rule.impl.IO003RapidWithdrawRule;
import main.java.com.amlengine.stats.AlertStatsService;

public class AppMain {
    public static void main(String[] args) {
        System.out.println("start");

        List<Rule> rules = List.of(
                new Cu001ForeignCountryRule(),
                new IO003RapidWithdrawRule(new RapidWithdrawRuleConfig(10, 3)),
                new IO002HighAmountAfterDepositRule(
                        new HighAmountAfterDepositRuleConfig(BigDecimal.valueOf(0.9), 500000000L, 60 * 24)));

        RuleEngine engine = new RuleEngine(rules);
        // Rule rule = new Cu001ForeignCountryRule();

        List<TransactionDTO> txList = CsvTransactionLoader
                .load("/Users/rimu/Projects/MyProjects/csvTest/tx_boundary_cases.csv");
        List<AlertDTO> alerts = engine.run(txList);
        AlertAssignmentService assign = new AlertAssignmentService();
        List<String> reviewers = List.of("analyst1", "analyst2", "analyst3", "analyst4");

        int nextIndex = assign.assignRoundRobin(alerts, reviewers, 0);
        AlertStatsService stats = new AlertStatsService();

        Map<String, Long> byRule = stats.countByRule(alerts);
        Map<RiskLevel, Long> byRisk = stats.countByRiskLevel(alerts);
        Map<AlertStatus, Long> byStatus = stats.countByStatus(alerts);
        Map<String, Long> byReviewer = stats.countByReviewer(alerts);

        System.out.println("\n=== Stats: Rule별 Alert 수 ===");
        for (Map.Entry<String, Long> rule : byRule.entrySet()) {
            System.out.println("[ " + rule.getKey() + " ] 검출건수 : " + rule.getValue());
        }

        System.out.println("\n=== Stats: Risk별 Alert 수 ===");
        for (Map.Entry<RiskLevel, Long> rule : byRisk.entrySet()) {
            System.out.println("[ " + rule.getKey() + " ] 검출건수 : " + rule.getValue());
        }

        System.out.println("\n=== Stats: Status별 Alert 수 ===");
        for (Map.Entry<AlertStatus, Long> rule : byStatus.entrySet()) {
            System.out.println("[ " + rule.getKey() + " ] 검출건수 : " + rule.getValue());
        }

        System.out.println("\n=== Stats: Reviewer별 Alert 수 ===");
        for (Map.Entry<String, Long> rule : byReviewer.entrySet()) {
            System.out.println("[ " + rule.getKey() + " ] 검출건수 : " + rule.getValue());
        }

        // assign.assignRoundRobin(alerts, reviewers, nextIndex );

        System.out.println("\n=== Alert 상세 리스트 ===");
        for (AlertDTO alert : alerts) {
            String reviewer = (alert.getReviewer() == null) ? "미배정" : alert.getReviewer();

            System.out.println(
                    "[" + alert.getRuleId() + " / " + alert.getRuleName() + "] "
                            + "tx=" + alert.getMainTxId()
                            + ", risk=" + alert.getRiskLevel()
                            + ", status=" + alert.getStatus()
                            + ", reviewer=" + reviewer);
        }

        AlertJsonExporter exporter = new AlertJsonExporter();
        String json = exporter.toJson(alerts);
        System.out.println(json);

        Path outputPath = Paths.get("/Users/rimu/Projects/MyProjects/csvTest/alerts_boundary.json");

        exporter.exportToFile(alerts, outputPath);
        

    }
}
