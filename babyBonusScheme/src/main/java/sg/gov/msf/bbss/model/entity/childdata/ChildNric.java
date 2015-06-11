package sg.gov.msf.bbss.model.entity.childdata;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.annotation.DisplayNameId;

/**
 * Created by bandaray
 */
public class ChildNric {

    public static final String FIELD_NRIC1 = "nric1";
    public static final String FIELD_NRIC2 = "nric2";

    /*
     * WARNING : If you refactor any of the property names below,
     *           you have to rename the above constant values as well.
     */

    @DisplayNameId(R.string.label_sibling_check_child_1)
    private String nric1;

    @DisplayNameId(R.string.label_sibling_check_child_2)
    private String nric2;

    public String getNric1() {
        return nric1;
    }

    public void setNric1(String nric1) {
        this.nric1 = nric1;
    }

    public String getNric2() {
        return nric2;
    }

    public void setNric2(String nric2) {
        this.nric2 = nric2;
    }
}
