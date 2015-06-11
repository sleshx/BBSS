package sg.gov.msf.bbss.view.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyEditType;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMeta;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMetaList;
import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.apputils.ui.component.MessageBox;
import sg.gov.msf.bbss.apputils.ui.helper.MessageBoxButtonClickListener;
import sg.gov.msf.bbss.apputils.ui.helper.MessagePopupButtonClickListener;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.validation.ValidationInfo;
import sg.gov.msf.bbss.apputils.validation.ValidationMessage;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.BabyBonusValidationHandler;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.logic.server.response.ServerResponse;
import sg.gov.msf.bbss.logic.server.response.ServerResponseType;
import sg.gov.msf.bbss.logic.type.AddressType;
import sg.gov.msf.bbss.logic.type.CommunicationType;
import sg.gov.msf.bbss.model.entity.common.Address;
import sg.gov.msf.bbss.model.entity.people.Adult;
import sg.gov.msf.bbss.model.wizardbase.UpdateProfile;
import sg.gov.msf.bbss.view.MainActivity;

/**
 * Created by chuanhe
 * Fixed bugs and did some code reimplementation by bandaray
 */
public class UpdateProfileActivity extends Activity {

    private static int CURRENT_POSITION = 0;

    private static Class ADULT_CLASS = Adult.class;
    private static Class ADDRESS_CLASS = Address.class;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private ModelViewSynchronizer<Adult> adultModelViewSynchronizer;
    private boolean isAddressLocal;

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        context = this;
        getActionBar().hide();

        app = (BbssApplication) this.getApplication();
        rootView = getWindow().getDecorView().findViewById(android.R.id.content);

