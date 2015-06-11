package sg.gov.msf.bbss.model.wizardbase.eservice;

import java.util.ArrayList;

import sg.gov.msf.bbss.apputils.wizard.WizardBase;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.entity.common.BankAccount;
import sg.gov.msf.bbss.model.entity.people.Adult;

/**
 * Created by chuanhe on 4/2/2015.
 */
public class ServiceTransferToPsea extends WizardBase {
    private ArrayList<ChildItem> childItems;

    private ArrayList<String> childIds;

    private Adult nominatedAccHolder;

    private BankAccount newBankAccount;

    private boolean isDeclared;

    private boolean isThirdParty;

    //----------------------------------------------------------------------------------------------

    public ServiceTransferToPsea() {

    }

    public ServiceTransferToPsea(ArrayList<ChildItem> childItems, ArrayList<String> childIds) {
        super();
        this.childItems = childItems;
        this.childIds = childIds;
    }

    //----------------------------------------------------------------------------------------------


    public Adult getNominatedAccHolder() {
        return nominatedAccHolder;
    }

    public void setNominatedAccHolder(Adult nominatedAccHolder) {
        this.nominatedAccHolder = nominatedAccHolder;
    }

    public ArrayList<ChildItem> getChildItems() {
        return childItems;
    }

    public void setChildItems(ArrayList<ChildItem> childItems) {
        this.childItems = childItems;
    }

    public ArrayList<String> getChildIds() {
        return childIds;
    }

    public void setChildIds(ArrayList<String> childIds) {
        this.childIds = childIds;
    }

    public BankAccount getNewBankAccount() {
        return newBankAccount;
    }

    public void setNewBankAccount(BankAccount newBankAccount) {
        this.newBankAccount = newBankAccount;
    }

    public boolean isDeclared() {
        return isDeclared;
    }

    public void setDeclared(boolean isDeclared) {
        this.isDeclared = isDeclared;
    }

    public boolean isThirdParty() {
        return isThirdParty;
    }

    public void setThirdParty(boolean isThirdParty) {
        this.isThirdParty = isThirdParty;
    }
}
