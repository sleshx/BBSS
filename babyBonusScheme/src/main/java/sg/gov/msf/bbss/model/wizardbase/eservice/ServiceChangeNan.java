package sg.gov.msf.bbss.model.wizardbase.eservice;

import java.util.ArrayList;

import sg.gov.msf.bbss.apputils.wizard.WizardBase;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.entity.common.BankAccount;
import sg.gov.msf.bbss.model.entity.people.Adult;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class ServiceChangeNan extends WizardBase {

    private ArrayList<ChildItem> childItems;

    private ArrayList<String> childIds;

    private Adult nominatedAccHolder;

    private BankAccount newBankAccount;

    private boolean isDeclared;

    //----------------------------------------------------------------------------------------------

    public ServiceChangeNan() {

    }

    public ServiceChangeNan(ArrayList<ChildItem> childItems, ArrayList<String> childIds) {
        super();
        this.childItems = childItems;
        this.childIds = childIds;
    }

    //----------------------------------------------------------------------------------------------

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

    public Adult getNominatedAccHolder() {
        return nominatedAccHolder;
    }

    public void setNominatedAccHolder(Adult nominatedAccHolder) {
        this.nominatedAccHolder = nominatedAccHolder;
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

}
