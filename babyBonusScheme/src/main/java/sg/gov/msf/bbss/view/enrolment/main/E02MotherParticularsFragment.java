package sg.gov.msf.bbss.view.enrolment.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyEditType;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMeta;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMetaList;
import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.apputils.validation.ValidationInfo;
import sg.gov.msf.bbss.apputils.validation.ValidationMessage;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.BabyBonusValidationHandler;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.type.CommunicationType;
import sg.gov.msf.bbss.logic.type.EnrolmentAppType;
import sg.gov.msf.bbss.logic.type.IdentificationType;
import sg.gov.msf.bbss.logic.type.MasterDataType;
import sg.gov.msf.bbss.model.entity.masterdata.GenericDataItem;
import sg.gov.msf.bbss.model.entity.people.Adult;
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentForm;

/**
 * Created by bandaray
 */
public class E02MotherParticularsFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;

    private static Class ADULT_CLASS = Adult.class;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private EnrolmentFragmentContainerActivity fragmentContainer;
    private ModelViewSynchronizer<Adult> adultModelViewSynchronizer;

    private boolean isLoaded = false;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static E02MotherParticularsFragment newInstance(int index) {
        E02MotherParticularsFragment fragment = new E02MotherParticularsFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_person_particulars, null);

        context = getActivity();
        app = (BbssApplication) getActivity().getApplication();
        fragmentContainer = ((EnrolmentFragmentContainerActivity) getActivity());

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        EnrolmentForm enrolmentForm = app.getEnrolmentForm();
        Adult adult = adultModelViewSynchronizer.getDataObject();
        ValidationInfo validationInfo = adultModelViewSynchronizer.getValidationInfo();

        enrolmentForm.setMother(adult);
        enrolmentForm.clearClientPageValidations(CURRENT_POSITION);
        enrolmentForm.addSectionPage(SerializedNames.SEC_ENROLMENT_MOTHER_PARTICULARS, CURRENT_POSITION);

        BabyBonusValidationHandler.validateCitizenNricPrefix(context, adult, validationInfo);
        BabyBonusValidationHandler.validateNricFormat(context, adult, validationInfo);
        BabyBonusValidationHandler.validateMobileNumberPrefix(context, adult, validationInfo);

        if (validationInfo.hasAnyValidationMessages()) {
            enrolmentForm.addPageValidation(CURRENT_POSITION, validationInfo);
        }

        return false;
    }

    @Override
    public boolean onResumeFragment() {
        adultModelViewSynchronizer = new ModelViewSynchronizer<Adult>(
                ADULT_CLASS, getMetaData(), rootView,
                SerializedNames.SEC_ENROLMENT_MOTHER_PARTICULARS);

        displayData(displayValidationErrors());
        setButtonClicks();

        return false;
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {
        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, false);
        fragmentContainer.setNextButtonClick(rootView, R.id.btnFirstInThree, CURRENT_POSITION, true);
        fragmentContainer.setSaveAsDraftButtonClick(rootView, R.id.btnSecondInThree);
        fragmentContainer.setCancelButtonClick(rootView, R.id.btnThirdInThree);
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    private void displayData(String errorMessage) {
        Adult adult = app.getEnrolmentForm().getMother();

        if(adult == null) {
            adult = new Adult();
        }

        adultModelViewSynchronizer.setLabels();
        adultModelViewSynchronizer.setHeaderTitle(R.id.section_person_particulars,
                R.string.label_adult_type_mother);
        adultModelViewSynchronizer.displayDataObject(adult);

        hideUnwantedIncludeLayouts();

        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(rootView, CURRENT_POSITION,
                R.string.label_enrolment_fill_section_below_parent, true, errorMessage);
    }

    private String displayValidationErrors() {
        EnrolmentForm enrolmentForm = app.getEnrolmentForm();
        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;

        if(enrolmentForm.isDisplayValidationErrors()) {
            ValidationInfo validationInfo = enrolmentForm.getPageSectionValidations(
                    CURRENT_POSITION, SerializedNames.SEC_ENROLMENT_MOTHER_PARTICULARS);

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

        ArrayAdapter<IdentificationType> identificationTypeAdapter =
                new ArrayAdapter<IdentificationType>(context, android.R.layout.simple_list_item_1,
                        IdentificationType.values());

        ArrayAdapter<CommunicationType> communicationTypeAdapter =
                new ArrayAdapter<CommunicationType>(context, android.R.layout.simple_list_item_1,
                        CommunicationType.values());

        ArrayAdapter<GenericDataItem> nationalityAdapter =
                new ArrayAdapter<GenericDataItem>(context, android.R.layout.simple_list_item_1,
                        new ArrayList<GenericDataItem>());

        ArrayAdapter<GenericDataItem> occupationAdapter =
                new ArrayAdapter<GenericDataItem>(context, android.R.layout.simple_list_item_1,
                        new ArrayList<GenericDataItem>());

        fragmentContainer.displayGenericData(MasterDataType.NATIONALITY, nationalityAdapter);
        fragmentContainer.displayGenericData(MasterDataType.OCCUPATION, occupationAdapter);

            try {
            //---NRIC/FIN
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_NRIC);
            viewMeta.setIncludeTagId(R.id.edit_person_nric);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_PERSON_NRIC);
            viewMeta.setMaxLength(BabyBonusConstants.LENGTH_ADULT_NRIC_FIN);
            viewMeta.setFocusable(isFocusable);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---Identification Type
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_IDENTIFICATION_TYPE);
            viewMeta.setIncludeTagId(R.id.edit_person_id_type);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DROP_DOWN);
            viewMeta.setSerialName(SerializedNames.SN_ADULT_ID_TYPE);
            viewMeta.setDropDownAdapter(identificationTypeAdapter);
            viewMeta.setDropDownItemSelectedListener(
                    new IdentificationTypeSelectionChangeListener(identificationTypeAdapter));
            viewMeta.setFocusable(isFocusable);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---Passport No / Foreign ID
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_IDENTIFICATION_NO);
            viewMeta.setIncludeTagId(R.id.edit_person_id_no);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_ADULT_ID_NO);
            viewMeta.setMaxLength(BabyBonusConstants.LENGTH_ADULT_ID_NO);
            viewMeta.setFocusable(isFocusable);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---Name
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_NAME);
            viewMeta.setIncludeTagId(R.id.edit_person_name_as_in);
            viewMeta.setLabelResourceId(R.string.label_adult_name_as_in);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_PERSON_NAME);
            viewMeta.setMaxLength(BabyBonusConstants.LENGTH_ADULT_NAME);
            viewMeta.setFocusable(isFocusable);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---Nationality
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_NATIONALITY);
            viewMeta.setIncludeTagId(R.id.edit_person_nationality);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DROP_DOWN);
            viewMeta.setSerialName(SerializedNames.SN_ADULT_NATIONALITY);
            viewMeta.setDropDownAdapter(nationalityAdapter);
            viewMeta.setFocusable(isFocusable);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---Date of Birth
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_BIRTHDAY);
            viewMeta.setIncludeTagId(R.id.edit_person_birthday);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DATE);
            viewMeta.setSerialName(SerializedNames.SN_PERSON_BIRTHDAY);
            viewMeta.setFocusable(isFocusable);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---Mode of Communication
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_MODE_OF_COM);
            viewMeta.setIncludeTagId(R.id.edit_person_communication_mode);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DROP_DOWN);
            viewMeta.setSerialName(SerializedNames.SN_ADULT_MODE_OF_COMM);
            viewMeta.setDropDownAdapter(communicationTypeAdapter);
            viewMeta.setDropDownItemSelectedListener(
                    new ModeOfCommunicationSelectionChangeListener(communicationTypeAdapter));
            viewMeta.setFocusable(!isViewMode);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---Mobile Number
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_MOBILE_NO);
            viewMeta.setIncludeTagId(R.id.edit_person_mobile);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.PHONE);
            viewMeta.setSerialName(SerializedNames.SN_ADULT_MOBILE);
            viewMeta.setMaxLength(BabyBonusConstants.LENGTH_ADULT_MOBILE);
            viewMeta.setFocusable(!isViewMode);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---Email Address
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_EMAIL_ADDR);
            viewMeta.setIncludeTagId(R.id.edit_person_email);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.EMAIL);
            viewMeta.setSerialName(SerializedNames.SN_ADULT_EMAIL);
            viewMeta.setMaxLength(BabyBonusConstants.LENGTH_ADULT_EMAIL);
            viewMeta.setFocusable(!isViewMode);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---Occupation
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_OCCUPATION);
            viewMeta.setIncludeTagId(R.id.edit_person_occupation);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DROP_DOWN);
            viewMeta.setSerialName(SerializedNames.SN_ADULT_OCCUPATION);
            viewMeta.setDropDownAdapter(occupationAdapter);
            viewMeta.setFocusable(isFocusable);

            metaDataList.add(ADULT_CLASS, viewMeta);

            //---Monthly Income
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_MONTHLY_INCOME);
            viewMeta.setIncludeTagId(R.id.edit_person_monthly_income);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.CURRENCY);
            viewMeta.setSerialName(SerializedNames.SN_ADULT_INCOME);
            viewMeta.setMaxLength(BabyBonusConstants.LENGTH_ADULT_INCOME);
            viewMeta.setFocusable(isFocusable);

            metaDataList.add(ADULT_CLASS, viewMeta);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

    //--- HELPERS ----------------------------------------------------------------------------------

    private void hideUnwantedIncludeLayouts() {
        rootView.findViewById(R.id.screen_1buttons).setVisibility(View.GONE);
        rootView.findViewById(R.id.screen_2buttons).setVisibility(View.GONE);
        rootView.findViewById(R.id.edit_person_address_type).setVisibility(View.GONE);
        if (app.getEnrolmentForm().isPrePopulated()) {
            rootView.findViewById(R.id.edit_person_nationality).setVisibility(View.GONE);
            rootView.findViewById(R.id.edit_person_monthly_income).setVisibility(View.GONE);
            rootView.findViewById(R.id.edit_person_occupation).setVisibility(View.GONE);
        }
    }

    //--- LISTENERS  -------------------------------------------------------------------------------

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

    private class IdentificationTypeSelectionChangeListener
            implements AdapterView.OnItemSelectedListener {
        private ArrayAdapter<IdentificationType> adapter;

        public IdentificationTypeSelectionChangeListener(ArrayAdapter<IdentificationType> adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            IdentificationType identificationType = adapter.getItem(position);
            boolean isMandatory = (identificationType == IdentificationType.FOREIGN_PASSPORT ||
                    identificationType == IdentificationType.FOREIGN_ID);
            adultModelViewSynchronizer.setFieldMandatory(Adult.FIELD_IDENTIFICATION_NO, isMandatory);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}

