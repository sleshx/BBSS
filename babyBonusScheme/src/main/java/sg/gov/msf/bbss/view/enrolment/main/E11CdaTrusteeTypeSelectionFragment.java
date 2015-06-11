package sg.gov.msf.bbss.view.enrolment.main;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

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
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.type.EnrolmentAppType;
import sg.gov.msf.bbss.logic.type.RelationshipType;
import sg.gov.msf.bbss.model.entity.people.Adult;
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentForm;

/**
 * Created by bandaray
 */
public class E11CdaTrusteeTypeSelectionFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;
    private static int PERSON_PARTICULARS_PAGE_INDEX = CURRENT_POSITION + 1;
    private static int PERSON_ADDRESS_PAGE_INDEX = CURRENT_POSITION + 2;

    private static Class ADULT_CLASS = Adult.class;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private EnrolmentFragmentContainerActivity fragmentContainer;
    private ModelViewSynchronizer<Adult> adultRegModelViewSynchronizer;


    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static E11CdaTrusteeTypeSelectionFragment newInstance(int index) {
        E11CdaTrusteeTypeSelectionFragment fragment = new E11CdaTrusteeTypeSelectionFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_enrolment_person_type_selection, null);

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
        Adult adult = adultRegModelViewSynchronizer.getDataObject();
        ValidationInfo validationInfo = adultRegModelViewSynchronizer.getValidationInfo();

        enrolmentForm.setCdaTrustee(adult);
        enrolmentForm.clearClientPageValidations(CURRENT_POSITION);
        enrolmentForm.addSectionPage(SerializedNames.SEC_ENROLMENT_CDAT_PARTICULARS, CURRENT_POSITION);

        if (validationInfo.hasAnyValidationMessages()){
            enrolmentForm.addPageValidation(CURRENT_POSITION, validationInfo);
        }

        return false;
    }

    @Override
    public boolean onResumeFragment() {
        adultRegModelViewSynchronizer = new ModelViewSynchronizer<Adult>(
                ADULT_CLASS, getMetaData(), rootView,
                SerializedNames.SEC_ENROLMENT_CDAT_PARTICULARS);

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
        Adult adult = app.getEnrolmentForm().getCdaTrustee();

        if(adult == null){
            adult = new Adult();
        }

        adultRegModelViewSynchronizer.setLabels();
        adultRegModelViewSynchronizer.setHeaderTitle(R.id.enrolment_type_selection,
                R.string.label_cda_trustee_long);
        adultRegModelViewSynchronizer.displayDataObject(adult);

        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(rootView, CURRENT_POSITION,
                R.string.label_enrolment_cdat_type_selection, true, errorMessage);
    }

    private String displayValidationErrors() {
        EnrolmentForm enrolmentForm = app.getEnrolmentForm();
        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;

        if(enrolmentForm.isDisplayValidationErrors()){
            ValidationInfo validationInfo = enrolmentForm.getPageSectionValidations(
                    CURRENT_POSITION, SerializedNames.SEC_ENROLMENT_CDAT_PARTICULARS);

            if(validationInfo.hasAnyValidationMessages()) {
                errorMessageList = validationInfo.getValidationMessages();
                adultRegModelViewSynchronizer.displayValidationErrors(errorMessageList);

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

        boolean isFocusable = !app.getEnrolmentForm().getAppType().equals(EnrolmentAppType.VIEW);

        ArrayAdapter<RelationshipType> relationshipTypeAdapter =
                new ArrayAdapter<RelationshipType>(context, android.R.layout.simple_list_item_1,
                        RelationshipType.values());

        try {
            //New CDA Trustee Type
            viewMeta = new ModelPropertyViewMeta(Adult.FIELD_RELATIONSHIP);
            viewMeta.setIncludeTagId(R.id.enrolment_person_type_selection);
            viewMeta.setLabelResourceId(R.string.label_adult_type_cdat);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DROP_DOWN);
            viewMeta.setSerialName(SerializedNames.SN_ADULT_RELATIONSHIP);
            viewMeta.setDropDownAdapter(relationshipTypeAdapter);
            viewMeta.setDropDownItemSelectedListener(
                    new TypeSelectionChangeListener(relationshipTypeAdapter));
            viewMeta.setFocusable(isFocusable);

            metaDataList.add(ADULT_CLASS, viewMeta);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

    //--- LISTENERS  -------------------------------------------------------------------------------

    private class TypeSelectionChangeListener
            implements AdapterView.OnItemSelectedListener {
        private ArrayAdapter<RelationshipType> adapter;

        public TypeSelectionChangeListener(ArrayAdapter<RelationshipType> adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            EnrolmentForm enrolmentForm = app.getEnrolmentForm();
            enrolmentForm.setCdaTrusteeType(adapter.getItem(position));

            if (!adapter.getItem(position).equals(RelationshipType.TRUSTEE)) {
                if (enrolmentForm.getCdaTrustee() != null) {

                    enrolmentForm.setCdaTrustee(null);
                    enrolmentForm.clearClientPageValidations(PERSON_PARTICULARS_PAGE_INDEX);
                    enrolmentForm.clearClientPageValidations(PERSON_ADDRESS_PAGE_INDEX);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}

