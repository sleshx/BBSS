package sg.gov.msf.bbss.view.enrolment.sub;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.FragmentContainerActivityHelper;
import sg.gov.msf.bbss.logic.SupportingDocumentsHelper;
import sg.gov.msf.bbss.logic.masterdata.MasterDataListener;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.server.response.ServerResponse;
import sg.gov.msf.bbss.logic.server.response.ServerResponseType;
import sg.gov.msf.bbss.logic.type.ChildRegistrationType;
import sg.gov.msf.bbss.logic.type.EnrolmentAppType;
import sg.gov.msf.bbss.logic.type.YesNoType;
import sg.gov.msf.bbss.model.entity.childdata.ChildRegistration;
import sg.gov.msf.bbss.model.entity.common.SupportingFile;
import sg.gov.msf.bbss.model.entity.people.Child;
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentChildRegistration;
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentForm;

public class EnrolmentChildActivity extends Activity  {

    private static int CURRENT_POSITION = 100;

    private static Class CHILD_CLASS = Child.class;

    public static int SUPPORTING_DOC_REQUEST_CODE = 0;
    public static int POST_BIRTH_REQUEST_CODE = 1;
    public static int CITIZENSHIP_REQUEST_CODE = 2;

    private View rootView;
    private Context context;
    private BbssApplication app;

    private ModelViewSynchronizer<Child> childModelViewSynchronizer;
    private ChildRegistrationType registrationType;

    private List<Map<String,Bitmap>> image = new ArrayList<Map<String,Bitmap>>();
    private Map<String,Bitmap> map = new HashMap<String, Bitmap>();
    private List<SupportingFile> supportingFiles = new ArrayList<SupportingFile>();

    private boolean isViewEditMode = false;
    private int position;
    //private Child currentChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_child);

        rootView = this.findViewById(android.R.id.content);
        context = this;
        app = (BbssApplication) getApplication();
        getActionBar().hide();

        registrationType = (ChildRegistrationType) getIntent().getSerializableExtra(
                BabyBonusConstants.ENROLMENT_CHILD_REGISTRATION_TYPE);
        isViewEditMode = getIntent().getBooleanExtra(BabyBonusConstants.ENROLMENT_IS_VIEW_EDIT_MODE,false);
        position = getIntent().getIntExtra(BabyBonusConstants.ENROLMENT_SELECTED_LIST_POSITION,0);

