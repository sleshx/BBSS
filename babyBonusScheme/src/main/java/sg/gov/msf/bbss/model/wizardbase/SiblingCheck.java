package sg.gov.msf.bbss.model.wizardbase;

import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.wizard.WizardBase;
import sg.gov.msf.bbss.model.entity.childdata.ChildNric;
import sg.gov.msf.bbss.model.entity.people.Child;

/**
 * Created by chuanhe
 */
public class SiblingCheck extends WizardBase {
    private ChildNric childNric;

    public ChildNric getChildNric() {
        return childNric;
    }

    public void setChildNric(ChildNric childNric) {
        this.childNric = childNric;
    }
}