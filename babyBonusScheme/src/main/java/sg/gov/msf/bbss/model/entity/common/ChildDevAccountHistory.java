package sg.gov.msf.bbss.model.entity.common;

import java.util.Date;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.annotation.DisplayNameId;

/**
 * Created by bandaray
 */
public class ChildDevAccountHistory {

    public static final String FIELD_CDA_HISTORY_ID = "id";
    public static final String FIELD_CDA_HISTORY_DEPOSIT_AMT = "depositAmount";
    public static final String FIELD_CDA_HISTORY_MATCHED_AMT = "matchedAmount";
    public static final String FIELD_CDA_HISTORY_DEPOSIT_DATE = "depositDate";
    public static final String FIELD_CDA_HISTORY_MATCHED_DATE = "matchedDate";

    /*
     * WARNING : If you refactor any of the property names below,
     *           you have to rename the above constant values as well.
     */

    @DisplayNameId(R.string.label_common_id)
    private String id;

    @DisplayNameId(R.string.label_cda_matching_history_deposit)
    private double depositAmount;

    @DisplayNameId(R.string.label_cda_matching_history_matching)
    private double matchedAmount;

    @DisplayNameId(R.string.label_cda_matching_history_deposit_date)
    private Date depositDate;

    @DisplayNameId(R.string.label_cda_matching_history_matching_date)
    private Date matchedDate;


    //----------------------------------------------------------------------------------------------


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(double depositAmount) {
        this.depositAmount = depositAmount;
    }

    public double getMatchedAmount() {
        return matchedAmount;
    }

    public void setMatchedAmount(double matchedAmount) {
        this.matchedAmount = matchedAmount;
    }

    public Date getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(Date depositDate) {
        this.depositDate = depositDate;
    }

    public Date getMatchedDate() {
        return matchedDate;
    }

    public void setMatchedDate(Date matchedDate) {
        this.matchedDate = matchedDate;
    }
}
