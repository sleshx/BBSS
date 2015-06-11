package sg.gov.msf.bbss.model.entity;

import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentChildStatus;

/**
 * Created by chuanhe
 */
public class EnrolmentStatus {
    private String appId;
    private EnrolmentChildStatus[] enrolmentChildStatuses;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public EnrolmentChildStatus[] getEnrolmentChildStatuses() {
        return enrolmentChildStatuses;
    }

    public void setEnrolmentChildStatuses(EnrolmentChildStatus[] enrolmentChildStatuses) {
        this.enrolmentChildStatuses = enrolmentChildStatuses;
    }
}
