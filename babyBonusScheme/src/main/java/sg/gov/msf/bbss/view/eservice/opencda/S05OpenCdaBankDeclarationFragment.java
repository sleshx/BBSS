package sg.gov.msf.bbss.view.eservice.opencda;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import sg.gov.msf.bbss.model.entity.common.CdaBankAccount;
import sg.gov.msf.bbss.model.entity.masterdata.Bank;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceChangeCdat;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceChangeNah;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceOpenCda;
import sg.gov.msf.bbss.view.eservice.ServicesHelper;
import sg.gov.msf.bbss.view.eservice.ServicesHomeActivity;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class S05OpenCdaBankDeclarationFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION = 4;

    private static Class BANK_ACCOUNT_CLASS = BankAccount.class;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private ModelViewSynchronizer<CdaBankAccount> bankAccountModelViewSynchronizer;
    private OpenCdaFragmentContainerActivity fragmentContainer;
    private ServicesHelper servicesHelper;

    public static S05OpenCdaBankDeclarationFragment newInstance(int index) {
        S05OpenCdaBankDeclarationFragment fragment = new S05OpenCdaBankDeclarationFragment();
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
        fragmentContainer = ((OpenCdaFragmentContainerActivity) getActivity());
        servicesHelper = new ServicesHelper(context);

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        CdaBankAccount bankAccount = bankAccountModelViewSynchronizer.getDataObject();
        ValidationInfo validationInfo = bankAccountModelViewSynchronizer.getValidationInfo();

        app.getServiceOpenCda().setCdaBankAccount(bankAccount);
        app.getServiceOpenCda().clearClientPageValidations(CURRENT_POSITION);

        if (validationInfo.hasAnyValidationMessages()){
            app.getServiceOpenCda().addPageValidation(CURRENT_POSITION, validationInfo);
        }

        return false;
    }

    @Override
    public boolean onResumeFragment() {
        bankAccountModelViewSynchronizer = new ModelViewSynchronizer<CdaBankAccount>(
                CdaBankAccount.class, getMetaData(),
                rootView, SerializedNames.SEC_SERVICE_CHANGE_NAH_CG_AUTHORIZER);

        displayData(displayValidationErrors());
        setButtonClicks();

        return false;
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {
        Log.i(getClass().getName(), "----------setButtonClicks()");

        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, false);
        setSubmitButtonClick();
        fragmentContainer.setCancelButtonClick(rootView, R.id.btnSecondInTwo);
    }

    private void setSubmitButtonClick() {
        Button next = (Button) rootView.findViewById(R.id.btnFirstInTwo);
        next.setText(R.string.btn_submit);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!onPauseFragment(true)) {
                    ServiceOpenCda serviceOpenCda = app.getServiceOpenCda();
                    if (serviceOpenCda.hasClientValidations()) {
                        afterSubmit(true);
                    } else if(!serviceOpenCda.isDeclared1() ||
                            !serviceOpenCda.isDeclared2() || !serviceOpenCda.isDeclared3()) {
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
        CdaBankAccount account = app.getServiceOpenCda().getCdaBankAccount();

        if(account == null){
            account = new CdaBankAccount();
        }

        bankAccountModelViewSynchronizer.setLabels();
        bankAccountModelViewSynchronizer.setHeaderTitle(R.id.section_new_account_detail,
                R.string.label_services_new_cdab);
        bankAccountModelViewSynchronizer.displayDataObject(account);

        setDeclarationSection();
        hideUnwantedIncludeLayouts();

        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(rootView, CURRENT_POSITION, 0, true, errorMessage);
    }

    private String displayValidationErrors() {
        ServiceOpenCda serviceOpenCda = app.getServiceOpenCda();
        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;

        if(serviceOpenCda.isDisplayValidationErrors()){
            ValidationInfo validationInfo = serviceOpenCda.getPageSectionValidations(
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
            viewMeta.setLabelResourceId(R.string.label_services_new_cdab);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DROP_DOWN);
            viewMeta.setSerialName(SerializedNames.SN_BANK_ID);
            viewMeta.setDropDownAdapter(bankAdapter);

            metaDataList.add(BANK_ACCOUNT_CLASS, viewMeta);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

    //--- HELPERS ----------------------------------------------------------------------------------

    private void setDeclarationSection() {
        TextView tvDeclarationTitle = (TextView) rootView.findViewById( R.id.section_declaration);
        LinearLayout llDeclaration1 = (LinearLayout) rootView.findViewById(R.id.declaration_1);
        LinearLayout llDeclaration2 = (LinearLayout) rootView.findViewById(R.id.declaration_2);
        LinearLayout llDeclaration3 = (LinearLayout) rootView.findViewById(R.id.declaration_3);

        WebView wvDeclaration1 = (WebView) llDeclaration1.findViewById(R.id.wvLabel);
        WebView wvDeclaration2 = (WebView) llDeclaration2.findViewById(R.id.wvLabel);
        WebView wvDeclaration3 = (WebView) llDeclaration3.findViewById(R.id.wvLabel);

        final CheckBox cbDeclaration1 = (CheckBox) llDeclaration1.findViewById(R.id.cbValue);
        final CheckBox cbDeclaration2 = (CheckBox) llDeclaration2.findViewById(R.id.cbValue);
        final CheckBox cbDeclaration3 = (CheckBox) llDeclaration3.findViewById(R.id.cbValue);

        tvDeclarationTitle.setText(R.string.label_common_declaration);

        wvDeclaration1.loadData(StringHelper.getJustifiedString(context,
                R.string.desc_services_common_declaration_2, 0), "text/html", "utf-8");
        wvDeclaration2.loadData(StringHelper.getJustifiedString(context,
                R.string.label_services_open_cda_declaration, 0), "text/html", "utf-8");
        wvDeclaration3.loadData(StringHelper.getJustifiedString(context,
                R.string.desc_services_common_declaration_3, 0), "text/html", "utf-8");

        cbDeclaration1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.getServiceOpenCda().setDeclared1(cbDeclaration1.isChecked());
            }
        });
        cbDeclaration2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.getServiceOpenCda().setDeclared2(cbDeclaration2.isChecked());
            }
        });
        cbDeclaration3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.getServiceOpenCda().setDeclared3(cbDeclaration3.isChecked());
            }
        });
    }

    private void hideUnwantedIncludeLayouts() {
        RelativeLayout rlBankAccount = (RelativeLayout) rootView.findViewById(R.id.bank_account_details_section);
        ((WebView) rlBankAccount.findViewById(R.id.wvScreenDescription)).setVisibility(View.GONE);
        ((LinearLayout) rlBankAccount.findViewById(R.id.edit_account_bank_branch)).setVisibility(View.GONE);
        ((LinearLayout) rlBankAccount.findViewById(R.id.edit_account_no)).setVisibility(View.GONE);

        rootView.findViewById(R.id.declaration_4).setVisibility(View.GONE);
    }

    private void afterSubmit(final boolean isClientSubmit){
        servicesHelper.createClientValidationIssuesMessageBox(new MessageBoxButtonClickListener() {
            @Override
            public void onClickPositiveButton(DialogInterface dialog, int id) {
                ServiceOpenCda serviceOpenCda = app.getServiceOpenCda();

                if(isClientSubmit) {
                    serviceOpenCda.setDisplayValidationErrors(serviceOpenCda.hasClientValidations());
                } else {
                    serviceOpenCda.setDisplayValidationErrors(serviceOpenCda.hasAnyValidations());
                }

                fragmentContainer.jumpToPageWithIndex(serviceOpenCda.getFirstErrorPage());
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
                    R.string.progress_service_change_nah_updating));
            dialog.show();
        }

        @Override
        protected ServerResponse doInBackground(Void... params) {
            return ProxyFactory.getEServiceProxy().updateOpenCDA(app.getServiceOpenCda());
        }

        protected void onPostExecute(ServerResponse response) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if(response.getResponseType() == ServerResponseType.SUCCESS){
                servicesHelper.createServiceResponseMessageBox(app, response.getMessage(),
                        response.getAppId(), ChildListType.OPEN_CDA);
            } else if(response.getResponseType() == ServerResponseType.APPLICATION_ERROR){
                String message = StringHelper.getStringByResourceId(context, R.string.error_common_application_error);
                MessageBox.show(context, message, false, true, R.string.btn_ok, false, 0, null);
            } else {
                afterSubmit(false);
            }
        }
    }
}
