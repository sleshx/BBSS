package sg.gov.msf.bbss.view.enrolment.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyEditType;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMeta;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMetaList;
import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.validation.ValidationInfo;
import sg.gov.msf.bbss.apputils.validation.ValidationMessage;
import sg.gov.msf.bbss.logic.FragmentContainerActivityHelper;
import sg.gov.msf.bbss.logic.SupportingDocumentsHelper;
import sg.gov.msf.bbss.logic.masterdata.MasterDataListener;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.server.response.ServerResponse;
import sg.gov.msf.bbss.logic.server.response.ServerResponseType;
import sg.gov.msf.bbss.logic.type.ChildDeclarationType;
import sg.gov.msf.bbss.logic.type.MasterDataType;
import sg.gov.msf.bbss.model.entity.childdata.ChildDeclaration;
import sg.gov.msf.bbss.model.entity.common.SupportingFile;
import sg.gov.msf.bbss.model.entity.masterdata.GenericDataItem;
import sg.gov.msf.bbss.model.entity.people.Child;
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentForm;

/**
 * Created by bandaray
 */
public class MotherDeclarationDataEntryUtils {

    private static Class CHILD_CLASS = Child.class;
    private static Class CHILD_DECLARATION_CLASS = ChildDeclaration.class;

    public static int SUPPORTING_DOC_REQUEST_CODE = 0;

    private View rootView;
    private Context context;
    private Activity activity;
    private BbssApplication app;
    private LayoutInflater inflater;
    //private EnrolmentFragmentContainerActivity fragmentContainer;

    private ModelViewSynchronizer<Child> childModelViewSynchronizer;
    private ModelViewSynchronizer<ChildDeclaration> childDeclarationModelViewSynchronizer;
    private List<SupportingFile> supportingFiles = new ArrayList<SupportingFile>();
    private ChildDeclarationType childDeclarationType;
    private List<ChildDeclaration> childDeclarations;

    public MotherDeclarationDataEntryUtils(Activity activity) {
        this.activity = activity;
        this.inflater = activity.getLayoutInflater();
    }

    //--- CREATION ---------------------------------------------------------------------------------

