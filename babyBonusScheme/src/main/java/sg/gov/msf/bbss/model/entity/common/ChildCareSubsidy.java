package sg.gov.msf.bbss.model.entity.common;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.annotation.DisplayNameId;

/**
 * Created by bandaray
 */
public class ChildCareSubsidy {

    public static final String FIELD_SUBSIDY_ID= "id";
    public static final String FIELD_SUBSIDY_NAME = "name";
    public static final String FIELD_SUBSIDY_AMOUNT = "amount";
    public static final String FIELD_SUBSIDY_MONTH = "month";

    /*
     * WARNING : If you refactor any of the property names below,
     *           you have to rename the above constant values as well.
     */

    @DisplayNameId(R.string.label_common_id)
    private String id;

    @DisplayNameId(R.string.label_child_care_subsidy_name)
    private String name;

    @DisplayNameId(R.string.label_child_care_subsidy_amount)
    private double amount;

    @DisplayNameId(R.string.label_child_care_subsidy_month)
    private String month;

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
