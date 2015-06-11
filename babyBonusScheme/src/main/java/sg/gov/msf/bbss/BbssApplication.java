package sg.gov.msf.bbss;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import sg.gov.msf.bbss.logic.type.LoginUserType;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.entity.childdata.ChildStatement;
import sg.gov.msf.bbss.model.wizardbase.UpdateProfile;
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentChildRegistration;
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentForm;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceCdabTc;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceChangeBo;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceChangeCdab;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceChangeCdat;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceChangeNah;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceChangeNan;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceOpenCda;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceTransferToPsea;
import sg.gov.msf.bbss.view.MainActivity;

/**
 * Created by bandaray
 * Modified to add new methods by chuanhe
 */
public class BbssApplication extends Application {

    private static final String APP_SHARED_PREFERENCES = "AppSharedPreferences" ;
    private static final String APP_AUTH_TOKEN = "APP_AUTH_TOKEN";

    private SharedPreferences sharedpreferences;
    private ProgressDialog progressDialog;

    //--- Home
    private UpdateProfile updateProfile;
    private ChildItem[] familyViewChildren;
    private ChildStatement childStatement;

    //--- Enrolment
    private EnrolmentForm enrolmentForm;
    private EnrolmentChildRegistration enrolmentChildRegistration;

    //--- Services
    private ServiceChangeNah serviceChangeNah;
    private ServiceChangeNan serviceChangeNan;
    private ServiceChangeCdat serviceChangeCdat;
    private ServiceChangeCdab serviceChangeCdab;
    private ServiceTransferToPsea serviceTransferToPsea;
    private ServiceChangeBo serviceChangeBo;
    private ServiceCdabTc serviceCdabTc;
    private ServiceOpenCda serviceOpenCda;


    @Override
    public void onCreate() {
        super.onCreate();
        sharedpreferences = getSharedPreferences(APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    //--- Share Preferences ------------------------------------------------------------------------

    public String getSettingsPassword() {
        return sharedpreferences.getString(APP_AUTH_TOKEN, "");
    }

    public boolean setSettingsPassword(String password) {
        Editor editor = sharedpreferences.edit();
        editor.putString(APP_AUTH_TOKEN, password);
        return editor.commit();
    }

    //--- Progress Dialog --------------------------------------------------------------------------

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    //--- Home -------------------------------------------------------------------------------------

    //Family View
    public ChildItem[] getFamilyViewChildren() {
        return familyViewChildren;
    }

    public void setFamilyViewChildren(ChildItem[] familyViewChildren) {
        this.familyViewChildren = familyViewChildren;
    }

    public ChildStatement getChildStatement() {
        return childStatement;
    }

    public void setChildStatement(ChildStatement childStatement) {
        this.childStatement = childStatement;
    }

    // Update Profile
    public UpdateProfile getUpdateProfile() {
        return updateProfile;
    }

    public void setUpdateProfile(UpdateProfile updateProfile) {
        this.updateProfile = updateProfile;
    }

    //--- Enrolment Form ---------------------------------------------------------------------------

    public EnrolmentForm getEnrolmentForm() {
        return enrolmentForm;
    }

    public void setEnrolmentForm(EnrolmentForm enrolmentForm) {
        this.enrolmentForm = enrolmentForm;
    }

    public EnrolmentChildRegistration getEnrolmentChildRegistration() {
        return enrolmentChildRegistration;
    }

    public void setEnrolmentChildRegistration(EnrolmentChildRegistration enrolmentChildRegistration) {
        this.enrolmentChildRegistration = enrolmentChildRegistration;
    }

    //--- Services ---------------------------------------------------------------------------------

    //NAH
    public ServiceChangeNah getServiceChangeNah() {
        return serviceChangeNah;
    }

    public void setServiceChangeNah(ServiceChangeNah serviceChangeNah) {
        this.serviceChangeNah = serviceChangeNah;
    }

    //NAN
    public ServiceChangeNan getServiceChangeNan() {
        return serviceChangeNan;
    }

    public void setServiceChangeNan(ServiceChangeNan serviceChangeNan) {
        this.serviceChangeNan = serviceChangeNan;
    }

    //CDAT
    public ServiceChangeCdat getServiceChangeCdat() {
        return serviceChangeCdat;
    }

    public void setServiceChangeCdat(ServiceChangeCdat serviceChangeCdat) {
        this.serviceChangeCdat = serviceChangeCdat;
    }

    //CDAB
    public ServiceChangeCdab getServiceChangeCdab() {
        return serviceChangeCdab;
    }

    public void setServiceChangeCdab(ServiceChangeCdab serviceChangeCdab) {
        this.serviceChangeCdab = serviceChangeCdab;
    }

    //PSEA
    public ServiceTransferToPsea getServiceTransferToPsea() {
        return serviceTransferToPsea;
    }

    public void setServiceTransferToPsea(ServiceTransferToPsea serviceTransferToPsea) {
        this.serviceTransferToPsea = serviceTransferToPsea;
    }

    //BO
    public ServiceChangeBo getServiceChangeBo() {
        return serviceChangeBo;
    }

    public void setServiceChangeBo(ServiceChangeBo serviceChangeBo) {
        this.serviceChangeBo = serviceChangeBo;
    }

    //CDAB_TC
    public ServiceCdabTc getServiceCdabTc() {
        return serviceCdabTc;
    }

    public void setServiceCdabTc(ServiceCdabTc serviceCdabTc) {
        this.serviceCdabTc = serviceCdabTc;
    }

    //OPEN_CDA
    public ServiceOpenCda getServiceOpenCda() {
        return serviceOpenCda;
    }

    public void setServiceOpenCda(ServiceOpenCda serviceOpenCda) {
        this.serviceOpenCda = serviceOpenCda;
    }

    //--- HELPERS ----------------------------------------------------------------------------------

    public void startMainActivity1(Activity currentActivity) {
        Intent intent = new Intent(currentActivity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        currentActivity.finish();
    }
}
