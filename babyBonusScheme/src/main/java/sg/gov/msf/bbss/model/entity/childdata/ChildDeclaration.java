package sg.gov.msf.bbss.model.entity.childdata;

import java.util.Date;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.annotation.DisplayNameId;
import sg.gov.msf.bbss.logic.type.ChildDeclarationType;
import sg.gov.msf.bbss.model.entity.common.SupportingFile;
import sg.gov.msf.bbss.model.entity.masterdata.GenericDataItem;
import sg.gov.msf.bbss.model.entity.people.Child;

/**
 * Created by bandaray
 */
public class ChildDeclaration {

    public static final String FIELD_DEC_TYPE = "declarationType";
    public static final String FIELD_DEC_ADOPTION_DATE = "dateOfAdoption";
    public static final String FIELD_DEC_ADOPTION_ORDER_DATE = "dateOfAdoptionOrder";
    public static final String FIELD_DEC_BIRTH_COUNTRY = "countryOfBirth";
    public static final String FIELD_DEC_CITIZENSHIP_NO = "citizenshipNo";
    public static final String FIELD_DEC_DECEASED_DATE = "deceasedDate";
    public static final String FIELD_DEC_REMARKS = "remarks";

    /*
     * WARNING : If you refactor any of the property names below,
     *           you have to rename the above constant values as well.
     */

    @DisplayNameId(R.string.label_child)
    private Child child;

    @DisplayNameId(R.string.label_child_dec_type)
    private ChildDeclarationType declarationType;

    @DisplayNameId(R.string.label_child_dec_date_of_adoption)
    private Date dateOfAdoption;

    @DisplayNameId(R.string.label_child_dec_date_of_adoption_order)
    private Date dateOfAdoptionOrder;

    @DisplayNameId(R.string.label_child_dec_country_of_birth)
    private GenericDataItem countryOfBirth;

    @DisplayNameId(R.string.label_child_dec_singapore_citizenship_no)
    private String citizenshipNo;

    @DisplayNameId(R.string.label_child_dec_date_deceased)
    private Date deceasedDate;

    @DisplayNameId(R.string.label_child_dec_remarks)
    private String remarks;

    private SupportingFile[] supportingFiles = new SupportingFile[0];

    //----------------------------------------------------------------------------------------------

    public ChildDeclaration(ChildDeclarationType declarationType) {
        this.declarationType = declarationType;
    }

    //----------------------------------------------------------------------------------------------

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public ChildDeclarationType getDeclarationType() {
        return declarationType;
    }

    public void setDeclarationType(ChildDeclarationType declarationType) {
        this.declarationType = declarationType;
    }

    public Date getDateOfAdoption() {
        return dateOfAdoption;
    }

    public void setDateOfAdoption(Date dateOfAdoption) {
        this.dateOfAdoption = dateOfAdoption;
    }

    public Date getDateOfAdoptionOrder() {
        return dateOfAdoptionOrder;
    }

    public void setDateOfAdoptionOrder(Date dateOfAdoptionOrder) {
        this.dateOfAdoptionOrder = dateOfAdoptionOrder;
    }

    public GenericDataItem getCountryOfBirth() {
        return countryOfBirth;
    }

    public void setCountryOfBirth(GenericDataItem countryOfBirth) {
        this.countryOfBirth = countryOfBirth;
    }

    public String getCitizenshipNo() {
        return citizenshipNo;
    }

    public void setCitizenshipNo(String citizenshipNo) {
        this.citizenshipNo = citizenshipNo;
    }

    public Date getDeceasedDate() {
        return deceasedDate;
    }

    public void setDeceasedDate(Date deceasedDate) {
        this.deceasedDate = deceasedDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public SupportingFile[] getSupportingFiles() {
        return supportingFiles;
    }

    public void setSupportingFiles(SupportingFile[] supportingFiles) {
        this.supportingFiles = supportingFiles;
    }
}
