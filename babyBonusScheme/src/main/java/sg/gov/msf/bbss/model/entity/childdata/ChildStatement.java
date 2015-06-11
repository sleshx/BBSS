package sg.gov.msf.bbss.model.entity.childdata;

import java.util.Date;
import java.util.List;

import sg.gov.msf.bbss.model.entity.common.CashGift;
import sg.gov.msf.bbss.model.entity.common.CdaBankAccount;
import sg.gov.msf.bbss.model.entity.common.ChildCareSubsidy;
import sg.gov.msf.bbss.model.entity.common.ChildDevAccountHistory;
import sg.gov.msf.bbss.model.entity.people.Adult;

/**
 * Created by bandaray
 * Modified to add from and to dates by chuanhe
 */
public class ChildStatement {

    public static final String FIELD_CCS_TOTAL = "childCareSubsidyAmt";

    private ChildItem childItem;

    private Adult nominatedAccountHolder;

    private List<CashGift> cashGiftList;

    private Adult childDevAccountTrustee;

    private CdaBankAccount cdaBankAccount;

    private List<ChildCareSubsidy> childCareSubsidies;

    private List<ChildDevAccountHistory> childDevAccountHistories;

    private double childCareSubsidyAmt;

    private boolean isChildDataDisplayed;

    private Date fromDate;

    private Date toDate;

    //----------------------------------------------------------------------------------------------

    public ChildItem getChildItem() {
        return childItem;
    }

    public void setChildItem(ChildItem childItem) {
        this.childItem = childItem;
    }

    public Adult getNominatedAccountHolder() {
        return nominatedAccountHolder;
    }

    public void setNominatedAccountHolder(Adult nominatedAccountHolder) {
        this.nominatedAccountHolder = nominatedAccountHolder;
    }

    public List<CashGift> getCashGiftList() {
        return cashGiftList;
    }

    public void setCashGiftList(List<CashGift> cashGiftList) {
        this.cashGiftList = cashGiftList;
    }

    public Adult getChildDevAccountTrustee() {
        return childDevAccountTrustee;
    }

    public void setChildDevAccountTrustee(Adult childDevAccountTrustee) {
        this.childDevAccountTrustee = childDevAccountTrustee;
    }

    public CdaBankAccount getCdaBankAccount() {
        return cdaBankAccount;
    }

    public void setCdaBankAccount(CdaBankAccount cdaBankAccount) {
        this.cdaBankAccount = cdaBankAccount;
    }

    public List<ChildCareSubsidy> getChildCareSubsidies() {
        return childCareSubsidies;
    }

    public void setChildCareSubsidies(List<ChildCareSubsidy> childCareSubsidies) {
        this.childCareSubsidies = childCareSubsidies;
    }

    public List<ChildDevAccountHistory> getChildDevAccountHistories() {
        return childDevAccountHistories;
    }

    public void setChildDevAccountHistories(List<ChildDevAccountHistory> childDevAccountHistories) {
        this.childDevAccountHistories = childDevAccountHistories;
    }

    public double getChildCareSubsidyAmt() {
        return childCareSubsidyAmt;
    }

    public void setChildCareSubsidyAmt(double childCareSubsidyAmt) {
        this.childCareSubsidyAmt = childCareSubsidyAmt;
    }

    public boolean isChildDataDisplayed() {
        return isChildDataDisplayed;
    }

    public void setChildDataDisplayed(boolean isChildDataDisplayed) {
        this.isChildDataDisplayed = isChildDataDisplayed;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
}
