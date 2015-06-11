package sg.gov.msf.bbss.view.enrolment.main;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.List;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyEditType;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMeta;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMetaList;
import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.apputils.ui.component.MessageBox;
import sg.gov.msf.bbss.apputils.validation.ValidationInfo;
import sg.gov.msf.bbss.apputils.validation.ValidationMessage;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.BabyBonusValidationHandler;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.type.ChildRegistrationType;
import sg.gov.msf.bbss.logic.type.EnrolmentAppType;
import sg.gov.msf.bbss.model.entity.childdata.ChildRegistration;
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentForm;

/**
 * Created by bandaray
 */
public class E03PreBirthRegistrationFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;

    private static Class CHILD_REG_CLASS = ChildRegistration.class;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private EnrolmentFragmentContainerActivity fragmentContainer;
    private ModelViewSynchronizer<ChildRegistration> childRegModelViewSynchronizer;
    private boolean isMessageDisplayed = false;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static E03PreBirthRegistrationFragment newInstance(int index) {
        E03PreBirthRegistrationFragment fragment = new E03PreBirthRegistrationFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_enrolment_pre_birth, null);

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
        ChildRegistration childRegistration = childRegModelViewSynchronizer.getDataObject();
        ValidationInfo validationInfo = childRegModelViewSynchronizer.getValidationInfo();

        enrolmentForm.setChildRegistration(childRegistration);
        enrolmentForm.clearClientPageValidations(CURRENT_POSITION);
        enrolmentForm.addSectionPage(SerializedNames.SEC_ENROLMENT_ENROL_CHILD_PRE_BIRTH, CURRENT_POSITION);

        if (childRegistration != null) {
            Date estimatedDelivery = childRegistration.getEstimatedDelivery();
            if (estimatedDelivery != null) {
                BabyBonusValidationHandler.validateEstimatedDeliveryDate(context,
                        estimatedDelivery, validationInfo);
            }
        }

        if (validationInfo.hasAnyValidationMessages()){
            enrolmentForm.addPageValidation(CURRENT_POSITION, validationInfo);
        }

        if(childRegistration.getEstimatedDelivery() == null &&
           childRegistration.getRegistrationType() == ChildRegistrationType.PRE_BIRTH){
            childRegistration.setRegistrationType(null);
        } else if(childRegistration.getEstimatedDelivery() != null &&
                  childRegistration.getRegistrationType() == null){
            childRegistration.setRegistrationType(ChildRegistrationType.PRE_BIRTH);
        } else if(childRegistration.getEstimatedDelivery() != null &&
                  childRegistration.getRegistrationType() != null &&
                  childRegistration.getRegistrationType() != ChildRegistrationType.PRE_BIRTH) {
            String message = getString(R.string.error_enrolment_allowed_only_one_registration,
                    childRegistration.getRegistrationType().getDisplayName(context));
            MessageBox.show(context, message, false, true, R.string.btn_ok, false, 0, null);

            return true;
        }

        return false;
    }

    @Override
    public boolean onResumeFragment() {
        childRegModelViewSynchronizer = new ModelViewSynchronizer<ChildRegistration>(
                ChildRegistration.class, getMetaData(), rootView,
                SerializedNames.SEC_ENROLMENT_ENROL_CHILD_PRE_BIRTH);

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
        ChildRegistration childRegistration = app.getEnrolmentForm().getChildRegistration();

        if(childRegistration == null){
            childRegistration = new ChildRegistration();
        }

        childRegModelViewSynchronizer.setLabels();
        childRegModelViewSynchronizer.setHeaderTitle(R.id.section_pre_birth_data,
                R.string.label_child_reg_type_pre_birth);
        childRegModelViewSynchronizer.displayDataObject(childRegistration);

        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(rootView, CURRENT_POSITION,
                R.string.label_enrolment_fill_section_below_child_pre_birth, true, errorMessage);
    }

    private String displayValidationErrors() {
        EnrolmentForm enrolmentForm = app.getEnrolmentForm();
        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;

        if(enrolmentForm.isDisplayValidationErrors()){
            ValidationInfo validationInfo = enrolmentForm.getPageSectionValidations(
                    CURRENT_POSITION, SerializedNames.SEC_ENROLMENT_ENROL_CHILD_PRE_BIRTH);

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

        boolean isFocusable = !app.getEnrolmentForm().getAppType().equals(EnrolmentAppType.VIEW);

        try {
            //---Estimated Date of Delivery
            viewMeta = new ModelPropertyViewMeta(ChildRegistration.FIELD_REG_ESTIMATED_DELIVERY);
            viewMeta.setIncludeTagId(R.id.edit_pre_birth_est_date);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(true);
            viewMeta.setFutureDateRequired(true);
            viewMeta.setEditType(ModelPropertyEditType.DATE);
            viewMeta.setSerialName(SerializedNames.SN_CHILD_REG_EST_DELIVERY_DATE);
            viewMeta.setFocusable(isFocusable);

            metaDataList.add(CHILD_REG_CLASS, viewMeta);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

}

