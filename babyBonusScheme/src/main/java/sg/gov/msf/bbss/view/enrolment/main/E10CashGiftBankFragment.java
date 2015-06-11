package sg.gov.msf.bbss.view.enrolment.main;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

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
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.model.entity.common.BankAccount;
import sg.gov.msf.bbss.model.entity.masterdata.Bank;
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentForm;

/**
 * Created by bandaray
 */
public class E10CashGiftBankFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;
    private static Class BANK_ACCOUNT_CLASS = BankAccount.class;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private EnrolmentFragmentContainerActivity fragmentContainer;
    private ModelViewSynchronizer<BankAccount> bankAccountModelViewSynchronizer;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static E10CashGiftBankFragment newInstance(int index) {
        E10CashGiftBankFragment fragment = new E10CashGiftBankFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_service_cash_gift_bank, null);
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
        BankAccount bankAccount = bankAccountModelViewSynchronizer.getDataObject();
        ValidationInfo validationInfo = bankAccountModelViewSynchronizer.getValidationInfo();

        enrolmentForm.setCashGiftBankAccount(bankAccount);
        enrolmentForm.clearClientPageValidations(CURRENT_POSITION);
        enrolmentForm.addSectionPage(SerializedNames.SEC_ENROLMENT_CASH_GIFT_ACCOUNT, CURRENT_POSITION);

        if (validationInfo.hasAnyValidationMessages()){
            app.getEnrolmentForm().addPageValidation(CURRENT_POSITION, validationInfo);
        }

        return false;
    }

    @Override
    public boolean onResumeFragment() {
        bankAccountModelViewSynchronizer = new ModelViewSynchronizer<BankAccount>(
                BankAccount.class, getMetaData(), rootView, SerializedNames.SEC_ENROLMENT_CASH_GIFT_ACCOUNT);

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
        BankAccount account = app.getEnrolmentForm().getCashGiftBankAccount();

        if(account == null){
            account = new BankAccount();
        }

        //Bank Account Details
        bankAccountModelViewSynchronizer.setLabels();
        bankAccountModelViewSynchronizer.setHeaderTitle(R.id.section_new_account_detail,
                R.string.label_services_new_bank_acc_details);
        bankAccountModelViewSynchronizer.displayDataObject(account);

        //Bank Account Details - Description
        RelativeLayout rlBankDetailsDesc = (RelativeLayout) rootView.findViewById(
                R.id.bank_account_details_section);
        WebView wvBankDetailsDesc = (WebView) rlBankDetailsDesc.findViewById(
                R.id.wvScreenDescription);

        wvBankDetailsDesc.loadData(StringHelper.getJustifiedString(context,
                R.string.desc_services_common_declaration_desc_1,
                R.color.theme_gray_default_bg), "text/html", "utf-8");

        //Screen - Title and Instructions
        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(rootView, CURRENT_POSITION,
                R.string.label_enrolment_fill_section_below_nah_cg, true, errorMessage);
    }

    private String displayValidationErrors() {
        EnrolmentForm enrolmentForm = app.getEnrolmentForm();
        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;

        if(enrolmentForm.isDisplayValidationErrors()){
            ValidationInfo validationInfo = enrolmentForm.getPageSectionValidations(
                    CURRENT_POSITION, SerializedNames.SEC_ENROLMENT_CASH_GIFT_ACCOUNT);

            if(validationInfo.hasAnyValidationMessages()) {
                errorMessageList = validationInfo.getValidationMessages();
                bankAccountModelViewSynchronizer.displayValidationErrors(errorMessageList);

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
        ModelPropertyViewMeta viewMeta = null;

        ArrayAdapter<Bank> bankAdapter = new ArrayAdapter<Bank>(context,
                android.R.layout.simple_list_item_1, new ArrayList<Bank>());

        fragmentContainer.displayBanks(bankAdapter);

        try {
            viewMeta = new ModelPropertyViewMeta(BankAccount.FIELD_BANK);
            viewMeta.setIncludeTagId(R.id.edit_account_bank);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DROP_DOWN);
            viewMeta.setSerialName(SerializedNames.SN_BANK_ID);
            viewMeta.setDropDownAdapter(bankAdapter);

            metaDataList.add(BANK_ACCOUNT_CLASS, viewMeta);

            viewMeta = new ModelPropertyViewMeta(BankAccount.FIELD_BANK_BRANCH);
            viewMeta.setIncludeTagId(R.id.edit_account_bank_branch);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_BRANCH_ID);

            metaDataList.add(BANK_ACCOUNT_CLASS, viewMeta);

            viewMeta = new ModelPropertyViewMeta(BankAccount.FIELD_BANK_ACCOUNT);
            viewMeta.setIncludeTagId(R.id.edit_account_no);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_BANK_ACC_NO);

            metaDataList.add(BANK_ACCOUNT_CLASS, viewMeta);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }
}