        displayData(displayValidationErrors());
        setButtonClicks();
    }

    //--- ACTIVITY NAVIGATION ----------------------------------------------------------------------

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(getClass().getName() , "----------onActivityResult()");

        if(requestCode == SUPPORTING_DOC_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK && data != null) {
                boolean isShowDelete = !app.getEnrolmentForm().getAppType().equals(EnrolmentAppType.VIEW);

                SupportingDocumentsHelper docHelper = new SupportingDocumentsHelper(rootView,
                        context, this.getLayoutInflater());
                docHelper.generateUploadFileList(data, isShowDelete,
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

        setBackButtonClick();
        setSaveButtonClick();
        setResetButtonClick();
        setCancelButtonClick();
        setBrowseDocButtonClick();
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    private void displayData(String errorMessage) {
        //Section - Activity Title
        TextView title = (TextView) findViewById(R.id.tvPageTitle);
        title.setText(StringHelper.getStringByResourceId(context,
                R.string.title_activity_enrolment_sub_add_child));

        //Section - Instructions
        displayInstructions(errorMessage);

        //Populate Data
        childModelViewSynchronizer = new ModelViewSynchronizer<Child>(Child.class,
                        getMetaData(), this.findViewById(android.R.id.content),
                        SerializedNames.SEC_ENROLMENT_ENROL_CHILD);
        childModelViewSynchronizer.setLabels();
        childModelViewSynchronizer.setHeaderTitle(R.id.section_add_child, R.string.label_child);

        if (isViewEditMode){
            int selectedIndex = getIntent().getIntExtra(BabyBonusConstants.ENROLMENT_SELECTED_LIST_POSITION, 0);
            Child currentChild = app.getEnrolmentForm().getChildRegistration().getChildren().get(selectedIndex);

            childModelViewSynchronizer.displayDataObject(currentChild);
            addExistingSupportingFiles(app.getEnrolmentForm().getChildRegistration(), currentChild);
        }else {
            childModelViewSynchronizer.displayDataObject(new Child());
        }

        //Unwanted UI
        hideUnwantedIncludeLayouts();

        //Section - Supporting Documents
        TextView supDocHeader = (TextView) findViewById(R.id.document_listing_section_header);
        supDocHeader.setText(R.string.label_supporting_doc);

        WebView wvDeclarationDesc = (WebView) findViewById(
                R.id.wvScreenDescription);
        wvDeclarationDesc.loadData(StringHelper.getJustifiedString(context,
                R.string.label_enrolment_post_birth_child_supporting_doc_desc,
                R.color.theme_gray_default_bg), "text/html", "utf-8");
    }

    private String displayValidationErrors() {
        EnrolmentChildRegistration enrolmentChildReg = app.getEnrolmentChildRegistration();

        if(enrolmentChildReg == null){
            enrolmentChildReg = new EnrolmentChildRegistration();
            app.setEnrolmentChildRegistration(enrolmentChildReg);
        }

        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;

        if(enrolmentChildReg.isDisplayValidationErrors()){
            ValidationInfo validationInfo = enrolmentChildReg.getPageSectionValidations(
                    CURRENT_POSITION, SerializedNames.SEC_ENROLMENT_ENROL_CHILD);

            if(validationInfo.hasAnyValidationMessages()) {
                errorMessageList = validationInfo.getValidationMessages();
                childModelViewSynchronizer.displayValidationErrors(errorMessageList);

                for(ValidationMessage messageList : errorMessageList) {
                    errorMessage = errorMessage + messageList.getMessage() +
                            AppConstants.SYMBOL_BREAK_LINE;
                }
            }
        }
        return errorMessage;
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    public void setBackButtonClick() {
        ImageView backButton = (ImageView) findViewById(R.id.ivBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setSaveButtonClick() {
        Button saveButton = (Button) findViewById(R.id.btnFirstInThree);
        saveButton.setText(StringHelper.getStringByResourceId(context, R.string.btn_save));
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChildRegistration childRegistration = app.getEnrolmentForm().getChildRegistration();
                Child child = childModelViewSynchronizer.getDataObject();

                app.setEnrolmentChildRegistration(new EnrolmentChildRegistration(childRegistration));
                final EnrolmentChildRegistration enrolmentChildReg = app.getEnrolmentChildRegistration();

                ValidationInfo validationInfo = childModelViewSynchronizer.getValidationInfo();

                enrolmentChildReg.clearClientPageValidations(CURRENT_POSITION);

                if (validationInfo.hasAnyValidationMessages()) {
                    enrolmentChildReg.addPageValidation(CURRENT_POSITION, validationInfo);
                }

                final boolean hasValidationErrors = enrolmentChildReg.hasClientValidations();

                if (hasValidationErrors) {
                    MessageBox.show(context, StringHelper.getStringByResourceId(
                                    context, R.string.error_common_form_not_properly_completed),
                            false, true, R.string.btn_ok, false, 0,
                            new MessageBoxButtonClickListener() {
                                @Override
                                public void onClickPositiveButton(DialogInterface dialog, int id) {
                                    enrolmentChildReg.setDisplayValidationErrors(hasValidationErrors);
                                    String errorMessage = displayValidationErrors();

                                    if (!StringHelper.isStringNullOrEmpty(errorMessage)) {
                                        displayInstructions(errorMessage);
                                    }

                                    if (enrolmentChildReg != null) {
                                        app.setEnrolmentChildRegistration(null);
                                    }
                                    dialog.dismiss();
                                }

                                @Override
                                public void onClickNegativeButton(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                } else {
                    if (registrationType.equals(ChildRegistrationType.POST_BIRTH)) { //TODO: Workaround to fix a bug
                        child.setBornOnOverseas(YesNoType.NO);
                    }

                    int attachedFileCount = supportingFiles.size();
                    if (attachedFileCount > 0 ) {
                        child.setSupportingFiles(supportingFiles.toArray(new SupportingFile[attachedFileCount]));
                    }

                    childRegistration.setRegistrationType(registrationType);
                    if (isViewEditMode){
                        childRegistration.getChildren().set(position, child);
                    }else {
                        childRegistration.getChildren().add(child);
                    }

                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    public void setResetButtonClick() {
        Button resetButton = (Button) findViewById(R.id.btnSecondInThree);
        resetButton.setText(StringHelper.getStringByResourceId(context, R.string.btn_reset));
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setCancelButtonClick() {
        Button cancelButton = (Button) findViewById(R.id.btnThirdInThree);
        cancelButton.setText(StringHelper.getStringByResourceId(context, R.string.btn_cancel));
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setBrowseDocButtonClick() {
        Log.i(getClass().getName() , "----------setBrowseDocButtonClick()");

        LinearLayout llSupDocSection = (LinearLayout)findViewById(R.id.supporting_docs);

        Button browseButton = (Button)llSupDocSection.findViewById(R.id.btnFirstInOne);
        browseButton.setText(R.string.btn_browse);
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picture = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(picture, SUPPORTING_DOC_REQUEST_CODE);
            }
        });
    }

    //--- META DATA --------------------------------------------------------------------------------

    private ModelPropertyViewMetaList getMetaData() {
        ModelPropertyViewMetaList metaDataList = new ModelPropertyViewMetaList(context);
        ModelPropertyViewMeta viewMeta;

        EnrolmentForm enrolmentForm = app.getEnrolmentForm();
        boolean isFocusable = !enrolmentForm.isPrePopulated();
        boolean isViewMode = enrolmentForm.getAppType().equals(EnrolmentAppType.VIEW);

        if (isFocusable && isViewMode) {
            isFocusable = false;
        }

        ArrayAdapter<YesNoType> yesNoTypeArrayAdapter =
                new ArrayAdapter<YesNoType>(context, android.R.layout.simple_list_item_1,
                        YesNoType.values());

        try {
            //---NAME
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_NAME);
            viewMeta.setIncludeTagId(R.id.edit_registration_child_name);
            viewMeta.setLabelResourceId(R.string.label_child_name);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_PERSON_NAME);
            viewMeta.setFocusable(isFocusable);

            metaDataList.add(CHILD_CLASS, viewMeta);

            //--- BORN OVERSEAS
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_IS_BORN_OVERSEAS);
            viewMeta.setIncludeTagId(R.id.edit_registration_child_born);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DROP_DOWN);
            viewMeta.setSerialName(SerializedNames.SN_CHILD_IS_BORN_OVERSEAS);
            viewMeta.setDropDownAdapter(yesNoTypeArrayAdapter);
            viewMeta.setFocusable(isFocusable);

            metaDataList.add(CHILD_CLASS, viewMeta);

            //---BIRTH CERTIFICATE NO.
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_BIRTH_CERT_NO);
            viewMeta.setIncludeTagId(R.id.edit_registration_child_certificate_no);
            viewMeta.setLabelResourceId(R.string.label_child_birth_certificate_no);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_CHILD_BIRTH_CERT_NO);
            viewMeta.setFocusable(isFocusable);

            metaDataList.add(CHILD_CLASS, viewMeta);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

    //--- HELPERS ----------------------------------------------------------------------------------

    private void displayInstructions(String errorMessage) {
        FragmentContainerActivityHelper fragmentHelper = new FragmentContainerActivityHelper(context);
        fragmentHelper.setFragmentInstructions(this.findViewById(android.R.id.content), 0, 0,
                R.string.label_enrolment_child_registration_instruction,
                true, 1, errorMessage);

        //Screen - Error Message
        WebView wvError = (WebView) findViewById(R.id.wvErrorDesc);
        if (errorMessage != AppConstants.EMPTY_STRING) {
            wvError.setVisibility(View.VISIBLE);
            wvError.loadDataWithBaseURL(null, StringHelper.getJustifiedErrorString(context,
                    errorMessage, R.color.theme_creme),"text/html", "utf-8", null);
        } else {
            wvError.setVisibility(View.GONE);
        }
    }

    private void hideUnwantedIncludeLayouts() {
        if (registrationType.equals(ChildRegistrationType.POST_BIRTH)) {
            findViewById(R.id.edit_registration_child_born).setVisibility(View.GONE);
        } else if (registrationType.equals(ChildRegistrationType.CITIZENSHIP)) {
            findViewById(R.id.edit_registration_child_born).setVisibility(View.VISIBLE);
        }
    }

    private void addExistingSupportingFiles(ChildRegistration childRegistration, Child currentChild){
        ChildRegistrationType type = childRegistration.getRegistrationType();
        if (type != null){
            if (type == ChildRegistrationType.POST_BIRTH) {
                SupportingFile[] files = currentChild.getSupportingFiles();
                if (files != null && files.length > 0) {
                    supportingFiles = Arrays.asList(files);
                }

                if (supportingFiles.size() > 0) {
                    SupportingDocumentsHelper helper = new SupportingDocumentsHelper(rootView,
                            context, this.getLayoutInflater());
                    helper.removeDocumentItemsView();
                    for (SupportingFile file : supportingFiles) {
                        boolean isShowDelete = !app.getEnrolmentForm().getAppType().equals(EnrolmentAppType.VIEW);
                        String displayName = StringHelper.isStringNullOrEmpty(file.getFileName()) ?
                                file.getCode() : file.getFileName();
                        helper.addDocumentItemView(displayName, isShowDelete);
                    }
                }
            }
        }
    }
}
