package sg.gov.msf.bbss.model.entity.childdata;

import java.util.ArrayList;
import java.util.Date;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.annotation.DisplayNameId;
import sg.gov.msf.bbss.logic.type.ChildRegistrationType;
import sg.gov.msf.bbss.logic.type.YesNoType;
import sg.gov.msf.bbss.model.entity.common.SupportingFile;
import sg.gov.msf.bbss.model.entity.people.Child;

/**
 * Created by bandaray
 */
public class ChildRegistration {

    public static final String FIELD_REG_ESTIMATED_DELIVERY = "estimatedDelivery";
    public static final String FIELD_REG_IS_MARRIED = "isMarried";
    public static final String FIELD_REG_IS_REG_IN_SINGAPORE = "isRegisteredInSingapore";

    /*
     * WARNING : If you refactor any of the property names below,
     *           you have to rename the above constant values as well.
     */

    @DisplayNameId(R.string.label_child)
    private ArrayList<Child> children = new ArrayList<Child>();

    @DisplayNameId(R.string.label_child_reg_type)
    private ChildRegistrationType registrationType;

    @DisplayNameId(R.string.label_child_reg_pre_birth_est_date)
    private Date estimatedDelivery;

    @DisplayNameId(R.string.label_child_reg_is_married)
    private YesNoType isMarried;

    @DisplayNameId(R.string.label_child_reg_is_reg_in_singapore)
    private YesNoType isRegisteredInSingapore;

    private SupportingFile[] supportingFiles = new SupportingFile[0];

    //----------------------------------------------------------------------------------------------

    public ArrayList<Child> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Child> children) {
        this.children = children;
    }

    public ChildRegistrationType getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(ChildRegistrationType registrationType) {
        this.registrationType = registrationType;
    }

    public Date getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(Date estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    public YesNoType getIsMarried() {
        return isMarried;
    }

    public void setMarried(YesNoType isMarried) {
        this.isMarried = isMarried;
    }

    public YesNoType getIsRegisteredInSingapore() {
        return isRegisteredInSingapore;
    }

    public void setRegisteredInSingapore(YesNoType isRegisteredInSingapore) {
        this.isRegisteredInSingapore = isRegisteredInSingapore;
    }

    public SupportingFile[] getSupportingFiles() {
        return supportingFiles;
    }

    public void setSupportingFiles(SupportingFile[] supportingFiles) {
        this.supportingFiles = supportingFiles;
    }

    //----------------------------------------------------------------------------------------------

    public boolean isMarried() {
        return (isMarried == YesNoType.YES);
    }

    public void setIsMarried(boolean isMarried) {
        if (isMarried) {
            this.isMarried = YesNoType.YES;
        } else {
            this.isMarried = YesNoType.NO;
        }
    }

    public boolean isRegisteredInSingapore() {
        return (isRegisteredInSingapore == YesNoType.YES);
    }

    public void setIsRegisteredInSingapore(boolean isRegisteredInSingapore) {
        if (isRegisteredInSingapore) {
            this.isMarried = YesNoType.YES;
        } else {
            this.isMarried = YesNoType.NO;
        }
    }
}
