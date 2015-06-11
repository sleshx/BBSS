package sg.gov.msf.bbss.model.entity.masterdata;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.annotation.DisplayNameId;

/**
 * Created by bandaray
 */
public class AccessibleService {

    public static final String SERVICE_ID = "id";
    public static final String SERVICE_NAME = "name";
    public static final String SERVICE_IS_OUTSTANDING = "isOutstanding";

    /*
     * WARNING : If you refactor any of the property names below,
     *           you have to rename the above constant values as well.
     */

//    @DisplayNameId(R.string.label_common_id)
//    private String id;

    @DisplayNameId(R.string.label_services_common)
    private String code;

    @DisplayNameId(R.string.label_services_common_is_pending)
    private boolean isOutstanding;

    //----------------------------------------------------------------------------------------------

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isOutstanding() {
        return isOutstanding;
    }

    public void setIsOutstanding(boolean isOutstanding) {
        this.isOutstanding = isOutstanding;
    }
}
