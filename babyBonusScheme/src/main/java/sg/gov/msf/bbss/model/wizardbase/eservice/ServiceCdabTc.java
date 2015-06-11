package sg.gov.msf.bbss.model.wizardbase.eservice;

import java.util.ArrayList;

import sg.gov.msf.bbss.apputils.wizard.WizardBase;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.entity.people.Adult;

/**
 * Created by chuanhe on 4/2/2015.
 */
public class ServiceCdabTc extends WizardBase {
    private ArrayList<ChildItem> childItems;

    private ArrayList<String> childIds;

    private Adult nominatedAccHolder;

    private boolean isDeclared1;

    private boolean isDeclared2;

    private boolean isThirdParty;

    public ServiceCdabTc() {

    }

    public ServiceCdabTc(ArrayList<ChildItem> childItems, ArrayList<String> childIds) {
        super();
        this.childItems = childItems;
        this.childIds = childIds;
    }

    public Adult getNominatedAccHolder() {
        return nominatedAccHolder;
    }

    public void setNominatedAccHolder(Adult nominatedAccHolder) {
        this.nominatedAccHolder = nominatedAccHolder;
    }

    public boolean isThirdParty() {
        return isThirdParty;
    }

    public void setThirdParty(boolean isThirdParty) {
        this.isThirdParty = isThirdParty;
    }

    public ArrayList<ChildItem> getChildItems() {
        return childItems;
    }

    public void setChildItems(ArrayList<ChildItem> childItems) {
        this.childItems = childItems;
    }

    public boolean isDeclared2() {
        return isDeclared2;
    }

    public void setDeclared2(boolean isDeclared2) {
        this.isDeclared2 = isDeclared2;
    }

    public boolean isDeclared1() {
        return isDeclared1;
    }

    public void setDeclared1(boolean isDeclared1) {
        this.isDeclared1 = isDeclared1;
    }

    public ArrayList<String> getChildIds() {
        return childIds;
    }

    public void setChildIds(ArrayList<String> childIds) {
        this.childIds = childIds;
    }
}
