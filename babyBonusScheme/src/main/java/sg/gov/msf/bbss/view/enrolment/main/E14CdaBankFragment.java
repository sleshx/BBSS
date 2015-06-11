package sg.gov.msf.bbss.view.enrolment.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.model.entity.common.CdaBankAccount;
import sg.gov.msf.bbss.model.entity.masterdata.Bank;
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentForm;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class E14CdaBankFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;

    private static Class CDA_BANK_ACCOUNT_CLASS = CdaBankAccount.class;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private EnrolmentFragmentContainerActivity fragmentContainer;
    private ModelViewSynchronizer<CdaBankAccount> cdaBankAccountModelViewSynchronizer;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static E14CdaBankFragment newInstance(int index) {
        E14CdaBankFragment fragment = new E14CdaBankFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_enrolment_cda_bank, container, false);

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
        CdaBankAccount cdaBankAccount = cdaBankAccountModelViewSynchronizer.getDataObject();
        ValidationInfo validationInfo = cdaBankAccountModelViewSynchronizer.getValidationInfo();

        enrolmentForm.setCdaBankAccount(cdaBankAccount);
        enrolmentForm.clearClientPageValidations(CURRENT_POSITION);
        enrolmentForm.addSectionPage(SerializedNames.SEC_ENROLMENT_CHILD_DEV_ACCOUNT, CURRENT_POSITION);

        if (validationInfo.hasAnyValidationMessages()){
            app.getEnrolmentForm().addPageValidation(CURRENT_POSITION, validationInfo);
        }
        return false;
    }

    @Override
    public boolean onResumeFragment() {
        cdaBankAccountModelViewSynchronizer = new ModelViewSynchronizer<CdaBankAccount>(
                CdaBankAccount.class, getMetaData(), rootView, SerializedNames.SEC_ENROLMENT_CHILD_DEV_ACCOUNT);

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
        CdaBankAccount cdaBankAccount = app.getEnrolmentForm().getCdaBankAccount();

        if(cdaBankAccount == null){
            cdaBankAccount = new CdaBankAccount();
        }

        //Bank Account Details
        cdaBankAccountModelViewSynchronizer.setLabels();
        cdaBankAccountModelViewSynchronizer.setHeaderTitle(R.id.section_header,
                R.string.label_child_dev_acc);
        cdaBankAccountModelViewSynchronizer.displayDataObject(cdaBankAccount);

        //Screen - Title and Instructions
        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(rootView, CURRENT_POSITION,
                R.string.label_enrolment_fill_section_below_cda_bank, true, errorMessage);
    }

    private String displayValidationErrors() {
        EnrolmentForm enrolmentForm = app.getEnrolmentForm();
        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;

        if (enrolmentForm.isDisplayValidationErrors()) {
            ValidationInfo validationInfo = enrolmentForm.getPageSectionValidations(
                    CURRENT_POSITION, SerializedNames.SEC_ENROLMENT_CHILD_DEV_ACCOUNT);

            if (validationInfo.hasAnyValidationMessages()) {
                errorMessageList = validationInfo.getValidationMessages();
                cdaBankAccountModelViewSynchronizer.displayValidationErrors(errorMessageList);

                for(ValidationMessage messageList : errorMessageList) {
                    errorMessage = errorMessage + messageList.getMessage() +
                            AppConstants.SYMBOL_BREAK_LINE;
                }
            }
        }
        return errorMessage;
    }

    //--- VIEW META --------------------------------------------------------------------------------

    private ModelPropertyViewMetaList getMetaData() {
        ModelPropertyViewMetaList metaDataList = new ModelPropertyViewMetaList(context);
        ModelPropertyViewMeta viewMeta = null;

        ArrayAdapter<Bank> bankAdapter = new ArrayAdapter<Bank>(context,
                android.R.layout.simple_list_item_1, new ArrayList<Bank>());

        fragmentContainer.displayBanks(bankAdapter);

        try {
            viewMeta = new ModelPropertyViewMeta(CdaBankAccount.FIELD_BANK);
            viewMeta.setIncludeTagId(R.id.edit_cda_bank);
            viewMeta.setLabelResourceId(R.string.label_child_dev_acc_bank);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DROP_DOWN);
            viewMeta.setSerialName(SerializedNames.SN_BANK_ID);
            viewMeta.setDropDownAdapter(bankAdapter);

            metaDataList.add(CDA_BANK_ACCOUNT_CLASS, viewMeta);

            viewMeta = new ModelPropertyViewMeta(CdaBankAccount.FIELD_CDA_NETS_CARD_NAME);
            viewMeta.setIncludeTagId(R.id.edit_nets_card_child_name);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_CDAB_NETS_CARD_NAME);

            metaDataList.add(CDA_BANK_ACCOUNT_CLASS, viewMeta);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }
}
