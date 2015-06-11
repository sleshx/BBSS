package sg.gov.msf.bbss.model.entity.common;

import java.util.Date;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.annotation.DisplayNameId;

/**
 * Created by bandaray
 */
public class CashGift {

    public static final String FIELD_CG_ID = "id";
    public static final String FIELD_CG_BANK = "bankAccount";
    public static final String FIELD_CG_AMOUNT = "giftAmount";
    public static final String FIELD_CG_DATE_PAID = "paidDate";
    public static final String FIELD_CG_DATE_SCHEDULED = "scheduledDate";

    /*
     * WARNING : If you refactor any of the property names below,
     *           you have to rename the above constant values as well.
     */

    @DisplayNameId(R.string.label_common_id)
    private String id;

    @DisplayNameId(R.string.label_bank_name)
    private BankAccount bankAccount;

    @DisplayNameId(R.string.label_cash_gift_amount)
    private double giftAmount;

    @DisplayNameId(R.string.label_cash_gift_paid_date)
    private Date paidDate;

    @DisplayNameId(R.string.label_cash_gift_scheduled_date)
    private Date scheduledDate;

    //----------------------------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public double getGiftAmount() {
        return giftAmount;
    }

    public void setGiftAmount(double giftAmount) {
        this.giftAmount = giftAmount;
    }

    public Date getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
}
