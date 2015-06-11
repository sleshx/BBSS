package sg.gov.msf.bbss.model.wizardbase.eservice;

import java.util.ArrayList;

import sg.gov.msf.bbss.apputils.wizard.WizardBase;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.entity.people.CdaTrustee;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class ServiceChangeCdat extends WizardBase {

    private ArrayList<ChildItem> childItems;

    private ArrayList<String> childIds;

    private CdaTrustee cdaTrustee;

    private boolean isDeclared1;

    private boolean isDeclared2;

    private boolean isThirdParty;

    private boolean isValidationsRequired;

    //----------------------------------------------------------------------------------------------

    public ServiceChangeCdat() {

    }

    public ServiceChangeCdat(ArrayList<ChildItem> childItems, ArrayList<String> childIds) {
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

    public boolean isThirdParty() {
        return isThirdParty;
    }

    public void setThirdParty(boolean isThirdParty) {
        this.isThirdParty = isThirdParty;
    }

    public boolean isValidationsRequired() {
        return isValidationsRequired;
    }

    public void setValidationsRequired(boolean isValidationsRequired) {
        this.isValidationsRequired = isValidationsRequired;
    }
}
