package sg.gov.msf.bbss.model.entity.masterdata;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.annotation.DisplayNameId;

/**
 * Created by bandaray
 */
public class Bank {

    public static final String FIELD_BANK_ID = "id";
    public static final String FIELD_BANK_NAME = "name";
    public static final String FIELD_BANK_TC_URL = "termsAndConditionsUrl";

    /*
     * WARNING : If you refactor any of the property names below,
     *           you have to rename the below constant values as well.
     */

    @DisplayNameId(R.string.label_common_id)
    private String id;

    @DisplayNameId(R.string.label_bank_name)
    private String name;

    @DisplayNameId(R.string.label_bank_tc_url)
    private String termsAndConditionsUrl;

    //----------------------------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTermsAndConditionsUrl() {
        return termsAndConditionsUrl;
    }

    public void setTermsAndConditionsUrl(String termsAndConditionsUrl) {
        this.termsAndConditionsUrl = termsAndConditionsUrl;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return name;
    }
}