        setButtonClicks();
        isOkToShowFields(false);
        (new GetUserProfileAsyncTask(context)).execute();
    }

    //--- BACK NAVIGATION --------------------------------------------------------------------------

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    private void displayData() {
        isOkToShowFields(true);

        //Person Particulars
        adultModelViewSynchronizer = new ModelViewSynchronizer<Adult>(
                ADULT_CLASS, getMetaDataPersonParticulars(), rootView,
                SerializedNames.SEC_HOME_UPDATE_PROFILE);

        Adult adult = app.getUpdateProfile().getUserDetail();

        if(adult == null){
            adult = new Adult();
        }

        adultModelViewSynchronizer.setLabels();
        adultModelViewSynchronizer.setHeaderTitle(R.id.section_user_particulars,
                R.string.label_user_details);
        adultModelViewSynchronizer.displayDataObject(adult);

        //Person Address
        ModelViewSynchronizer<Address> addressModelViewSynchronizer = new ModelViewSynchronizer<Address>(
                ADDRESS_CLASS, getMetaDataPersonAddress(), rootView,
                SerializedNames.SEC_ADDRESS);

        Address address = adult.getPostalAddress();

        if(address == null){
            address = new Address();
        }

        addressModelViewSynchronizer.setLabels();
        addressModelViewSynchronizer.setHeaderTitle(R.id.section_user_particulars,
                R.string.label_user_details);
        addressModelViewSynchronizer.displayDataObject(address);

        showAddress();

        //Screen - Title
        ((TextView) findViewById(R.id.tvPageTitle)).setText(StringHelper.getStringByResourceId(this,
                R.string.title_activity_update_profile));

        //Screen - Instructions
        displayInstructions(displayValidationErrors());
    }

    private String displayValidationErrors() {
        UpdateProfile updateProfile = app.getUpdateProfile();
        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;

        if(updateProfile.isDisplayValidationErrors()){
            ValidationInfo validationInfo = updateProfile.getPageSectionValidations(
                    CURRENT_POSITION, SerializedNames.SEC_HOME_UPDATE_PROFILE);

            if(validationInfo.hasAnyValidationMessages()) {
                errorMessageList = validationInfo.getValidationMessages();
                adultModelViewSynchronizer.displayValidationErrors(errorMessageList);

                for(ValidationMessage messageList : errorMessageList) {
                    errorMessage = errorMessage + messageList.getMessage() +
                            AppConstants.SYMBOL_BREAK_LINE;
                }
            }
        }
        return errorMessage;
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {
        ImageView ivBackButton = (ImageView) rootView.findViewById(R.id.ivBackButton);
        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        });

        setSubmitButtonClick();
    }

    private void setSubmitButtonClick() {
        Button submit = (Button) rootView.findViewById(R.id.btnFirstInOne);
        submit.setText(R.string.btn_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateProfile updateProfile = app.getUpdateProfile();
                Adult adult = adultModelViewSynchronizer.getDataObject();
                ValidationInfo validationInfo = adultModelViewSynchronizer.getValidationInfo();

                BabyBonusValidationHandler.validateMobileNumberPrefix(context, adult, validationInfo);

                updateProfile.clearAnyPageValidations(CURRENT_POSITION);
                updateProfile.addSectionPage(SerializedNames.SEC_HOME_UPDATE_PROFILE, CURRENT_POSITION);

                if (validationInfo.hasAnyValidationMessages()) {
                    updateProfile.addPageValidation(CURRENT_POSITION, validationInfo);
                }

                if (updateProfile.hasClientValidations()) {
                    afterSubmit(true,
                            StringHelper.getStringByResourceId(context,
                                    R.string.error_common_form_not_properly_completed));
                } else {
                    new UpdateUserProfileAsyncTask(context).execute();
                }
            }
        });
    }

    //--- HELPERS ----------------------------------------------------------------------------------

    private void displayInstructions(String errorMessage) {
        LinearLayout instructionLayout = (LinearLayout) findViewById(R.id.screen_instructions);

        instructionLayout.findViewById(R.id.tvInstructionTitle).setVisibility(View.GONE);
        instructionLayout.findViewById(R.id.tvInstructionStepNo).setVisibility(View.GONE);

        ((WebView) instructionLayout.findViewById(R.id.wvInstructionDesc)).loadData(
                StringHelper.getJustifiedString(context, R.string.desc_home_update_profile,
                        R.color.theme_creme), "text/html", "utf-8");

        //Screen - Error Message
        WebView wvError = (WebView) findViewById(R.id.wvErrorDesc);
        if (!StringHelper.isStringNullOrEmpty(errorMessage)) {
            wvError.setVisibility(View.VISIBLE);
            wvError.loadDataWithBaseURL(null, StringHelper.getJustifiedErrorString(context,
                    errorMessage, R.color.theme_creme),"text/html", "utf-8", null);
        } else {
            wvError.setVisibility(View.GONE);
        }
    }

    private void showAddress() {
        LinearLayout postalCode = (LinearLayout) rootView.findViewById(R.id.user_local_postal_code);
        LinearLayout unitNo = (LinearLayout) rootView.findViewById(R.id.user_local_unit_no);
        LinearLayout blockNo = (LinearLayout) rootView.findViewById(R.id.user_local_block_no);
        LinearLayout street = (LinearLayout) rootView.findViewById(R.id.user_local_street_name_);
        LinearLayout building = (LinearLayout) rootView.findViewById(R.id.user_local_building_name);
        LinearLayout address1 = (LinearLayout) rootView.findViewById(R.id.user_foreign_address1);
        LinearLayout address2 = (LinearLayout) rootView.findViewById(R.id.user_foreign_address2);

        if (isAddressLocal) {
            postalCode.setVisibility(View.VISIBLE);
            unitNo.setVisibility(View.VISIBLE);
            blockNo.setVisibility(View.VISIBLE);
            street.setVisibility(View.VISIBLE);
            building.setVisibility(View.VISIBLE);
            address1.setVisibility(View.GONE);
            address2.setVisibility(View.GONE);
        } else {
            postalCode.setVisibility(View.GONE);
            unitNo.setVisibility(View.GONE);
            blockNo.setVisibility(View.GONE);
            street.setVisibility(View.GONE);
            building.setVisibility(View.GONE);
            address1.setVisibility(View.VISIBLE);
            address2.setVisibility(View.VISIBLE);
        }
    }

    private void isOkToShowFields(boolean isOkToShowFields) {
        int visibility = (isOkToShowFields) ? View.VISIBLE : View.GONE;

        rootView.findViewById(R.id.user_nric).setVisibility(visibility);
        rootView.findViewById(R.id.user_id_type).setVisibility(visibility);
        rootView.findViewById(R.id.user_id_no).setVisibility(visibility);
        rootView.findViewById(R.id.user_name).setVisibility(visibility);
        rootView.findViewById(R.id.user_nationality).setVisibility(visibility);
        rootView.findViewById(R.id.user_birthday).setVisibility(visibility);
        rootView.findViewById(R.id.user_mode_of_comunication).setVisibility(visibility);
        rootView.findViewById(R.id.user_mobile).setVisibility(visibility);
        rootView.findViewById(R.id.user_email).setVisibility(visibility);
        rootView.findViewById(R.id.user_address_type).setVisibility(visibility);
        rootView.findViewById(R.id.update_ll_local).setVisibility(visibility);
        rootView.findViewById(R.id.update_ll_foreign).setVisibility(visibility);
        rootView.findViewById(R.id.submit_buttons).setVisibility(visibility);
    }

    //--- META DATA --------------------------------------------------------------------------------

    private ModelPropertyViewMetaList getMetaDataPersonParticulars() {
        ModelPropertyViewMetaList metaDataList = new ModelPropertyViewMetaList(context);
        ModelPropertyViewMeta viewMeta;

        ArrayAdapter<CommunicationType> communicationTypeAdapter =
                new ArrayAdapter<CommunicationType>(context, android.R.layout.simple_list_item_1,
                        CommunicationType.values());

        try {
            //---NRIC/FIN
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_NRIC);
            viewMeta.setIncludeTagId(R.id.user_nric);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_PERSON_NRIC);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---Identification Type
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_IDENTIFICATION_TYPE);
            viewMeta.setIncludeTagId(R.id.user_id_type);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_ADULT_ID_TYPE);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---Passport No / Foreign ID
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_IDENTIFICATION_NO);
            viewMeta.setIncludeTagId(R.id.user_id_no);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_ADULT_ID_NO);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---Name
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_NAME);
            viewMeta.setIncludeTagId(R.id.user_name);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_PERSON_NAME);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---Nationality
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_NATIONALITY);
            viewMeta.setIncludeTagId(R.id.user_nationality);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_ADULT_NATIONALITY);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---Date of Birth
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_BIRTHDAY);
            viewMeta.setIncludeTagId(R.id.user_birthday);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.DATE);
            viewMeta.setSerialName(SerializedNames.SN_PERSON_BIRTHDAY);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---Mode of Communication
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_MODE_OF_COM);
            viewMeta.setIncludeTagId(R.id.user_mode_of_comunication);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DROP_DOWN);
            viewMeta.setSerialName(SerializedNames.SN_ADULT_MODE_OF_COMM);
            viewMeta.setDropDownAdapter(communicationTypeAdapter);
            viewMeta.setDropDownItemSelectedListener(
                    new ModeOfCommunicationSelectionChangeListener(communicationTypeAdapter));

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---Mobile Number
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_MOBILE_NO);
            viewMeta.setIncludeTagId(R.id.user_mobile);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.PHONE);
            viewMeta.setSerialName(SerializedNames.SN_ADULT_MOBILE);
            viewMeta.setMaxLength(BabyBonusConstants.LENGTH_ADULT_MOBILE);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---Email Address
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_EMAIL_ADDR);
            viewMeta.setIncludeTagId(R.id.user_email);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.EMAIL);
            viewMeta.setSerialName(SerializedNames.SN_ADULT_EMAIL);
            viewMeta.setMaxLength(BabyBonusConstants.LENGTH_ADULT_EMAIL);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---Address Type
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_ADDR_TYPE);
            viewMeta.setIncludeTagId(R.id.user_address_type);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_ADULT_ADDR_TYPE);

            metaDataList.add(ADULT_CLASS, viewMeta);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

    private ModelPropertyViewMetaList getMetaDataPersonAddress() {
        ModelPropertyViewMetaList metaDataList = new ModelPropertyViewMetaList(context);
        ModelPropertyViewMeta viewMeta;

        try {

            if(isAddressLocal) {
                //--Postal Code
                viewMeta = new ModelPropertyViewMeta(Address.FIELD_POSTAL_CODE);
                viewMeta.setIncludeTagId(R.id.user_local_postal_code);
                viewMeta.setMandatory(false);
                viewMeta.setEditable(false);
                viewMeta.setEditType(ModelPropertyEditType.TEXT);
                viewMeta.setSerialName(SerializedNames.SN_ADULT_ADDR_TYPE);

                metaDataList.add(ADDRESS_CLASS, viewMeta);

                //---Floor/Unit Number
                viewMeta = new ModelPropertyViewMeta(Address.FIELD_UNIT_NO);
                viewMeta.setIncludeTagId(R.id.user_local_unit_no);
                viewMeta.setMandatory(false);
                viewMeta.setEditable(false);
                viewMeta.setEditType(ModelPropertyEditType.TEXT);
                viewMeta.setSerialName(SerializedNames.SN_ADDRESS_UNIT_NO);

                metaDataList.add(ADDRESS_CLASS, viewMeta);

                //---Block / House Number
                viewMeta = new ModelPropertyViewMeta(Address.FIELD_BLOCK_HOUSE_NO);
                viewMeta.setIncludeTagId(R.id.user_local_block_no);
                viewMeta.setMandatory(false);
                viewMeta.setEditable(false);
                viewMeta.setEditType(ModelPropertyEditType.TEXT);
                viewMeta.setSerialName(SerializedNames.SN_ADDRESS_BLOCK_HOUSE_NO);

                metaDataList.add(ADDRESS_CLASS, viewMeta);

                //---Street Name
                viewMeta = new ModelPropertyViewMeta(Address.FIELD_STREET_NAME);
                viewMeta.setIncludeTagId(R.id.user_local_street_name_);
                viewMeta.setMandatory(false);
                viewMeta.setEditable(false);
                viewMeta.setEditType(ModelPropertyEditType.TEXT);
                viewMeta.setSerialName(SerializedNames.SN_ADDRESS_STREET);

                metaDataList.add(ADDRESS_CLASS, viewMeta);

                //---Building Name
                viewMeta = new ModelPropertyViewMeta(Address.FIELD_BUILDING_NAME);
                viewMeta.setIncludeTagId(R.id.user_local_building_name);
                viewMeta.setMandatory(false);
                viewMeta.setEditable(false);
                viewMeta.setEditType(ModelPropertyEditType.TEXT);
                viewMeta.setSerialName(SerializedNames.SN_ADDRESS_BUILDING);

                metaDataList.add(ADDRESS_CLASS, viewMeta);

            } else {

                //---Address 1
                viewMeta = new ModelPropertyViewMeta(Address.FIELD_ADDRESS1);
                viewMeta.setIncludeTagId(R.id.user_foreign_address1);
                viewMeta.setMandatory(false);
                viewMeta.setEditable(false);
                viewMeta.setEditType(ModelPropertyEditType.TEXT);
                viewMeta.setSerialName(SerializedNames.SN_ADDRESS_ADDRESS2);

                metaDataList.add(ADDRESS_CLASS, viewMeta);

                //---Address 2
                viewMeta = new ModelPropertyViewMeta(Address.FIELD_ADDRESS2);
                viewMeta.setIncludeTagId(R.id.user_foreign_address2);
                viewMeta.setMandatory(false);
                viewMeta.setEditable(false);
                viewMeta.setEditType(ModelPropertyEditType.TEXT);
                viewMeta.setSerialName(SerializedNames.SN_ADDRESS_ADDRESS1);

                metaDataList.add(ADDRESS_CLASS, viewMeta);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

    //--- DROPDOWN SELECTION LISTENERS  ------------------------------------------------------------

    private class ModeOfCommunicationSelectionChangeListener
            implements AdapterView.OnItemSelectedListener {
        private ArrayAdapter<CommunicationType> adapter;

        public ModeOfCommunicationSelectionChangeListener(ArrayAdapter<CommunicationType> adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            CommunicationType communicationType = adapter.getItem(position);

            if (communicationType.equals(CommunicationType.EMAIL)) {
                adultModelViewSynchronizer.setFieldMandatory(Adult.FIELD_EMAIL_ADDR, true);
                adultModelViewSynchronizer.setFieldMandatory(Adult.FIELD_MOBILE_NO, false);
            } else if (communicationType.equals(CommunicationType.SMS)) {
                adultModelViewSynchronizer.setFieldMandatory(Adult.FIELD_EMAIL_ADDR, false);
                adultModelViewSynchronizer.setFieldMandatory(Adult.FIELD_MOBILE_NO, true);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void afterSubmit(final boolean isClientSubmit, String message){

        MessageBoxButtonClickListener listener = new MessageBoxButtonClickListener() {
            @Override
            public void onClickPositiveButton(DialogInterface dialog, int id) {
                UpdateProfile updateProfile = app.getUpdateProfile();

                if(isClientSubmit) {
                    updateProfile.setDisplayValidationErrors(updateProfile.hasClientValidations());
                } else {
                    updateProfile.setDisplayValidationErrors(updateProfile.hasAnyValidations());
                }

                String errorMessage = displayValidationErrors();

                if (!StringHelper.isStringNullOrEmpty(errorMessage)) {
                    displayInstructions(errorMessage);
                }
                dialog.dismiss();
            }

            @Override
            public void onClickNegativeButton(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        };

        MessageBox.show(context, message, false, true, R.string.btn_ok, false, 0, listener);
    }

    private void showPopupWindow(final boolean isClientSubmit, String message) {
        MessagePopupButtonClickListener listener = new MessagePopupButtonClickListener() {
            @Override
            public void onClickPositiveButton(PopupWindow popupWindow) {
                UpdateProfile updateProfile = app.getUpdateProfile();

                if(isClientSubmit) {
                    updateProfile.setDisplayValidationErrors(updateProfile.hasClientValidations());
                } else {
                    updateProfile.setDisplayValidationErrors(updateProfile.hasAnyValidations());
                }

                String errorMessage = displayValidationErrors();

                if (!StringHelper.isStringNullOrEmpty(errorMessage)) {
                    displayInstructions(errorMessage);
                }
                popupWindow.dismiss();
            }

            @Override
            public void onClickNegativeButton(PopupWindow popupWindow) {
                popupWindow.dismiss();
            }
        };

        MessageBox.popup(context, message, rootView, true, R.string.btn_ok, false, 0, listener);
    }

    //--- ASYNC TASKS ------------------------------------------------------------------------------

    private class GetUserProfileAsyncTask extends AsyncTask<Void,Void,UpdateProfile>{

        private Context context;
        private ProgressDialog dialog;

        public GetUserProfileAsyncTask(Context context) {
            this. context = context;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(StringHelper.getStringByResourceId(context,
                    R.string.progress_home_get_user_profile));
            dialog.show();
        }

        @Override
        protected UpdateProfile doInBackground(Void... params) {
            try{
                return ProxyFactory.getOtherProxy().getUserProfile();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(UpdateProfile updateProfile) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            app.setUpdateProfile(new UpdateProfile(updateProfile.getUserDetail()));

            isAddressLocal = updateProfile.getUserDetail().getAddressType() == AddressType.LOCAL;

            displayData();
        }
    }

    private class UpdateUserProfileAsyncTask extends AsyncTask<Void,Void,ServerResponse>{

        private Context context;
        private ProgressDialog dialog;

        public UpdateUserProfileAsyncTask(Context context) {
            this. context = context;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(StringHelper.getStringByResourceId(context,
                    R.string.progress_home_set_user_profile));
            dialog.show();
        }

        @Override
        protected ServerResponse doInBackground(Void... params) {
            try{
                return ProxyFactory.getOtherProxy().updateUserProfile(app.getUpdateProfile());
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ServerResponse response) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if(response.getResponseType() == ServerResponseType.SUCCESS){
                MessageBox.show(context, response.getMessage(), false, true, R.string.btn_ok, false, 0, null);
                ((WebView) findViewById(R.id.wvErrorDesc)).setVisibility(View.GONE);

//                MessageBox.popup(context, response.getMessage(), rootView, true, R.string.btn_ok, false, 0, null);
//                ((WebView) findViewById(R.id.wvErrorDesc)).setVisibility(View.GONE);

            } else if(response.getResponseType() == ServerResponseType.APPLICATION_ERROR){
                MessageBox.show(context, StringHelper.getStringByResourceId(
                        context, R.string.error_common_application_error),
                        false, true, R.string.btn_ok, false, 0, null);

//                MessageBox.popup(context, StringHelper.getStringByResourceId(
//                        context, R.string.error_common_application_error),
//                        rootView, true, R.string.btn_ok, false, 0, null);
            } else {
                afterSubmit(false, response.getMessage());
            }
        }
    }
}
