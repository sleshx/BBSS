package sg.gov.msf.bbss.model.wizardbase.eservice;

import java.util.ArrayList;

import sg.gov.msf.bbss.apputils.wizard.WizardBase;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.entity.common.CdaBankAccount;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class ServiceChangeCdab extends WizardBase {


    private ArrayList<ChildItem> childItems;

    private ArrayList<String> childIds;

    private CdaBankAccount cdaBankAccount;

    private boolean isDeclared1;

    private boolean isDeclared2;

    //----------------------------------------------------------------------------------------------

    public ServiceChangeCdab() {

    }

    public ServiceChangeCdab(ArrayList<ChildItem> childItems, ArrayList<String> childIds) {
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

    public CdaBankAccount getCdaBankAccount() {
        return cdaBankAccount;
    }

    public void setCdaBankAccount(CdaBankAccount cdaBankAccount) {
        this.cdaBankAccount = cdaBankAccount;
    }

    public boolean isDeclared1() {
        return isDeclared1;
    }

    public void setDeclared1(boolean isDeclared1) {
        this.isDeclared1 = isDeclared1;
    }

    public boolean isDeclared2() {
        return isDeclared2;
    }

    public void setDeclared2(boolean isDeclared2) {
        this.isDeclared2 = isDeclared2;
    }
}
