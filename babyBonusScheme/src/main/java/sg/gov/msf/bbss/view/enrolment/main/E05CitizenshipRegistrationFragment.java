package sg.gov.msf.bbss.view.enrolment.main;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
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
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.validation.ValidationInfo;
import sg.gov.msf.bbss.apputils.validation.ValidationMessage;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.BabyBonusValidationHandler;
import sg.gov.msf.bbss.logic.SupportingDocumentsHelper;
import sg.gov.msf.bbss.logic.adapter.enrolment.EnrolmentChildListAdapter;
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
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentForm;
import sg.gov.msf.bbss.view.MainActivity;
import sg.gov.msf.bbss.view.enrolment.sub.EnrolmentChildActivity;

/**
 * Created by bandaray
 */
public class E05CitizenshipRegistrationFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;

    private static Class CHILD_REG_CLASS = ChildRegistration.class;

    public static int SUPPORTING_DOC_REQUEST_CODE = 0;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private ListView listView;
    private View listFooterView;
    private View listHeaderView;

    private EnrolmentFragmentContainerActivity fragmentContainer;
    private ModelViewSynchronizer<ChildRegistration> childRegModelViewSynchronizer;
    private List<SupportingFile> supportingFiles = new ArrayList<SupportingFile>();
    private EnrolmentChildListAdapter adapter;

    private boolean isHeaderLoaded;
    private boolean isFooterLoaded;

    private boolean isRegInSingapore;
    private boolean isMarried;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static E05CitizenshipRegistrationFragment newInstance(int index) {
        E05CitizenshipRegistrationFragment fragment = new E05CitizenshipRegistrationFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Log.i(getClass().getName(), "----------onCreateView()");

        rootView = inflater.inflate(R.layout.layout_listview, null);

        context = getActivity();
        app = (BbssApplication) getActivity().getApplication();
        fragmentContainer = ((EnrolmentFragmentContainerActivity) getActivity());

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        listView = (ListView) rootView.findViewById(R.id.lvMain);
        isHeaderLoaded = false;
        isFooterLoaded = false;

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        EnrolmentForm enrolmentForm = app.getEnrolmentForm();
        ChildRegistration childRegistration = childRegModelViewSynchronizer.getDataObject();
        ValidationInfo validationInfo = childRegModelViewSynchronizer.getValidationInfo();

        if(adapter.getCount() == 0 &&
                childRegistration.getRegistrationType() == ChildRegistrationType.CITIZENSHIP){
            childRegistration.setRegistrationType(null);
        } else if(adapter.getCount() > 0 &&
                childRegistration.getRegistrationType() == null){
            childRegistration.setRegistrationType(ChildRegistrationType.CITIZENSHIP);
        } else if (childRegistration.getRegistrationType() == null && isValidationRequired) {
            BabyBonusValidationHandler.validateIfAtLeastOneChildRegistrationAdded(context, validationInfo);
        }

        int attachedFileCount = supportingFiles.size();
        if (attachedFileCount > 0 &&
                childRegistration.getRegistrationType() == ChildRegistrationType.CITIZENSHIP) {
            childRegistration.setSupportingFiles(supportingFiles.toArray(new SupportingFile[attachedFileCount]));
        }

        enrolmentForm.setChildRegistration(childRegistration);
        enrolmentForm.clearClientPageValidations(CURRENT_POSITION);
        enrolmentForm.addSectionPage(SerializedNames.SEC_ENROLMENT_ENROL_CHILD_CITIZENSHIP_BIRTH, CURRENT_POSITION);

        if(adapter.getCount() > 0) {
            if(!childRegistration.isMarried()) {
                //TODO:BabyBonusValidationHandler.validateIfParentsMarried()
                MessageBox.show(context,
                        StringHelper.getStringByResourceId(context, R.string.error_enrolment_you_should_be_married_proceed),
                        false, true, R.string.btn_ok, true, R.string.btn_cancel,
                        new MessageBoxButtonClickListener() {
                            @Override
                            public void onClickPositiveButton(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }

                            @Override
                            public void onClickNegativeButton(DialogInterface dialog, int id) {
                                app.setEnrolmentForm(null);
                                context.startActivity(new Intent(context, MainActivity.class));
                                dialog.dismiss();
                            }
                        });
                return true;
            }

            if (childRegistration.isMarried() || !childRegistration.isRegisteredInSingapore()) {
                BabyBonusValidationHandler.validateIfAtLeastOneDocAttached(context,
                        getActivity().getLayoutInflater(), rootView, validationInfo);
            }
        }

        if (validationInfo.hasAnyValidationMessages()) {
            enrolmentForm.addPageValidation(CURRENT_POSITION, validationInfo);
        }

        return false;
    }

    @Override
    public boolean onResumeFragment() {
        Log.i(getClass().getName() , "----------onResumeFragment()");

        childRegModelViewSynchronizer = new ModelViewSynchronizer<ChildRegistration>(
                ChildRegistration.class, getMetaData(), rootView,
                SerializedNames.SEC_ENROLMENT_ENROL_CHILD_CITIZENSHIP_BIRTH);

        displayData(displayValidationErrors());
        setButtonClicks();

        return false;
    }

    //--- ACTIVITY NAVIGATION ----------------------------------------------------------------------

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(getClass().getName() , "----------onActivityResult()");

        if (requestCode == EnrolmentChildActivity.CITIZENSHIP_REQUEST_CODE) {
            populateListView(app.getEnrolmentForm().getChildRegistration());
        } else if(requestCode == SUPPORTING_DOC_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK && data != null) {
                boolean isShowDelete = !app.getEnrolmentForm().getAppType().equals(EnrolmentAppType.VIEW);

                SupportingDocumentsHelper docHelper = new SupportingDocumentsHelper(rootView,
                        context, getActivity().getLayoutInflater());
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
        Log.i(getClass().getName() , "----------setButtonClicks()");

        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, false);
        fragmentContainer.setNextButtonClick(rootView, R.id.btnFirstInThree, CURRENT_POSITION, true);
        fragmentContainer.setSaveAsDraftButtonClick(rootView, R.id.btnSecondInThree);
        fragmentContainer.setCancelButtonClick(rootView, R.id.btnThirdInThree);

        setAddChildButtonClick();
        setBrowseDocButtonClick();
    }

    private void setBrowseDocButtonClick() {
        Log.i(getClass().getName() , "----------setBrowseDocButtonClick()");

        LinearLayout llSupDocSection = (LinearLayout)rootView.findViewById(R.id.supporting_docs);

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

    private void setAddChildButtonClick() {
        Log.i(getClass().getName() , "----------setAddChildButtonClick()");

        Button addChildButton = (Button) rootView.findViewById(R.id.btnFirstInOne);
        addChildButton.setText(R.string.btn_add_child);
        addChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChildRegistrationType registrationType = app.getEnrolmentForm()
                        .getChildRegistration().getRegistrationType();

                if(registrationType != null &&
                        registrationType != ChildRegistrationType.CITIZENSHIP) {
                    String message = getString(R.string.error_enrolment_allowed_only_one_registration,
                            registrationType.getDisplayName(context));
                    MessageBox.show(context, message, false, true, R.string.btn_ok, false, 0, null);
                } else {
                    Intent intent = new Intent(context, EnrolmentChildActivity.class);
                    intent.putExtra(BabyBonusConstants.ENROLMENT_CHILD_REGISTRATION_TYPE,
                            ChildRegistrationType.CITIZENSHIP);

                    startActivityForResult(intent, EnrolmentChildActivity.CITIZENSHIP_REQUEST_CODE);
                }
            }
        });

        if (!isAddChildAllowed()) {
            addChildButton.setVisibility(View.GONE);
        }
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    private void displayData(String errorMessage) {
        Log.i(getClass().getName() , "----------displayData()");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        ChildRegistration childRegistration = app.getEnrolmentForm().getChildRegistration();

        if(childRegistration == null){
            childRegistration = new ChildRegistration();
        }

        listHeaderView = inflater.inflate(
                R.layout.section_header_row_and_instruction_row, null);
        listFooterView = (LinearLayout) inflater.inflate(
                R.layout.fragment_enrolment_other_birth_footer, null);

        //Listview - Header
        if (!isHeaderLoaded) {
            listView.addHeaderView(listHeaderView);
            isHeaderLoaded = true;
        }

        //Listview - Footer
        if (! isFooterLoaded) {
            listView.addFooterView(listFooterView);
            isFooterLoaded = true;
        }

        //Listview - Populate Data
        TextView tvHeader = (TextView) listHeaderView.findViewById(R.id.section_header);
        tvHeader.setText(R.string.label_child_reg_type_citizenship);
        populateListView(childRegistration);

        //Screen - Title and Instructions
        fragmentContainer.setFragmentTitle(rootView);
        if(isAddChildAllowed()) {
            fragmentContainer.setInstructions(listHeaderView, CURRENT_POSITION,
                    R.string.label_enrolment_fill_section_below_child_citizenship, true,
                    errorMessage);
        } else {
            fragmentContainer.setInstructions(listHeaderView, CURRENT_POSITION,
                    R.string.label_enrolment_fill_section_below_child_citizenship_pre_pop, true,
                    errorMessage);
        }

        //Section - Questions
        childRegModelViewSynchronizer.setLabels();
        childRegModelViewSynchronizer.displayDataObject(childRegistration);

        //Section - Supporting Documents
        TextView supDocHeader = (TextView)rootView.findViewById(R.id.document_listing_section_header);
        supDocHeader.setText(R.string.label_supporting_doc);

        WebView wvDeclarationDesc = (WebView) listFooterView.findViewById(
                R.id.wvScreenDescription);
        wvDeclarationDesc.loadData(StringHelper.getJustifiedString(context,
                R.string.label_enrolment_citizenship_adult_supporting_doc_desc,
                R.color.theme_gray_default_bg), "text/html", "utf-8");

        addExistingSupportingFiles(childRegistration);
    }

    private String displayValidationErrors() {
        EnrolmentForm enrolmentForm = app.getEnrolmentForm();
        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;

        if(enrolmentForm.isDisplayValidationErrors()){
            ValidationInfo validationInfo = enrolmentForm.getPageSectionValidations(
                    CURRENT_POSITION, SerializedNames.SEC_ENROLMENT_ENROL_CHILD_CITIZENSHIP_BIRTH);

            if(validationInfo.hasAnyValidationMessages()) {
                errorMessageList = validationInfo.getValidationMessages();
                childRegModelViewSynchronizer.displayValidationErrors(errorMessageList);

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
            //---IS MARRIED
            viewMeta = new ModelPropertyViewMeta(ChildRegistration.FIELD_REG_IS_MARRIED);
            viewMeta.setIncludeTagId(R.id.edit_is_married);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DROP_DOWN);
            viewMeta.setSerialName(SerializedNames.SN_CHILD_REG_IS_MARRIED);
            viewMeta.setDropDownAdapter(yesNoTypeArrayAdapter);
            viewMeta.setDropDownItemSelectedListener(
                    new IsMarriedSelectionChangeListener(yesNoTypeArrayAdapter));
            viewMeta.setFocusable(isFocusable);

            metaDataList.add(CHILD_REG_CLASS, viewMeta);

            //---IS REGISTERED IN SINGAPORE
            viewMeta = new ModelPropertyViewMeta(ChildRegistration.FIELD_REG_IS_REG_IN_SINGAPORE);
            viewMeta.setIncludeTagId(R.id.edit_is_marriage_reg_in_singapore);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DROP_DOWN);
            viewMeta.setSerialName(SerializedNames.SN_CHILD_REG_IS_MARRIAGE_REG_IN_SINGAPORE);
            viewMeta.setDropDownAdapter(yesNoTypeArrayAdapter);
            viewMeta.setDropDownItemSelectedListener(
                    new IsRegOInSingaporeSelectionChangeListener(yesNoTypeArrayAdapter));
            viewMeta.setFocusable(isFocusable);

            metaDataList.add(CHILD_REG_CLASS, viewMeta);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

    //--- HELPERS ----------------------------------------------------------------------------------

    private void populateListView(ChildRegistration childRegistration) {
        ArrayList<Child> children =
                childRegistration.getRegistrationType() == ChildRegistrationType.CITIZENSHIP ?
                        childRegistration.getChildren() : new ArrayList<Child>();
        adapter = new EnrolmentChildListAdapter(context, app, ChildRegistrationType.CITIZENSHIP,
                R.layout.layout_add_child_item, children, null);

        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void enableDisableSupportingDocSection(int visibility) {
        LinearLayout llSupDocSection = (LinearLayout)rootView.findViewById(R.id.supporting_docs);

        ((TextView)rootView.findViewById(R.id.document_listing_section_header)).setVisibility(visibility);
        ((WebView) listFooterView.findViewById(R.id.wvScreenDescription)).setVisibility(visibility);
        ((Button)llSupDocSection.findViewById(R.id.btnFirstInOne)).setVisibility(visibility);
    }

    private boolean isAddChildAllowed() {
        EnrolmentForm enrolmentForm = app.getEnrolmentForm();
        boolean isAllowedToAddChild = !enrolmentForm.isPrePopulated();

        if (isAllowedToAddChild && enrolmentForm.getAppType().equals(EnrolmentAppType.VIEW)) {
            isAllowedToAddChild = false;
        }
        return isAllowedToAddChild;
    }

    private void addExistingSupportingFiles(ChildRegistration childRegistration){
        ChildRegistrationType type = childRegistration.getRegistrationType();
        if (type != null){
            if (type == ChildRegistrationType.CITIZENSHIP) {
                SupportingFile[] files = childRegistration.getSupportingFiles();
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
        }
    }

    private class IsMarriedSelectionChangeListener implements AdapterView.OnItemSelectedListener {

        private ArrayAdapter<YesNoType> adapter;
        private ChildRegistration childRegistration;

        public IsMarriedSelectionChangeListener(ArrayAdapter<YesNoType> adapter) {
            this.adapter = adapter;
            this.childRegistration = app.getEnrolmentForm().getChildRegistration();
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            YesNoType type = adapter.getItem(position);
            isMarried = type == YesNoType.YES;
            if (!isMarried && isRegInSingapore){
                enableDisableSupportingDocSection(View.GONE);
            } else {
                enableDisableSupportingDocSection(View.VISIBLE);
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class IsRegOInSingaporeSelectionChangeListener implements AdapterView.OnItemSelectedListener {

        private ArrayAdapter<YesNoType> adapter;
        private ChildRegistration childRegistration;

        public IsRegOInSingaporeSelectionChangeListener(ArrayAdapter<YesNoType> adapter) {
            this.adapter = adapter;
            this.childRegistration = app.getEnrolmentForm().getChildRegistration();
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            YesNoType type = adapter.getItem(position);
            isRegInSingapore = type == YesNoType.YES;
            if (!isMarried && isRegInSingapore){
                enableDisableSupportingDocSection(View.GONE);
            } else {
                enableDisableSupportingDocSection(View.VISIBLE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}

