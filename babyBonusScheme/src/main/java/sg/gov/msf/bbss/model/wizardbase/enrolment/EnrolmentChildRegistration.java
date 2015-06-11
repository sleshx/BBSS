package sg.gov.msf.bbss.model.wizardbase.enrolment;

import sg.gov.msf.bbss.apputils.wizard.WizardBase;
import sg.gov.msf.bbss.model.entity.EnrolmentStatus;
import sg.gov.msf.bbss.model.entity.childdata.ChildRegistration;

/**
 * Created by bandaray
 */
public class EnrolmentChildRegistration extends WizardBase {
    private ChildRegistration childRegistration;

    public EnrolmentChildRegistration() {
    }

    public EnrolmentChildRegistration(ChildRegistration childRegistration) {
        this.childRegistration = childRegistration;
    }

    public ChildRegistration getChildRegistration() {
        return childRegistration;
    }

    public void setChildRegistration(ChildRegistration childRegistration) {
        this.childRegistration = childRegistration;
    }

}
