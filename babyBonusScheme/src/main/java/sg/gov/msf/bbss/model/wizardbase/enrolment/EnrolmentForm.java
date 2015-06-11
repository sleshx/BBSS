package sg.gov.msf.bbss.model.wizardbase.enrolment;

import java.util.ArrayList;
import java.util.List;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.annotation.DisplayNameId;
import sg.gov.msf.bbss.apputils.wizard.WizardBase;
import sg.gov.msf.bbss.logic.type.EnrolmentAppType;
import sg.gov.msf.bbss.logic.type.RelationshipType;
import sg.gov.msf.bbss.model.entity.childdata.ChildDeclaration;
import sg.gov.msf.bbss.model.entity.childdata.ChildRegistration;
import sg.gov.msf.bbss.model.entity.common.BankAccount;
import sg.gov.msf.bbss.model.entity.common.CdaBankAccount;
import sg.gov.msf.bbss.model.entity.common.SupportingFile;
import sg.gov.msf.bbss.model.entity.people.Adult;

/**
 * Created by bandaray
 */
public class EnrolmentForm extends WizardBase {

    private Adult father;

    private Adult mother;

    private ChildRegistration childRegistration;

    private Adult naHolder;

    private RelationshipType naHolderType;

    private BankAccount cashGiftBankAccount;

    private Adult cdaTrustee;

    private RelationshipType cdaTrusteeType;

    private CdaBankAccount cdaBankAccount;

    private ArrayList<ChildDeclaration> childDeclarations;

    private EnrolmentAppType appType;

    private boolean isPrePopulated;

    private boolean isDeclare1;

    private boolean isDeclare2;

    private boolean isDeclare3;

    private boolean isDeclare4;

    //----------------------------------------------------------------------------------------------

    public EnrolmentForm (boolean isPrePopulated, EnrolmentAppType appType) {
        this.isPrePopulated = isPrePopulated;
        this.appType = appType;
    }

    //----------------------------------------------------------------------------------------------

    public Adult getFather() {
        return father;
    }

    public void setFather(Adult father) {
        this.father = father;
    }

    public Adult getMother() {
        return mother;
    }

    public void setMother(Adult mother) {
        this.mother = mother;
    }

    public ChildRegistration getChildRegistration() {
        return childRegistration;
    }

    public void setChildRegistration(ChildRegistration childRegistration) {
        this.childRegistration = childRegistration;
    }

    public Adult getNaHolder() {
        return naHolder;
    }

    public void setNaHolder(Adult naHolder) {
        this.naHolder = naHolder;
    }

    public RelationshipType getNaHolderType() {
        return naHolderType;
    }

    public BankAccount getCashGiftBankAccount() {
        return cashGiftBankAccount;
    }

    public void setCashGiftBankAccount(BankAccount bankAccount) {
        this.cashGiftBankAccount = bankAccount;
    }

    public void setNaHolderType(RelationshipType naHolderType) {
        this.naHolderType = naHolderType;
    }

    public Adult getCdaTrustee() {
        return cdaTrustee;
    }

    public void setCdaTrustee(Adult cdaTrustee) {
        this.cdaTrustee = cdaTrustee;
    }

    public RelationshipType getCdaTrusteeType() {
        return cdaTrusteeType;
    }

    public void setCdaTrusteeType(RelationshipType cdaTrusteeType) {
        this.cdaTrusteeType = cdaTrusteeType;
    }

    public CdaBankAccount getCdaBankAccount() {
        return cdaBankAccount;
    }

    public void setCdaBankAccount(CdaBankAccount cdaBankAccount) {
        this.cdaBankAccount = cdaBankAccount;
    }

    public ArrayList<ChildDeclaration> getChildDeclarations() {
        return childDeclarations;
    }

    public void setChildDeclarations(ArrayList<ChildDeclaration> childDeclarations) {
        this.childDeclarations = childDeclarations;
    }

    public boolean isPrePopulated() {
        return isPrePopulated;
    }

    public EnrolmentAppType getAppType() {
        return appType;
    }

    public void setAppType(EnrolmentAppType appType) {
        this.appType = appType;
    }

    public boolean isDeclare1() {
        return isDeclare1;
    }

    public void setDeclare1(boolean isDeclare1) {
        this.isDeclare1 = isDeclare1;
    }

    public boolean isDeclare2() {
        return isDeclare2;
    }

    public void setDeclare2(boolean isDeclare2) {
        this.isDeclare2 = isDeclare2;
    }

    public boolean isDeclare3() {
        return isDeclare3;
    }

    public void setDeclare3(boolean isDeclare3) {
        this.isDeclare3 = isDeclare3;
    }

    public boolean isDeclare4() {
        return isDeclare4;
    }

    public void setDeclare4(boolean isDeclare4) {
        this.isDeclare4 = isDeclare4;
    }
}

