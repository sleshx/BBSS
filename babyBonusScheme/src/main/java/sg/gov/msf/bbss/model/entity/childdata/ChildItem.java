package sg.gov.msf.bbss.model.entity.childdata;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.annotation.DisplayHeaderNameId;
import sg.gov.msf.bbss.apputils.annotation.DisplayNameId;
import sg.gov.msf.bbss.model.entity.common.BankAccount;
import sg.gov.msf.bbss.model.entity.people.Adult;
import sg.gov.msf.bbss.model.entity.people.Child;

/**
 * Created by bandaray
 */
@DisplayHeaderNameId(R.string.label_child)
public class ChildItem {

    public static final String FIELD_CHILD = "child";
    public static final String FIELD_NAH = "nominatedAccountHolder";
    public static final String FIELD_CDAT = "childDevAccTrustee";
    public static final String FIELD_BANK_ACC = "bankAccount";
    public static final String FIELD_CASH_GIFT_AMOUNT = "cashGiftAmount";
    public static final String FIELD_GOVERNMENT_MATCHING_AMOUNT = "govMatchingAmount";
    public static final String FIELD_CHILDCARE_SUBSIDY_AMOUNT = "childCareSubsidyAmount";
    public static final String FIELD_TOTAL_AMOUNT_RECEIVED = "totalAmountReceived";

    private Child child;

    private Adult nominatedAccountHolder;

    private Adult childDevAccTrustee;

    private BankAccount bankAccount;

    @DisplayNameId(R.string.label_child_cash_gift_amount)
    private double cashGiftAmount;

    @DisplayNameId(R.string.label_child_government_matching_amount)
    private double govMatchingAmount;

    @DisplayNameId(R.string.label_child_child_care_subsidy_amount)
    private double childCareSubsidyAmount;

    @DisplayNameId(R.string.label_child_total_amount_received)
    private double totalAmountReceived;

    private boolean isShowChildSec;

    private boolean isShowNAH;

    private boolean isShowCG;

    private boolean isShowCDA;

    private boolean isShowGovtMatching;

    private boolean isShowChildCareSubsidy;

    //----------------------------------------------------------------------------------------------

    public ChildItem(){

    }

    public ChildItem(Child child, double cashGiftAmount,
                     double govMatchingAmount, double childCareSubsidyAmount) {
        this.child = child;
        this.cashGiftAmount = cashGiftAmount;
        this.govMatchingAmount = govMatchingAmount;
        this.childCareSubsidyAmount = childCareSubsidyAmount;
        this.totalAmountReceived = cashGiftAmount + govMatchingAmount + childCareSubsidyAmount;
    }

    //----------------------------------------------------------------------------------------------

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public Adult getNominatedAccountHolder() {
        return nominatedAccountHolder;
    }

    public void setNominatedAccountHolder(Adult nominatedAccountHolder) {
        this.nominatedAccountHolder = nominatedAccountHolder;
    }

    public Adult getChildDevAccTrustee() {
        return childDevAccTrustee;
    }

    public void setChildDevAccTrustee(Adult childDevAccTrustee) {
        this.childDevAccTrustee = childDevAccTrustee;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public double getCashGiftAmount() {
        return cashGiftAmount;
    }

    public void setCashGiftAmount(double cashGiftAmount) {
        this.cashGiftAmount = cashGiftAmount;
    }

    public double getGovMatchingAmount() {
        return govMatchingAmount;
    }

    public void setGovMatchingAmount(double govMatchingAmount) {
        this.govMatchingAmount = govMatchingAmount;
    }

    public double getChildCareSubsidyAmount() {
        return childCareSubsidyAmount;
    }

    public void setChildCareSubsidyAmount(double childCareSubsidyAmount) {
        this.childCareSubsidyAmount = childCareSubsidyAmount;
    }

    public void setTotalAmountReceived(double totalAmountReceived) {
        this.totalAmountReceived = totalAmountReceived;
    }

    public double getTotalAmountReceived() {
        return totalAmountReceived;
    }

    public void calculateTotalAmountReceived() {
        this.totalAmountReceived = cashGiftAmount + govMatchingAmount + childCareSubsidyAmount;
    }

    public boolean isShowChildSec() {
        return isShowChildSec;
    }

    public void setShowChildSec(boolean isShowChildSec) {
        this.isShowChildSec = isShowChildSec;
    }

    public boolean isShowNAH() {
        return isShowNAH;
    }

    public void setShowNAH(boolean isShowNAH) {
        this.isShowNAH = isShowNAH;
    }

    public boolean isShowCG() {
        return isShowCG;
    }

    public void setShowCG(boolean isShowCG) {
        this.isShowCG = isShowCG;
    }

    public boolean isShowCDA() {
        return isShowCDA;
    }

    public void setShowCDA(boolean isShowCDA) {
        this.isShowCDA = isShowCDA;
    }

    public boolean isShowGovtMatching() {
        return isShowGovtMatching;
    }

    public void setShowGovtMatching(boolean isShowGovtMatching) {
        this.isShowGovtMatching = isShowGovtMatching;
    }

    public boolean isShowChildCareSubsidy() {
        return isShowChildCareSubsidy;
    }

    public void setShowChildCareSubsidy(boolean isShowChildCareSubsidy) {
        this.isShowChildCareSubsidy = isShowChildCareSubsidy;
    }
}
