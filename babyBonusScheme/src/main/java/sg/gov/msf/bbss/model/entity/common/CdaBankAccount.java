package sg.gov.msf.bbss.model.entity.common;

import java.util.Date;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.annotation.DisplayNameId;
import sg.gov.msf.bbss.model.entity.masterdata.GenericDataItem;

/**
 * Created by bandaray
 */
public class CdaBankAccount extends BankAccount {

    public static final String FIELD_CDA_ID = "id";
    public static final String FIELD_CDA_EXPIRY = "expiryDate";
    public static final String FIELD_CDA_CAP = "capAmount";
    public static final String FIELD_CDA_REMAINING_CAP = "remainingCapAmount";
    public static final String FIELD_CDA_TOT_DEPOSIT = "totalDeposit";
    public static final String FIELD_CDA_TOT_GOV_MATCHING = "totalGovtMatching";
    public static final String FIELD_CHANGE_REASON = "changeReason";
    public static final String FIELD_CHANGE_REASON_OTHER = "changeReasonDescription";
    public static final String FIELD_CDA_NETS_CARD_NAME = "netsCardName";

    /*
     * WARNING : If you refactor any of the property names below,
     *           you have to rename the above constant values as well.
     */

    @DisplayNameId(R.string.label_child_dev_acc_expiry)
    private Date expiryDate;

    @DisplayNameId(R.string.label_child_dev_acc_cap)
    private double capAmount;

    @DisplayNameId(R.string.label_child_dev_acc_remaining_cap)
    private double remainingCapAmount;

    @DisplayNameId(R.string.label_child_dev_acc_total_deposit)
    private double totalDeposit;

    @DisplayNameId(R.string.label_child_dev_acc_total_gov_matching)
    private double totalGovtMatching;
    // TODO: or ChildDevAccount history;

    @DisplayNameId(R.string.label_cda_bank_change_reason)
    private GenericDataItem changeReason;

    @DisplayNameId(R.string.label_cda_bank_change_reason_other)
    private String changeReasonDescription;

    @DisplayNameId(R.string.label_child_dev_acc_nets_card_child_name)
    private String netsCardName;

    //-------------------------------------------------------------------------------------------

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public double getCapAmount() {
        return capAmount;
    }

    public void setCapAmount(double capAmount) {
        this.capAmount = capAmount;
    }

    public double getRemainingCapAmount() {
        return remainingCapAmount;
    }

    public void setRemainingCapAmount(double remainingCapAmount) {
        this.remainingCapAmount = remainingCapAmount;
    }

    public double getTotalDeposit() {
        return totalDeposit;
    }

    public void setTotalDeposit(double totalDeposit) {
        this.totalDeposit = totalDeposit;
    }

    public double getTotalGovtMatching() {
        return totalGovtMatching;
    }

    public void setTotalGovtMatching(double totalGovtMatching) {
        this.totalGovtMatching = totalGovtMatching;
    }

    public GenericDataItem getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(GenericDataItem changeReason) {
        this.changeReason = changeReason;
    }

    public String getChangeReasonDescription() {
        return changeReasonDescription;
    }

    public void setChangeReasonDescription(String changeReasonDescription) {
        this.changeReasonDescription = changeReasonDescription;
    }

    public String getNetsCardName() {
        return netsCardName;
    }

    public void setNetsCardName(String netsCardName) {
        this.netsCardName = netsCardName;
    }
}
