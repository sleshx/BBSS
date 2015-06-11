package sg.gov.msf.bbss.view.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyEditType;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMeta;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMetaList;
import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.apputils.ui.BbssActionBarActivity;
import sg.gov.msf.bbss.apputils.ui.component.MessageBox;
import sg.gov.msf.bbss.apputils.ui.helper.MessageBoxButtonClickListener;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.validation.ValidationInfo;
import sg.gov.msf.bbss.apputils.validation.ValidationMessage;
import sg.gov.msf.bbss.logic.BabyBonusValidationHandler;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.logic.server.response.ServerResponse;
import sg.gov.msf.bbss.logic.server.response.ServerResponseType;
import sg.gov.msf.bbss.model.entity.childdata.ChildNric;
import sg.gov.msf.bbss.model.entity.people.Adult;
import sg.gov.msf.bbss.model.wizardbase.SiblingCheck;
import sg.gov.msf.bbss.view.MainActivity;

/**
 * Created by chuanhe
 * Fixed bugs and did some code reimplementation by bandaray
 */
public class SiblingCheckActivity extends BbssActionBarActivity {

    private static final int CURRENT_POSITION = 0;

    private static Class CHILD_NRIC_CLASS = ChildNric.class;


    private Context context;
    private View rootView;
    private BbssApplication app;

    private SiblingCheck siblingCheck = new SiblingCheck();
    private ModelViewSynchronizer<ChildNric> childNricModelViewSynchronizer;

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sibling_check);

        context = this;
        rootView = findViewById(R.id.slibing_check_layout);
        app = (BbssApplication) getApplication();

        getActionBar().hide();

        displayData(displayValidationErrors());
        setButtonClicks();
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

    private void displayData(String errorMessage) {
        childNricModelViewSynchronizer = new ModelViewSynchronizer<ChildNric>(
                CHILD_NRIC_CLASS, getMetaData(), rootView, AppConstants.EMPTY_STRING);

        ChildNric childNric = siblingCheck.getChildNric();

        if(childNric == null){
            childNric = new ChildNric();
        }

        childNricModelViewSynchronizer.setLabels();
        childNricModelViewSynchronizer.displayDataObject(childNric);

        //Screen - Page Title
        TextView textView4 = (TextView) findViewById(R.id.tvPageTitle);
        textView4.setText(R.string.title_activity_home_sibling_check);

        //Screen - Instructions and Error Messages
        displayInstructions(errorMessage);

        //Screen - Header
        ((TextView) findViewById(R.id.section_header)).setText(R.string.title_activity_home_sibling_check);
    }

    private String displayValidationErrors() {
        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;

        if(siblingCheck.isDisplayValidationErrors()){
            ValidationInfo validationInfo = siblingCheck.getPageSectionValidations(
                    CURRENT_POSITION, AppConstants.EMPTY_STRING);

            if(validationInfo.hasAnyValidationMessages()) {
                errorMessageList = validationInfo.getValidationMessages();
                childNricModelViewSynchronizer.displayValidationErrors(errorMessageList);

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
        setBackButtonClick();
        setCheckSiblinghoodButton();
    }
    private void setBackButtonClick() {
        Log.i(getClass().getName(), "----------setBackButtonClick()");

        ImageView backButton = (ImageView) rootView.findViewById(R.id.ivBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        });
    }

    private  void setCheckSiblinghoodButton(){
        LinearLayout button = (LinearLayout)findViewById(R.id.screen_buttons);
        Button check = (Button) button.findViewById(R.id.btnFirstInOne);
        check.setText(R.string.btn_check);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidNric()) {
                    new SiblingCheckTask(SiblingCheckActivity.this).execute();
                } else {
                    if (siblingCheck.hasClientValidations()) {
                        siblingCheck.setDisplayValidationErrors(true);

                        MessageBox.show(context, StringHelper.getStringByResourceId(
                                        context, R.string.error_common_form_not_properly_completed),
                                false, true, R.string.btn_ok, false, 0,
                                new MessageBoxButtonClickListener() {
                                    @Override
                                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onClickNegativeButton(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });

                        if (siblingCheck.getFirstErrorPage() == CURRENT_POSITION) {
                            String errorMessage = displayValidationErrors();
                            if (!StringHelper.isStringNullOrEmpty(errorMessage)) {
                                displayInstructions(errorMessage);
                            }
                        }
                    }
                }
            }
        });
    }

    //--- META DATA --------------------------------------------------------------------------------

    private ModelPropertyViewMetaList getMetaData() {

        ModelPropertyViewMetaList metaDataList = new ModelPropertyViewMetaList(context);
        ModelPropertyViewMeta viewMeta;

        try{
            viewMeta = new ModelPropertyViewMeta(ChildNric.FIELD_NRIC1);
            viewMeta.setIncludeTagId(R.id.silibing_check_child1);
            viewMeta.setLabelResourceId(R.string.label_sibling_check_child_1);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_CHILD_NRIC1);

            metaDataList.add(CHILD_NRIC_CLASS, viewMeta);

            viewMeta = new ModelPropertyViewMeta(ChildNric.FIELD_NRIC2);
            viewMeta.setIncludeTagId(R.id.silibing_check_child2);
            viewMeta.setLabelResourceId(R.string.label_sibling_check_child_2);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_CHILD_NRIC2);

            metaDataList.add(CHILD_NRIC_CLASS, viewMeta);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

    //--- HELPER -----------------------------------------------------------------------------------

    private void displayInstructions(String errorMessage) {
        WebView webView = (WebView) findViewById(R.id.wvInstructionDesc);
        webView.loadData(StringHelper.getJustifiedString(this, R.string.desc_home_sibling_check_instructions,
                R.color.theme_creme), "text/html", "utf-8");

        findViewById(R.id.tvInstructionTitle).setVisibility(View.GONE);
        findViewById(R.id.tvInstructionStepNo).setVisibility(View.GONE);

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

    private boolean isValidNric() {
        ValidationInfo validationInfo;

        siblingCheck.clearClientPageValidations(CURRENT_POSITION);

        validationInfo = childNricModelViewSynchronizer.getValidationInfo();
        siblingCheck.setChildNric(childNricModelViewSynchronizer.getDataObject());

        BabyBonusValidationHandler.validateNricFormat(context, siblingCheck.getChildNric(), validationInfo);

        if(validationInfo.hasAnyValidationMessages()){
            siblingCheck.addPageValidation(CURRENT_POSITION, validationInfo);
            childNricModelViewSynchronizer.displayValidationErrors(
                    validationInfo.getValidationMessages());
        }

        return !validationInfo.hasAnyValidationMessages();
    }

    //--- ASYNC TASK -------------------------------------------------------------------------------

    private class SiblingCheckTask extends AsyncTask<Void, Void, ServerResponse> {

        private Context context;
        private ProgressDialog dialog;

        public SiblingCheckTask(Context context) {
            this. context = context;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(StringHelper.getStringByResourceId(context,
                    R.string.progress_home_sibling_check));
            dialog.show();
        }

        @Override
        protected ServerResponse doInBackground(Void... params) {
            return ProxyFactory.getOtherProxy().checkSiblingHood(siblingCheck);
        }

        protected void onPostExecute(ServerResponse response) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if(response.getResponseType() == ServerResponseType.SUCCESS){
                MessageBox.show(context, response.getMessage(), false, true, R.string.btn_ok, false, 0, null);
                ((WebView) findViewById(R.id.wvErrorDesc)).setVisibility(View.GONE);
            } else {
                MessageBox.show(context, response.getMessage(), false, true, R.string.btn_ok, false, 0, null);
            }
        }
    }
}
