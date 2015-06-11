package sg.gov.msf.bbss.view.eservice.bo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyEditType;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMeta;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMetaList;
import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.apputils.ui.component.MessageBox;
import sg.gov.msf.bbss.apputils.ui.helper.MessageBoxButtonClickListener;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.validation.ValidationInfo;
import sg.gov.msf.bbss.apputils.validation.ValidationMessage;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.SupportingDocumentsHelper;
import sg.gov.msf.bbss.logic.masterdata.MasterDataListener;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.logic.server.response.ServerResponse;
import sg.gov.msf.bbss.logic.server.response.ServerResponseType;
import sg.gov.msf.bbss.logic.type.ChildListType;
import sg.gov.msf.bbss.logic.type.EnrolmentAppType;
import sg.gov.msf.bbss.model.entity.common.SupportingFile;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceChangeBo;
import sg.gov.msf.bbss.view.eservice.ServicesHelper;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class S03ChangeBoDeclarationFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;

    public int SUPPORTING_DOC_REQUEST_CODE;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private ModelViewSynchronizer<ServiceChangeBo> chaneBoModelViewSynchronizer;
    private List<SupportingFile> supportingFiles = new ArrayList<SupportingFile>();
    private Map<String,Bitmap> map = new HashMap<String, Bitmap>();
    private ChangeBoFragmentContainerActivity fragmentContainer;
    private ServicesHelper servicesHelper;

    private EditText reason;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static S03ChangeBoDeclarationFragment newInstance(int index) {
        S03ChangeBoDeclarationFragment fragment = new S03ChangeBoDeclarationFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_service_change_bo_submission,
                container, false);

        context = getActivity();
        app = (BbssApplication) getActivity().getApplication();
        fragmentContainer = ((ChangeBoFragmentContainerActivity) getActivity());
        servicesHelper = new ServicesHelper(context);

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        ServiceChangeBo updateBirthOrder = chaneBoModelViewSynchronizer.getDataObject();
        ValidationInfo validationInfo = chaneBoModelViewSynchronizer.getValidationInfo();

        updateBirthOrder.clearClientPageValidations(CURRENT_POSITION);
        updateBirthOrder.addSectionPage(SerializedNames.SEC_SERVICE_CHANGE_BO_REASON, CURRENT_POSITION);

        if (validationInfo.hasAnyValidationMessages()){
            updateBirthOrder.addPageValidation(CURRENT_POSITION, validationInfo);
        }

        return false;
    }

    @Override
    public boolean onResumeFragment() {
        chaneBoModelViewSynchronizer = new ModelViewSynchronizer<ServiceChangeBo>(
                ServiceChangeBo.class, getMetaData(), rootView,
                SerializedNames.SEC_SERVICE_CHANGE_BO_REASON);

        displayData(displayValidationErrors());
        setButtonClicks();

        return false;
    }

    //--- ACTIVITY NAVIGATION ----------------------------------------------------------------------

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(getClass().getName(), "----------onActivityResult()");

        if(requestCode == SUPPORTING_DOC_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK && data != null) {
                SupportingDocumentsHelper docHelper = new SupportingDocumentsHelper(rootView,
                        context, getActivity().getLayoutInflater());
                docHelper.generateUploadFileList(data, true,
                        new MasterDataListener<ServerResponse>() {
                            @Override
                            public void onMasterData(ServerResponse serverResponse) {
                                if (serverResponse.getResponseType() == ServerResponseType.SUCCESS) {
                                    SupportingFile supportingFile = new SupportingFile();
                                    supportingFile.setCode(serverResponse.getCode());
                                    supportingFile.setFileName(serverResponse.getFile().getName());

                                    supportingFiles.add(supportingFile);
                                }
                            }
                        });
            }
        }
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {
        Log.i(getClass().getName(), "----------setButtonClicks()");

        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, false);
        fragmentContainer.setCancelButtonClick(rootView, R.id.btnSecondInTwo);
        setSubmitButtonClick();
        setBrowseButtonClick();
    }

    private void setBrowseButtonClick() {
        LinearLayout linearLayout = (LinearLayout)rootView.findViewById(R.id.supporting_docs);
        Button btnBrowse = (Button)linearLayout.findViewById(R.id.btnFirstInOne);
        btnBrowse.setText(R.string.btn_browse);
        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picture = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(picture, 0);
            }
        });
    }

    private void setSubmitButtonClick() {
        Button next = (Button) rootView.findViewById(R.id.btnFirstInTwo);
        next.setText(R.string.btn_submit);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!onPauseFragment(true)) {
                    ServiceChangeBo updateBirthOrder = app.getServiceChangeBo();
                    updateBirthOrder.setReason(reason.getText().toString());
                    updateBirthOrder.setBitmaps(map);

                    if (updateBirthOrder.hasClientValidations()) {
                        afterSubmit(true);
                    } else if (!updateBirthOrder.isDeclared()) {
                        servicesHelper.createDeclarationRequiredMessageBox();
                    } else {
                        new UpdateTransferPseaTask(context).execute();
                    }
                }
            }
        });
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    private void displayData(String errorMessage) {
        chaneBoModelViewSynchronizer.displayDataObject(app.getServiceChangeBo());
        //Screen - Instructions and Title
        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(rootView, CURRENT_POSITION, 0, true, errorMessage);

        //Screen - One Filed TODO
        LinearLayout linearLayout1 = (LinearLayout)rootView.findViewById(R.id.edit_change_reason);
        TextView textView = (TextView)linearLayout1.findViewById(R.id.tvLabel);
        textView.setText("Reason for Request");
        reason = (EditText)linearLayout1.findViewById(R.id.etValue);

        //Screen - Supporting Document Section
        ((TextView)rootView.findViewById(R.id.document_listing_section_header)).setText(
                R.string.label_supporting_doc);
        addExistingSupportingFiles();

        //Screen - Declaration Section
        ((TextView)rootView.findViewById(R.id.section_declaration)).setText(
                R.string.label_common_declaration);

        LinearLayout llDeclaration = (LinearLayout)rootView.findViewById(R.id.declaration_1);

        WebView webView = (WebView)llDeclaration.findViewById(R.id.wvLabel);
        webView.loadData(StringHelper.getJustifiedString(context,
                R.string.desc_services_common_declaration_1, 0), "text/html", "utf-8");

        CheckBox checkBox = (CheckBox) llDeclaration.findViewById(R.id.cbValue);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                app.getServiceChangeBo().setDeclared(isChecked);
            }
        });
    }

    private String displayValidationErrors() {
        ServiceChangeBo serviceChangeBo = app.getServiceChangeBo();
        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;

        if(serviceChangeBo.isDisplayValidationErrors()){
            ValidationInfo validationInfo = serviceChangeBo.getPageSectionValidations(
                    CURRENT_POSITION, SerializedNames.SEC_SERVICE_CHANGE_BO_REASON);

            if(validationInfo.hasAnyValidationMessages()) {
                errorMessageList = validationInfo.getValidationMessages();
                chaneBoModelViewSynchronizer.displayValidationErrors(errorMessageList);

                for(ValidationMessage messageList : errorMessageList) {
                    errorMessage = errorMessage + messageList.getMessage() +
                            AppConstants.SYMBOL_BREAK_LINE;
                }
            }
        }

        return errorMessage;
    }

    //--- META DATA --------------------------------------------------------------------------------

    private ModelPropertyViewMetaList getMetaData() {
        ModelPropertyViewMetaList metaDataList = new ModelPropertyViewMetaList(context);
        ModelPropertyViewMeta viewMeta;

        try {
            //Change Reason
            viewMeta = new ModelPropertyViewMeta(ServiceChangeBo.FIELD_CHANGE_REASON);
            viewMeta.setIncludeTagId(R.id.edit_change_reason);
            viewMeta.setLabelResourceId(R.string.label_services_change_bo);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SEC_SERVICE_CHANGE_BO_REASON);

            metaDataList.add(ServiceChangeBo.class, viewMeta);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

    //--- HELPERS ----------------------------------------------------------------------------------

    private void addExistingSupportingFiles(){
        SupportingFile[] files = app.getServiceChangeBo().getSupportingFiles();
        if (files != null && files.length > 0) {
            supportingFiles = Arrays.asList(files);
        }

        if (supportingFiles.size() > 0) {
            SupportingDocumentsHelper helper = new SupportingDocumentsHelper(rootView,
                    context, getActivity().getLayoutInflater());
            helper.removeDocumentItemsView();
            for (SupportingFile file : supportingFiles) {
                boolean isShowDelete = !app.getEnrolmentForm().getAppType().equals(EnrolmentAppType.VIEW);
                String displayName = StringHelper.isStringNullOrEmpty(file.getFileName()) ?
                        file.getCode() : file.getFileName();
                helper.addDocumentItemView(displayName, isShowDelete);
            }
        }
    }

    private void afterSubmit(final boolean isClientSubmit){
        servicesHelper.createClientValidationIssuesMessageBox(new MessageBoxButtonClickListener() {
            @Override
            public void onClickPositiveButton(DialogInterface dialog, int id) {
                ServiceChangeBo updateBirthOrder = app.getServiceChangeBo();
                int firstErrorPage = updateBirthOrder.getFirstErrorPage();

                if (isClientSubmit) {
                    updateBirthOrder.setDisplayValidationErrors(updateBirthOrder.hasClientValidations());
                } else {
                    updateBirthOrder.setDisplayValidationErrors(updateBirthOrder.hasAnyValidations());
                }

                if (firstErrorPage == CURRENT_POSITION) {
                    String errorMessage = displayValidationErrors();

                    if (!StringHelper.isStringNullOrEmpty(errorMessage)) {
                        fragmentContainer.setInstructions(rootView, CURRENT_POSITION, 0, true, errorMessage);
                    }
                } else {
                    fragmentContainer.jumpToPageWithIndex(firstErrorPage);
                }
                dialog.dismiss();
            }

            @Override
            public void onClickNegativeButton(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
    }

    //--- ASYNC TASKS ------------------------------------------------------------------------------

    private class UpdateTransferPseaTask extends AsyncTask<Void, Void, ServerResponse> {

        private Context context;
        private ProgressDialog dialog;

        public UpdateTransferPseaTask(Context context) {
            this. context = context;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(StringHelper.getStringByResourceId(context,
                    R.string.progress_service_psea_updating));
            dialog.show();
        }

        @Override
        protected ServerResponse doInBackground(Void... params) {
            return ProxyFactory.getEServiceProxy().updateBirthOrder(app.getServiceChangeBo());
        }

        protected void onPostExecute(ServerResponse response) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if(response.getResponseType() == ServerResponseType.SUCCESS){
                servicesHelper.createServiceResponseMessageBox(app, response.getMessage(),
                        response.getAppId(), ChildListType.CHANGE_BO);
            } else if(response.getResponseType() == ServerResponseType.APPLICATION_ERROR){
                String message = StringHelper.getStringByResourceId(context, R.string.error_common_application_error);
                MessageBox.show(context, message, false, true, R.string.btn_ok, false, 0, null);
            } else {
                afterSubmit(false);
            }
        }
    }
}
