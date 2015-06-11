package sg.gov.msf.bbss.view.eservice.psea;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ListView;
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
import sg.gov.msf.bbss.logic.adapter.DisplayChildArrayAdapter;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.logic.server.response.ServerResponse;
import sg.gov.msf.bbss.logic.server.response.ServerResponseType;
import sg.gov.msf.bbss.logic.type.ChildListType;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.entity.common.BankAccount;
import sg.gov.msf.bbss.model.entity.masterdata.Bank;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceTransferToPsea;
import sg.gov.msf.bbss.view.eservice.ServicesHelper;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class S02TransferPseaBankDeclarationFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;

    private static Class BANK_ACCOUNT_CLASS = BankAccount.class;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private TransferPseaFragmentContainerActivity fragmentContainer;
    private ServicesHelper servicesHelper;

    private ListView listView;
    private View listFooterView;

    private DisplayChildArrayAdapter adapter;
    private ModelViewSynchronizer<BankAccount> bankAccountModelViewSynchronizer;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static S02TransferPseaBankDeclarationFragment newInstance(int index) {
        S02TransferPseaBankDeclarationFragment fragment = new S02TransferPseaBankDeclarationFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.i(getClass().getName() , "----------onCreateView()");

        rootView = inflater.inflate(R.layout.layout_listview, null);

        context = getActivity();
        app = (BbssApplication) getActivity().getApplication();
        fragmentContainer = ((TransferPseaFragmentContainerActivity) getActivity());
        servicesHelper = new ServicesHelper(context);

        listView = (ListView) rootView.findViewById(R.id.lvMain);

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        ServiceTransferToPsea serviceTransferToPsea = app.getServiceTransferToPsea();
        BankAccount bankAccount = bankAccountModelViewSynchronizer.getDataObject();
        ValidationInfo validationInfo = bankAccountModelViewSynchronizer.getValidationInfo();

        serviceTransferToPsea.setNewBankAccount(bankAccount);
        serviceTransferToPsea.clearClientPageValidations(CURRENT_POSITION);
        serviceTransferToPsea.addSectionPage(SerializedNames.SEC_SERVICE_TRANSFER_PSEA_CDA_BANK, CURRENT_POSITION);

        if (validationInfo.hasAnyValidationMessages()){
            serviceTransferToPsea.addPageValidation(CURRENT_POSITION, validationInfo);
        }

        return false;
    }

    @Override
    public boolean onResumeFragment() {
        List<ChildItem> childItems = app.getServiceTransferToPsea().getChildItems();
        adapter = new DisplayChildArrayAdapter(context, childItems, ChildListType.CDA_TO_PSEA);

        bankAccountModelViewSynchronizer = new ModelViewSynchronizer<BankAccount>(
                BankAccount.class, getMetaData(),
                rootView, SerializedNames.SEC_SERVICE_TRANSFER_PSEA_CDA_BANK);

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
                    if (app.getServiceTransferToPsea().hasClientValidations()) {
                        afterSubmit(true);
                    } else if(!app.getServiceTransferToPsea().isDeclared()){
                        servicesHelper.createDeclarationRequiredMessageBox();
                    } else {
                        new TransferCdaPseaTask(context).execute();
                    }
                }
            }
        });
    }

    private void setButtonClicks() {
        Log.i(getClass().getName(), "----------setButtonClicks()");

        LinearLayout llButtons = (LinearLayout) listFooterView.findViewById(R.id.screen_buttons);

        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, false);
        setSubmitButtonClick();
        fragmentContainer.setCancelButtonClick(llButtons, R.id.btnSecondInTwo);
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    private void displayData(String errorMessage) {
        Log.i(getClass().getName() , "----------displayData()");

        LayoutInflater inflater = getActivity().getLayoutInflater();

        BankAccount account = app.getServiceTransferToPsea().getNewBankAccount();

        if(account == null){
            account = new BankAccount();
        }

        //Listview - Header
        View listHeaderView = inflater.inflate(
                R.layout.section_header_row_and_instruction_row, null);
        if (listView.getHeaderViewsCount() == 0) {
            listView.addHeaderView(listHeaderView);
        }

        //Listview - Footer
        listFooterView = inflater.inflate(
                R.layout.fragment_service_list_footer_with_bank_and_dec, null);

        if (listView.getFooterViewsCount() == 0) {
            listView.addFooterView(listFooterView);
        }

        //Listview - Populate Data
        listView.setAdapter(adapter);
        TextView tvHeader = (TextView) listHeaderView.findViewById(R.id.section_header);
        tvHeader.setText(R.string.label_child);

        //Screen - Title and Instructions
        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(listHeaderView, CURRENT_POSITION, 0,
                true, errorMessage);

        //Screen - Populate Data
        bankAccountModelViewSynchronizer.setLabels();
        bankAccountModelViewSynchronizer.setHeaderTitle(R.id.section_new_account_detail,
                R.string.label_services_direct_credit_auth);
        bankAccountModelViewSynchronizer.displayDataObject(account);

        //Screen - Populate Data - Bank and Declaration Section
        setDeclarationSection();
        setBankDetailsSection();
    }

    private String displayValidationErrors() {
        ServiceTransferToPsea serviceTransferToPsea = app.getServiceTransferToPsea();

        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;
        if(serviceTransferToPsea.isDisplayValidationErrors()){
            ValidationInfo validationInfo = serviceTransferToPsea.getPageSectionValidations(
                    CURRENT_POSITION, SerializedNames.SEC_SERVICE_TRANSFER_PSEA_CDA_BANK);

            if(validationInfo.hasAnyValidationMessages()) {
                errorMessageList = validationInfo.getValidationMessages();
                bankAccountModelViewSynchronizer.displayValidationErrors(
                        validationInfo.getValidationMessages());

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
        ModelPropertyViewMeta viewMeta ;

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
        RelativeLayout rlBankDetailsDesc = (RelativeLayout) listFooterView.findViewById(
                R.id.type_selection_section);
        WebView wvBankDetailsDesc = (WebView) rlBankDetailsDesc.findViewById(
                R.id.wvScreenDescription);

        wvBankDetailsDesc.loadData(StringHelper.getJustifiedString(context,
                getString(R.string.desc_services_transfer_psea_bank_declaration_desc, BabyBonusConstants.MSF_PSEA_URL),
                0), "text/html", "utf-8");

//        wvBankDetailsDesc.loadData(StringHelper.getJustifiedString(context,
//                R.string.desc_services_transfer_psea_bank_declaration_desc,
//                R.color.theme_gray_default_bg), "text/html", "utf-8");
    }

    private void setDeclarationSection() {
        View declarationSection = listFooterView.findViewById(R.id.services_declaration_section);

        TextView tvDeclarationTitle = (TextView) declarationSection.findViewById(
                R.id.section_declaration);

        LinearLayout llDeclaration1 = (LinearLayout) declarationSection.findViewById(
                R.id.declaration_1);

        declarationSection.findViewById(R.id.declaration_2).setVisibility(View.GONE);
        declarationSection.findViewById(R.id.declaration_3).setVisibility(View.GONE);
        declarationSection.findViewById(R.id.declaration_4).setVisibility(View.GONE);

        WebView wvDeclaration1 = (WebView) llDeclaration1.findViewById(R.id.wvLabel);
        final CheckBox cbDeclaration1 = (CheckBox) llDeclaration1.findViewById(R.id.cbValue);

        tvDeclarationTitle.setText(R.string.label_common_declaration);

        wvDeclaration1.loadData(StringHelper.getJustifiedString(context,
                R.string.desc_services_common_declaration_1, 0), "text/html", "utf-8");

        cbDeclaration1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.getServiceTransferToPsea().setDeclared(cbDeclaration1.isChecked());
            }
        });
    }

    private void afterSubmit(final boolean isClientSubmit){
        servicesHelper.createClientValidationIssuesMessageBox(new MessageBoxButtonClickListener() {
            @Override
            public void onClickPositiveButton(DialogInterface dialog, int id) {
                ServiceTransferToPsea serviceTransferToPsea = app.getServiceTransferToPsea();
                int firstErrorPage = serviceTransferToPsea.getFirstErrorPage();

                if (isClientSubmit) {
                    serviceTransferToPsea.setDisplayValidationErrors(serviceTransferToPsea.hasClientValidations());
                } else {
                    serviceTransferToPsea.setDisplayValidationErrors(serviceTransferToPsea.hasAnyValidations());
                }

                if (firstErrorPage == CURRENT_POSITION) {
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

    private class TransferCdaPseaTask extends AsyncTask<Void, Void, ServerResponse> {

        private Context context;
        private ProgressDialog dialog;

        public TransferCdaPseaTask(Context context) {
            this. context = context;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(StringHelper.getStringByResourceId(context,
                    R.string.progress_service_psea_updating));
            dialog.show();
        }

        @Override
        protected ServerResponse doInBackground(Void... params) {
            return ProxyFactory.getEServiceProxy().updateTransferCdaToPsea(app.getServiceTransferToPsea());
        }

        protected void onPostExecute(ServerResponse response) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if(response.getResponseType() == ServerResponseType.SUCCESS){
                servicesHelper.createServiceResponseMessageBox(app, response.getMessage(),
                        response.getAppId(), ChildListType.CDA_TO_PSEA);
            } else if(response.getResponseType() == ServerResponseType.APPLICATION_ERROR){
                String message = StringHelper.getStringByResourceId(context, R.string.error_common_application_error);
                MessageBox.show(context, message, false, true, R.string.btn_ok, false, 0, null);
            } else {
                afterSubmit(false);
            }
        }
    }
}
