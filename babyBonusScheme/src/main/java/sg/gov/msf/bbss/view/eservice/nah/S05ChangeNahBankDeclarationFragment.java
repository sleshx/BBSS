package sg.gov.msf.bbss.view.eservice.nah;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
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
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.logic.server.response.ServerResponse;
import sg.gov.msf.bbss.logic.server.response.ServerResponseType;
import sg.gov.msf.bbss.logic.type.ChildListType;
import sg.gov.msf.bbss.model.entity.common.BankAccount;
import sg.gov.msf.bbss.model.entity.masterdata.Bank;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceChangeNah;
import sg.gov.msf.bbss.view.eservice.ServicesHelper;

/**
 * Created by bandaray
 */
public class S05ChangeNahBankDeclarationFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;
    private static Class BANK_ACCOUNT_CLASS = BankAccount.class;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private ChangeNahFragmentContainerActivity fragmentContainer;
    private ServicesHelper servicesHelper;

    private ModelViewSynchronizer<BankAccount> bankAccountModelViewSynchronizer;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static S05ChangeNahBankDeclarationFragment newInstance(int index) {
        S05ChangeNahBankDeclarationFragment fragment = new S05ChangeNahBankDeclarationFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_service_bank_and_declaration, null);

        context = getActivity();
        app = (BbssApplication) getActivity().getApplication();
        fragmentContainer = ((ChangeNahFragmentContainerActivity) getActivity());
        servicesHelper = new ServicesHelper(context);

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        ServiceChangeNah serviceChangeNah = app.getServiceChangeNah();
        BankAccount bankAccount = bankAccountModelViewSynchronizer.getDataObject();
        ValidationInfo validationInfo = bankAccountModelViewSynchronizer.getValidationInfo();

        serviceChangeNah.setNewBankAccount(bankAccount);
        serviceChangeNah.clearClientPageValidations(CURRENT_POSITION);
        serviceChangeNah.addSectionPage(SerializedNames.SEC_SERVICE_CHANGE_NAH_CG_AUTHORIZER, CURRENT_POSITION);

        if (validationInfo.hasAnyValidationMessages()){
            serviceChangeNah.addPageValidation(CURRENT_POSITION, validationInfo);
        }

        return false;
    }

    @Override
    public boolean onResumeFragment() {
        bankAccountModelViewSynchronizer = new ModelViewSynchronizer<BankAccount>(
                BankAccount.class, getMetaData(),
                rootView, SerializedNames.SEC_SERVICE_CHANGE_NAH_CG_AUTHORIZER);

        displayData(displayValidationErrors());
        setButtonClicks();

        return false;
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setSubmitButtonClick() {
        Button next = (Button) rootView.findViewById(R.id.btnFirstInTwo);
        next.setText(R.string.btn_submit);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!onPauseFragment(true)) {
                    if (app.getServiceChangeNah().hasClientValidations()) {
                        afterSubmit(true);
                    } else if(!app.getServiceChangeNah().isDeclared()){
                        servicesHelper.createDeclarationRequiredMessageBox();
                    } else {
                        new UpdateNominatedAccHolderTask(context).execute();
                    }
                }
            }
        });
    }

    private void setButtonClicks() {
        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, false);
        setSubmitButtonClick();
        fragmentContainer.setCancelButtonClick(rootView, R.id.btnSecondInTwo);
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    private void displayData(String errorMessage) {
        BankAccount account = app.getServiceChangeNah().getNewBankAccount();

        if(account == null){
            account = new BankAccount();
        }

        bankAccountModelViewSynchronizer.setLabels();
        bankAccountModelViewSynchronizer.setHeaderTitle(R.id.section_new_account_detail,
                R.string.label_services_new_bank_acc_details);
        bankAccountModelViewSynchronizer.displayDataObject(account);

        setBankDetailsSection();
        setDeclarationSection();

        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(rootView, CURRENT_POSITION, 0, true, errorMessage);

        hideUnwantedIncludeLayouts();
    }

    private String displayValidationErrors() {
        ServiceChangeNah serviceChangeNah = app.getServiceChangeNah();
        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;

        if(serviceChangeNah.isDisplayValidationErrors()){
            ValidationInfo validationInfo = serviceChangeNah.getPageSectionValidations(
                    CURRENT_POSITION, SerializedNames.SEC_SERVICE_CHANGE_NAH_CG_AUTHORIZER);

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
        ModelPropertyViewMeta viewMeta;

        ArrayAdapter<Bank> bankAdapter = new ArrayAdapter<Bank>(context,
                android.R.layout.simple_list_item_1, new ArrayList<Bank>());

        servicesHelper.displayBanks(bankAdapter);

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

    //--- HELPERS ----------------------------------------------------------------------------------

    private void setBankDetailsSection() {
        RelativeLayout rlBankDetailsDesc = (RelativeLayout) rootView.findViewById(
                R.id.bank_account_details_section);
        WebView wvBankDetailsDesc = (WebView) rlBankDetailsDesc.findViewById(
                R.id.wvScreenDescription);

        wvBankDetailsDesc.loadData(StringHelper.getJustifiedString(context,
                R.string.desc_services_common_declaration_desc_1,
                R.color.theme_gray_default_bg), "text/html", "utf-8");
    }

    private void setDeclarationSection() {
        LinearLayout rlDeclarationSection = (LinearLayout) rootView.findViewById(
                R.id.declaration_section);

        TextView tvDeclarationTitle = (TextView) rlDeclarationSection.findViewById(
                R.id.section_declaration);

        LinearLayout llDeclarationDesc = (LinearLayout) rlDeclarationSection.findViewById(
                R.id.declaration_descriptions);
        WebView wvDeclarationDesc = (WebView) llDeclarationDesc.findViewById(
                R.id.wvScreenDescription);

        LinearLayout llDeclaration = (LinearLayout) rlDeclarationSection.findViewById(
                R.id.declaration_1);

        WebView wvDeclaration = (WebView) llDeclaration.findViewById(R.id.wvLabel);
        final CheckBox cbDeclaration = (CheckBox) llDeclaration.findViewById(R.id.cbValue);

        tvDeclarationTitle.setText(R.string.label_common_declaration);
        wvDeclarationDesc.loadData(StringHelper.getJustifiedString(context,
                R.string.desc_services_change_nah_declaration_desc,
                R.color.theme_gray_default_bg), "text/html", "utf-8");
        wvDeclaration.loadData(StringHelper.getJustifiedString(context,
                R.string.desc_services_common_declaration_1, 0), "text/html", "utf-8");

        cbDeclaration.setChecked(app.getServiceChangeNah().isDeclared());
        cbDeclaration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.getServiceChangeNah().setDeclared(cbDeclaration.isChecked());
            }
        });
    }

    private void hideUnwantedIncludeLayouts() {
        LinearLayout declarations = (LinearLayout) rootView.findViewById(R.id.declaration_section);

        declarations.findViewById(R.id.declaration_2).setVisibility(View.GONE);
        declarations.findViewById(R.id.declaration_3).setVisibility(View.GONE);
        declarations.findViewById(R.id.declaration_4).setVisibility(View.GONE);
    }

    private void afterSubmit(final boolean isClientSubmit){
        servicesHelper.createClientValidationIssuesMessageBox(new MessageBoxButtonClickListener() {
            @Override
            public void onClickPositiveButton(DialogInterface dialog, int id) {
                ServiceChangeNah serviceChangeNah = app.getServiceChangeNah();
                int firstErrorPage = serviceChangeNah.getFirstErrorPage();

                if(isClientSubmit) {
                    serviceChangeNah.setDisplayValidationErrors(serviceChangeNah.hasClientValidations());
                } else {
                    serviceChangeNah.setDisplayValidationErrors(serviceChangeNah.hasAnyValidations());
                }

                if(firstErrorPage == CURRENT_POSITION){
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

    private class UpdateNominatedAccHolderTask extends AsyncTask<Void, Void, ServerResponse>{

        private Context context;
        private ProgressDialog dialog;

        public UpdateNominatedAccHolderTask(Context context) {
            this. context = context;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(StringHelper.getStringByResourceId(context,
                    R.string.progress_service_change_nah_updating));
            dialog.show();
        }

        @Override
        protected ServerResponse doInBackground(Void... params) {
            return ProxyFactory.getEServiceProxy()
                               .updateNominatedAccountHolder(app.getServiceChangeNah());
        }

        protected void onPostExecute(ServerResponse response) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if(response.getResponseType() == ServerResponseType.SUCCESS){
                servicesHelper.createServiceResponseMessageBox(app, response.getMessage(),
                        response.getAppId(), ChildListType.CHANGE_NAH);
            } else if(response.getResponseType() == ServerResponseType.APPLICATION_ERROR){
                String message = StringHelper.getStringByResourceId(context, R.string.error_common_application_error);
                MessageBox.show(context, message, false, true, R.string.btn_ok, false, 0, null);
            } else {
                afterSubmit(false);
            }
        }
    }
}
