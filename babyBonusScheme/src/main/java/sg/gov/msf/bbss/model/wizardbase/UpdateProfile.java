package sg.gov.msf.bbss.model.wizardbase;

import sg.gov.msf.bbss.apputils.wizard.WizardBase;
import sg.gov.msf.bbss.model.entity.people.Adult;

/**
 * Created by chuanhe
 */
public class UpdateProfile extends WizardBase {
    private Adult userDetail;

    public UpdateProfile() {
    }

    public UpdateProfile(Adult userDetail) {
        this.userDetail = userDetail;
    }

    public Adult getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(Adult userDetail) {
        this.userDetail = userDetail;
    }
}
