package sg.gov.msf.bbss.logic.server.proxy.interfaces;

import sg.gov.msf.bbss.logic.server.response.ServerResponse;
import sg.gov.msf.bbss.logic.type.EnrolmentAppType;
import sg.gov.msf.bbss.logic.type.SubmitType;
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentForm;
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentFormStatus;

/**
 * Created by bandaray on 12/5/2015.
 */
public interface IEnrolmentProxy {
    EnrolmentForm getPrePopulatedApplication(EnrolmentAppType enrolmentAppType);

    EnrolmentForm getSavedApplication(EnrolmentAppType enrolmentAppType);

    ServerResponse updateEnrolmentApplication(EnrolmentForm enrolmentForm, SubmitType submitType);

    EnrolmentFormStatus getEnrolmentFormStatus();
}
