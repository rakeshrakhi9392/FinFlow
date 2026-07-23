package com.reimbursement.dto.response;

import com.reimbursement.enums.ReimbursementStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Ordered workflow stages for the timeline UI.
 */
public class WorkflowTimelineResponse {

    private List<TimelineStage> stages = new ArrayList<>();

    public List<TimelineStage> getStages() {
        return stages;
    }

    public void setStages(List<TimelineStage> stages) {
        this.stages = stages;
    }

    public static class TimelineStage {
        private String key;
        private String label;
        private ReimbursementStatus status;
        private String state; // completed | current | upcoming | skipped | denied
        private boolean skipped;
        private InstantStamp completedAt;
        private String completedBy;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public ReimbursementStatus getStatus() {
            return status;
        }

        public void setStatus(ReimbursementStatus status) {
            this.status = status;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public boolean isSkipped() {
            return skipped;
        }

        public void setSkipped(boolean skipped) {
            this.skipped = skipped;
        }

        public InstantStamp getCompletedAt() {
            return completedAt;
        }

        public void setCompletedAt(InstantStamp completedAt) {
            this.completedAt = completedAt;
        }

        public String getCompletedBy() {
            return completedBy;
        }

        public void setCompletedBy(String completedBy) {
            this.completedBy = completedBy;
        }
    }

    public static class InstantStamp {
        private String iso;
        private long epochMillis;

        public InstantStamp() {
        }

        public InstantStamp(String iso, long epochMillis) {
            this.iso = iso;
            this.epochMillis = epochMillis;
        }

        public String getIso() {
            return iso;
        }

        public void setIso(String iso) {
            this.iso = iso;
        }

        public long getEpochMillis() {
            return epochMillis;
        }

        public void setEpochMillis(long epochMillis) {
            this.epochMillis = epochMillis;
        }
    }
}
