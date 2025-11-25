package main.java.com.amlengine.assignment;

import java.time.LocalDateTime;
import java.util.List;

import main.java.com.amlengine.domain.AlertDTO;
import main.java.com.amlengine.domain.AlertStatus;

public class AlertAssignmentService {

    public void assignRoundRobin(List<AlertDTO> alerts, List<String> reviewers ){
        if(alerts == null || alerts.isEmpty()) return;
        if(reviewers == null || reviewers.isEmpty()) return;

        int reviewerCount = reviewers.size();
        int index = 0 ; 

        for (AlertDTO alert : alerts) {
            if(alert.getStatus() != AlertStatus.PENDING) continue;

            String reviewer = reviewers.get(index % reviewerCount);
            index++;

            alert.setReviewer(reviewer);
            alert.setStatus(AlertStatus.IN_REVIEW);
            alert.setReviewAssignedAt(LocalDateTime.now());

            
        }



    }



    
}
