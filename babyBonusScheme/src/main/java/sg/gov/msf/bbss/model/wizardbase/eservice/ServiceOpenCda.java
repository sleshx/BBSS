package sg.gov.msf.bbss.model.wizardbase.eservice;

import java.util.ArrayList;

import sg.gov.msf.bbss.apputils.wizard.WizardBase;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.entity.common.CdaBankAccount;
import sg.gov.msf.bbss.model.entity.people.Adult;
import sg.gov.msf.bbss.model.entity.people.CdaTrustee;

/**
 * Created by chuanhe on 4/2/2015.
 */
public class ServiceOpenCda extends WizardBase {
    private ArrayList<ChildItem> childItems;

    private ArrayList<String> childIds;

    private CdaTrustee cdaTrustee;

    private CdaBankAccount cdaBankAccount;

    private boolean isDeclared1;

    private boolean isDeclared2;

    private boolean isDeclared3;

    private boolean isThirdParty;

    //----------------------------------------------------------------------------------------------

    public ServiceOpenCda() {

    }

    public ServiceOpenCda(ArrayList<ChildItem> childItems, ArrayList<String> childIds) {
        super();
        this.childItems = childItems;
        this.childIds = childIds;
    }

    //----------------------------------------------------------------------------------------------


    public CdaBankAccount getCdaBankAccount() {
        return cdaBankAccount;
    }

    public void setCdaBankAccount(CdaBankAccount cdaBankAccount) {
        this.cdaBankAccount = cdaBankAccount;
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

    public CdaTrustee getCdaTrustee() {
        return cdaTrustee;
    }

    public void setCdaTrustee(CdaTrustee cdaTrustee) {
        this.cdaTrustee = cdaTrustee;
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

    public boolean isDeclared3() {
        return isDeclared3;
    }

    public void setDeclared3(boolean isDeclared3) {
        this.isDeclared3 = isDeclared3;
    }

    public boolean isThirdParty() {
        return isThirdParty;
    }

    public void setThirdParty(boolean isThirdParty) {
        this.isThirdParty = isThirdParty;
    }
}
