package main.java.com.amlengine.assignment;

import java.time.LocalDateTime;
import java.util.List;

import main.java.com.amlengine.domain.AlertDTO;
import main.java.com.amlengine.domain.AlertStatus;

public class AlertAssignmentService {

    public int assignRoundRobin(List<AlertDTO> alerts, List<String> reviewers, int startIndex){
        if(alerts == null || alerts.isEmpty()) return startIndex;
        if(reviewers == null || reviewers.isEmpty()) return startIndex;

        int reviewerCount = reviewers.size();
        int index = startIndex;

        for (AlertDTO alert : alerts) {
            if(alert.getStatus() != AlertStatus.PENDING) continue;

            String reviewer = reviewers.get(index % reviewerCount);
            index++;

            alert.setReviewer(reviewer);
            alert.setStatus(AlertStatus.ASSIGNED);
            alert.setReviewAssignedAt(LocalDateTime.now()); 
        }

        return index;
    }

}
