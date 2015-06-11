package sg.gov.msf.bbss.model.entity.people;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.annotation.DisplayHeaderNameId;
import sg.gov.msf.bbss.apputils.annotation.DisplayNameId;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.type.YesNoType;
import sg.gov.msf.bbss.model.entity.common.SupportingFile;

/**
 * Created by bandaray
 */
@DisplayHeaderNameId(R.string.label_child)
public class Child extends Person {

    public static final String FIELD_BIRTH_CERT_NO = "birthCert";
    public static final String FIELD_IS_BORN_OVERSEAS = "isBornOnOverseas";
    public static final String FIELD_DATE_OF_ADOPTION = "dateOfAdoption";

    /*
     * WARNING : If you refactor any of the property names below,
     *           you have to rename the above constant values as well.
     */


    public static final String[] SERIAL_NAMES = new String[] {
            SerializedNames.SN_PERSON_ID,
            SerializedNames.SN_PERSON_NRIC,
            SerializedNames.SN_PERSON_NAME,
            SerializedNames.SN_CHILD_BIRTH_CERT_NO,
            SerializedNames.SN_PERSON_BIRTHDAY,
            SerializedNames.SN_CHILD_IS_BORN_OVERSEAS
    };

    @DisplayNameId(R.string.label_child_birth_cert_no)
    private String birthCert;

    @DisplayNameId(R.string.label_child_current_birth_order)
    private int birthOrder;

    @DisplayNameId(R.string.label_child_born_on_overseas)
    private YesNoType isBornOnOverseas;

    @DisplayNameId(R.string.label_child_date_of_adoption)
    private YesNoType dateOfAdoption;

    private SupportingFile[] supportingFiles = new SupportingFile[0];

    //----------------------------------------------------------------------------------------------

    public String getBirthCertNo() {
        return birthCert;
    }

    public void setBirthCertNo(String birthCertNo) {
        this.birthCert = birthCertNo;
    }

    public int getBirthOrder() {
        return birthOrder;
    }

    public void setBirthOrder(int birthOrder) {
        this.birthOrder = birthOrder;
    }

    public YesNoType isBornOnOverseas() {
        return isBornOnOverseas;
    }

    public void setBornOnOverseas(YesNoType isBornOnOverseas) {
        this.isBornOnOverseas = isBornOnOverseas;
    }

    public YesNoType getDateOfAdoption() {
        return dateOfAdoption;
    }

    public void setDateOfAdoption(YesNoType dateOfAdoption) {
        this.dateOfAdoption = dateOfAdoption;
    }

    public SupportingFile[] getSupportingFiles() {
        return supportingFiles;
    }

    public void setSupportingFiles(SupportingFile[] supportingFiles) {
        this.supportingFiles = supportingFiles;
    }
}
