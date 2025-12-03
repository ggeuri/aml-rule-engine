package main.java.com.amlengine.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import main.java.com.amlengine.domain.AlertDTO;

public class AlertJsonExporter {

   
    public String toJson(List<AlertDTO> alerts) {
        if (alerts == null || alerts.isEmpty()) {
            return "[]";
        }

        StringBuilder alertJson = new StringBuilder();

        alertJson.append("[");

        for (int i = 0; i < alerts.size(); i++) {

            AlertDTO alert = alerts.get(i);

            if (i > 0) {
                alertJson.append(",");
            }

            alertJson.append("{");

            alertJson.append("\"alertId\":\"").append(alert.getAlertId()).append("\",");
            alertJson.append("\"mainTxId\":\"").append(alert.getMainTxId()).append("\",");

            
            List<String> related = alert.getRelatedTxIds();
            alertJson.append("\"relatedTxIds\":");
            if (related == null || related.isEmpty()) {
                alertJson.append("[]");
            } else {
                alertJson.append("[");
                for (int j = 0; j < related.size(); j++) {
                    if (j > 0)
                        alertJson.append(",");
                    alertJson.append("\"").append(related.get(j)).append("\"");
                }
                alertJson.append("]");
            }
            alertJson.append(",");

            alertJson.append("\"ruleId\":\"").append(alert.getRuleId()).append("\",");
            alertJson.append("\"ruleName\":\"").append(alert.getRuleName()).append("\",");
            alertJson.append("\"ruleDescription\":\"").append(alert.getRuleDescription()).append("\",");
            alertJson.append("\"frequency\":\"").append(alert.getFrequency()).append("\",");
            alertJson.append("\"detectedAt\":\"").append(alert.getDetectedAt()).append("\",");
            alertJson.append("\"riskLevel\":\"").append(alert.getRiskLevel()).append("\",");
            alertJson.append("\"status\":\"").append(alert.getStatus()).append("\",");

            
            alertJson.append("\"reviewer\":");
            if (alert.getReviewer() == null) {
                alertJson.append("null,");
            } else {
                alertJson.append("\"").append(alert.getReviewer()).append("\",");
            }

            
            alertJson.append("\"reviewAssignedAt\":");
            if (alert.getReviewAssignedAt() == null) {
                alertJson.append("null");
            } else {
                alertJson.append("\"").append(alert.getReviewAssignedAt()).append("\"");
            }

            alertJson.append("}");
        }

        alertJson.append("]");

        return alertJson.toString();

    }

    public void exportToFile(List<AlertDTO> alerts, Path outputPath) {

        String json = toJson(alerts);
        try {
            Files.writeString(outputPath, json, StandardCharsets.UTF_8);
            System.out.println("Alert JSON saved to: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();;
            System.out.println("exportToFile error");
        }

    }

}