    public View onCreateView() {
        rootView = inflater.inflate(R.layout.fragment_enrolment_mother_dec_data_entry, null);

        context = activity;
        app = (BbssApplication) activity.getApplication();

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    public boolean onPauseFragment(boolean isValidationRequired, int currentPosition) {

        EnrolmentForm enrolmentForm = app.getEnrolmentForm();
        Child child = childModelViewSynchronizer.getDataObject();
        ChildDeclaration childDeclaration = childDeclarationModelViewSynchronizer.getDataObject();
        ValidationInfo validationInfo;

        childDeclaration.setChild(child);
        ArrayList<ChildDeclaration> childDeclarationList = enrolmentForm.getChildDeclarations();
        childDeclarationList.add(childDeclaration);

        //enrolmentForm.setChildDeclarations(childDeclarationList);
        enrolmentForm.clearClientPageValidations(currentPosition);

        validationInfo = childModelViewSynchronizer.getValidationInfo();
        if (validationInfo.hasAnyValidationMessages()){
            app.getEnrolmentForm().addPageValidation(currentPosition, validationInfo);
        }

        validationInfo = childModelViewSynchronizer.getValidationInfo();
        if (validationInfo.hasAnyValidationMessages()){
            app.getEnrolmentForm().addPageValidation(currentPosition, validationInfo);
        }

        return false;
    }

    //--- ACTIVITY NAVIGATION ----------------------------------------------------------------------

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(getClass().getName(), "----------onActivityResult()");

        if(requestCode == SUPPORTING_DOC_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK && data != null) {
                SupportingDocumentsHelper docHelper = new SupportingDocumentsHelper(rootView,
                        context, inflater);
                docHelper.generateUploadFileList(data, true,
                        new MasterDataListener<ServerResponse>() {
                    @Override
                    public void onMasterData(ServerResponse serverResponse) {
                        if (serverResponse.getResponseType() == ServerResponseType.SUCCESS) {
                            SupportingFile supportingFile = new SupportingFile();
                            supportingFile.setCode(serverResponse.getCode());
                            supportingFile.setFileName(serverResponse.getFile().getName());

                            supportingFiles.add(supportingFile);
                        } else {
                            Toast.makeText(context, serverResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    public void setBrowseDocButtonClick() {
        Log.i(getClass().getName(), "----------setBrowseDocButtonClick()");

        LinearLayout llSupDocSection = (LinearLayout)rootView.findViewById(R.id.supporting_docs);

        Button browseButton = (Button)llSupDocSection.findViewById(R.id.btnFirstInOne);
        browseButton.setText(R.string.btn_browse);
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picture = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activity.startActivityForResult(picture, SUPPORTING_DOC_REQUEST_CODE);
            }
        });
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    public void displayData(FragmentContainerActivityHelper fragmentContainer) {

        childDeclarations = app.getEnrolmentForm().getChildDeclarations();
        ChildDeclaration currentDeclaration = childDeclarations.get(childDeclarations.size() - 1);

        childDeclarationType = currentDeclaration.getDeclarationType();

        childModelViewSynchronizer = new ModelViewSynchronizer<Child>(
                Child.class, getMetaDataChild(),
                rootView, SerializedNames.SEC_ENROLMENT_MOTHER_DECLARE_CHILD);
        childDeclarationModelViewSynchronizer = new ModelViewSynchronizer<ChildDeclaration>(
                ChildDeclaration.class, getMetaDataChildDeclaration(fragmentContainer),
                rootView, SerializedNames.SEC_ENROLMENT_MOTHER_DECLARE);

        childModelViewSynchronizer.setLabels();
        childDeclarationModelViewSynchronizer.setLabels();

        childModelViewSynchronizer.displayDataObject(new Child());
        childDeclarationModelViewSynchronizer.displayDataObject(currentDeclaration);

        //Screen - Header
        TextView tvHeader = (TextView) rootView.findViewById(R.id.section_header);
        tvHeader.setText(StringHelper.getStringByResourceId(context, R.string.label_child));

        hideUnwantedData();
    }

    public String displayValidationErrors(int currentPosition) {
        EnrolmentForm enrolmentForm = app.getEnrolmentForm();
        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;

        if (enrolmentForm.isDisplayValidationErrors()) {
            ValidationInfo validationInfo = enrolmentForm.getPageSectionValidations(
                    currentPosition, SerializedNames.SEC_ENROLMENT_MOTHER_DECLARE);

            if (validationInfo.hasAnyValidationMessages()) {
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

    //--- VIEW META --------------------------------------------------------------------------------

    public ModelPropertyViewMetaList getMetaDataChildDeclaration(
            FragmentContainerActivityHelper fragmentContainer) {
        ModelPropertyViewMetaList metaDataList = new ModelPropertyViewMetaList(context);
        ModelPropertyViewMeta viewMeta = null;

        ArrayAdapter<GenericDataItem> countryAdapter =
                new ArrayAdapter<GenericDataItem>(context, android.R.layout.simple_list_item_1,
                        new ArrayList<GenericDataItem>());

        fragmentContainer.displayGenericData(MasterDataType.COUNTRY, countryAdapter);

        try {
            //---Date of Adoption
            viewMeta = new ModelPropertyViewMeta(ChildDeclaration.FIELD_DEC_ADOPTION_DATE);
            viewMeta.setIncludeTagId(R.id.child_declaration_a);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DATE);
            // TODO viewMeta.setSerialName(SerializedNames.SEC_ADOPTION_GIVEN_DATE);

            metaDataList.add(CHILD_DECLARATION_CLASS, viewMeta);

            //---Date of Adoption Order
            viewMeta = new ModelPropertyViewMeta(ChildDeclaration.FIELD_DEC_ADOPTION_ORDER_DATE);
            viewMeta.setIncludeTagId(R.id.child_declaration_b);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DATE);
            //viewMeta.setSerialName(SerializedNames.SN_BANK_ID);

            metaDataList.add(CHILD_DECLARATION_CLASS, viewMeta);

            //---Country of Birth
            viewMeta = new ModelPropertyViewMeta(ChildDeclaration.FIELD_DEC_BIRTH_COUNTRY);
            viewMeta.setIncludeTagId(R.id.child_declaration_c);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DROP_DOWN);
            viewMeta.setDropDownAdapter(countryAdapter);
            //viewMeta.setSerialName(SerializedNames.SN_BANK_ID);

            metaDataList.add(CHILD_DECLARATION_CLASS, viewMeta);

            //---Citizenship No
            viewMeta = new ModelPropertyViewMeta(ChildDeclaration.FIELD_DEC_CITIZENSHIP_NO);
            viewMeta.setIncludeTagId(R.id.child_declaration_d);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            //viewMeta.setSerialName(SerializedNames.SN_BANK_ID);

            metaDataList.add(CHILD_DECLARATION_CLASS, viewMeta);

            //---Deceased Date
            viewMeta = new ModelPropertyViewMeta(ChildDeclaration.FIELD_DEC_DECEASED_DATE);
            viewMeta.setIncludeTagId(R.id.child_declaration_e);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DATE);
            //viewMeta.setSerialName(SerializedNames.SN_BANK_ID);

            metaDataList.add(CHILD_DECLARATION_CLASS, viewMeta);

            //---Remarks
            viewMeta = new ModelPropertyViewMeta(ChildDeclaration.FIELD_DEC_REMARKS);
            viewMeta.setIncludeTagId(R.id.remarks);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            //viewMeta.setSerialName(SerializedNames.SN_BANK_ID);

            metaDataList.add(CHILD_DECLARATION_CLASS, viewMeta);



        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

    public ModelPropertyViewMetaList getMetaDataChild() {
        ModelPropertyViewMetaList metaDataList = new ModelPropertyViewMetaList(context);
        ModelPropertyViewMeta viewMeta = null;

        try {
            //---Name
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_NAME);
            viewMeta.setIncludeTagId(R.id.edit_child_name);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_PERSON_NAME);

            metaDataList.add(CHILD_CLASS, viewMeta);

            //---Birth Day
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_BIRTHDAY);
            viewMeta.setIncludeTagId(R.id.date_of_birth);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DATE);
            viewMeta.setSerialName(SerializedNames.SN_PERSON_BIRTHDAY);

            metaDataList.add(CHILD_CLASS, viewMeta);

            //---Birth Certificate
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_BIRTH_CERT_NO);
            viewMeta.setIncludeTagId(R.id.birth_certificate_no);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_CHILD_BIRTH_CERT_NO);

            metaDataList.add(CHILD_CLASS, viewMeta);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

    //--- HELPERS ----------------------------------------------------------------------------------

    public void hideUnwantedData() {
        switch (childDeclarationType) {
            case HAS_GIVEN_UP_FOR_ADOPTION_CHILD:
                rootView.findViewById(R.id.child_declaration_a).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.child_declaration_b).setVisibility(View.GONE);
                rootView.findViewById(R.id.child_declaration_c).setVisibility(View.GONE);
                rootView.findViewById(R.id.child_declaration_d).setVisibility(View.GONE);
                rootView.findViewById(R.id.child_declaration_e).setVisibility(View.GONE);
                break;
            case HAS_ADOPTED_CHILD:
                rootView.findViewById(R.id.child_declaration_a).setVisibility(View.GONE);
                rootView.findViewById(R.id.child_declaration_b).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.child_declaration_c).setVisibility(View.GONE);
                rootView.findViewById(R.id.child_declaration_d).setVisibility(View.GONE);
                rootView.findViewById(R.id.child_declaration_e).setVisibility(View.GONE);
                break;
            case HAS_NON_SINGAPORE_CHILD:
                rootView.findViewById(R.id.child_declaration_a).setVisibility(View.GONE);
                rootView.findViewById(R.id.child_declaration_b).setVisibility(View.GONE);
                rootView.findViewById(R.id.child_declaration_c).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.child_declaration_d).setVisibility(View.GONE);
                rootView.findViewById(R.id.child_declaration_e).setVisibility(View.GONE);
                break;
            case HAS_SINGAPORE_BORN_CHILD:
                rootView.findViewById(R.id.child_declaration_a).setVisibility(View.GONE);
                rootView.findViewById(R.id.child_declaration_b).setVisibility(View.GONE);
                rootView.findViewById(R.id.child_declaration_c).setVisibility(View.GONE);
                rootView.findViewById(R.id.child_declaration_d).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.child_declaration_e).setVisibility(View.GONE);
                break;
            case HAS_DECEASED_CHILD:
                rootView.findViewById(R.id.child_declaration_a).setVisibility(View.GONE);
                rootView.findViewById(R.id.child_declaration_b).setVisibility(View.GONE);
                rootView.findViewById(R.id.child_declaration_c).setVisibility(View.GONE);
                rootView.findViewById(R.id.child_declaration_d).setVisibility(View.GONE);
                rootView.findViewById(R.id.child_declaration_e).setVisibility(View.VISIBLE);
                break;
        }
    }
}
