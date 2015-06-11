package sg.gov.msf.bbss.model.wizardbase.enrolment;

import sg.gov.msf.bbss.apputils.wizard.WizardBase;
import sg.gov.msf.bbss.model.entity.EnrolmentStatus;

/**
 * Created by chuanhe
 */
public class EnrolmentFormStatus extends WizardBase {
    private EnrolmentStatus[] enrollStatus;

    public EnrolmentStatus[] getEnrollStatus() {
        return enrollStatus;
    }

    public void setEnrollStatus(EnrolmentStatus[] enrollStatus) {
        this.enrollStatus = enrollStatus;
    }
}
