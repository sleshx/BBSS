package sg.gov.msf.bbss.view.eservice.cdab;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import sg.gov.msf.bbss.logic.BabyBonusValidationHandler;
import sg.gov.msf.bbss.logic.adapter.DisplayChildArrayAdapter;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.logic.server.response.ServerResponse;
import sg.gov.msf.bbss.logic.server.response.ServerResponseType;
import sg.gov.msf.bbss.logic.server.task.DataCodes;
import sg.gov.msf.bbss.logic.type.ChildListType;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.entity.common.CdaBankAccount;
import sg.gov.msf.bbss.model.entity.masterdata.Bank;
import sg.gov.msf.bbss.model.entity.masterdata.GenericDataItem;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceChangeCdab;
import sg.gov.msf.bbss.view.eservice.ServicesHelper;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class S02ChangeCdabBankDeclarationFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private ChangeCdabFragmentContainerActivity fragmentContainer;
    private ServicesHelper servicesHelper;

    private ListView listView;
    private View listHeaderView;
    private View listFooterView;

    private DisplayChildArrayAdapter adapter;
    private List<ChildItem> childItems;
    private ModelViewSynchronizer<CdaBankAccount> cdaBankModelViewSynchronizer;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static S02ChangeCdabBankDeclarationFragment newInstance(int index) {
        S02ChangeCdabBankDeclarationFragment fragment = new S02ChangeCdabBankDeclarationFragment();
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
        fragmentContainer = ((ChangeCdabFragmentContainerActivity) getActivity());
        servicesHelper = new ServicesHelper(context);

        listView = (ListView) rootView.findViewById(R.id.lvMain);

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        Log.i(getClass().getName() , "----------onPauseFragment()");

        ServiceChangeCdab serviceChangeCdab = app.getServiceChangeCdab();
        CdaBankAccount cdaBankAccount = cdaBankModelViewSynchronizer.getDataObject();
        ValidationInfo validationInfo = cdaBankModelViewSynchronizer.getValidationInfo();

        serviceChangeCdab.setCdaBankAccount(cdaBankAccount);
        serviceChangeCdab.clearClientPageValidations(CURRENT_POSITION);
        serviceChangeCdab.addSectionPage(SerializedNames.SEC_SERVICE_CHANGE_CDAB_ROOT, CURRENT_POSITION);

        if (validationInfo.hasAnyValidationMessages()){
            serviceChangeCdab.addPageValidation(CURRENT_POSITION, validationInfo);
        }

        if (isValidationRequired) {
            return  BabyBonusValidationHandler.validateSameBankOrAccount(context,
                    ChildListType.CHANGE_CDAB,
                    serviceChangeCdab.getCdaBankAccount(),
                    serviceChangeCdab.getChildItems(), adapter);
        }

        return false;
    }

    @Override
    public boolean onResumeFragment() {
        Log.i(getClass().getName() , "----------onResumeFragment()");

        childItems = app.getServiceChangeCdab().getChildItems();
        adapter = new DisplayChildArrayAdapter(context, childItems, ChildListType.CHANGE_CDAB);

        cdaBankModelViewSynchronizer = new ModelViewSynchronizer<CdaBankAccount>(
                CdaBankAccount.class, getMetaData(), rootView, SerializedNames.SEC_SERVICE_CHANGE_CDAB_ROOT);

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
                ServiceChangeCdab serviceChangeCdab = app.getServiceChangeCdab();

                if (!onPauseFragment(true)) {
                    if (serviceChangeCdab.hasClientValidations()) {
                        afterSubmit(true);
                    } else if(!serviceChangeCdab.isDeclared1() || !serviceChangeCdab.isDeclared2()){
                        servicesHelper.createDeclarationRequiredMessageBox();
                    } else {
                        new UpdateChildDevAccBankTask(context).execute();
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

        //Listview - Header
        listHeaderView = inflater.inflate(
                R.layout.section_header_row_and_instruction_row, null);
        if (listView.getHeaderViewsCount() == 0) {
            listView.addHeaderView(listHeaderView);
        }

        //Listview - Footer
        listFooterView = inflater.inflate(
                R.layout.fragment_service_list_footer_with_type_and_dec, null);
        if (listView.getFooterViewsCount() == 0) {
            listView.addFooterView(listFooterView);
        }

        //Listview - Populate Data
        listView.setAdapter(adapter);
        TextView tvHeader = (TextView) listHeaderView.findViewById(R.id.section_header);
        tvHeader.setText(R.string.label_child);

        //Screen - Populate Data - Bank Account Section
        CdaBankAccount cdaBank = app.getServiceChangeCdab().getCdaBankAccount();

        if(cdaBank == null){
            cdaBank = new CdaBankAccount();
        }

        cdaBankModelViewSynchronizer.setLabels();
        cdaBankModelViewSynchronizer.setHeaderTitle(R.id.section_new_type_header,
                R.string.label_services_new_cdab);
        cdaBankModelViewSynchronizer.displayDataObject(cdaBank);

        //Screen - Populate Data - Declaration Section
        setDeclarationSection();

        //Screen - Title and Instructions
        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(listHeaderView, CURRENT_POSITION, 0, true, errorMessage);
    }

    private String displayValidationErrors() {
        ServiceChangeCdab serviceChangeCdab = app.getServiceChangeCdab();
        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;

        if(serviceChangeCdab.isDisplayValidationErrors()){
            ValidationInfo validationInfo = serviceChangeCdab.getPageSectionValidations(
                    CURRENT_POSITION, SerializedNames.SEC_SERVICE_CHANGE_CDAB_ROOT);

            if(validationInfo.hasAnyValidationMessages()) {
                errorMessageList = validationInfo.getValidationMessages();
                cdaBankModelViewSynchronizer.displayValidationErrors(errorMessageList);

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

        ArrayAdapter<GenericDataItem> changeCdabChangeReasonAdapter =
                new ArrayAdapter<GenericDataItem>(context, android.R.layout.simple_list_item_1,
                        new ArrayList<GenericDataItem>());

        ArrayAdapter<Bank> bankAdapter = new ArrayAdapter<Bank>(context,
                android.R.layout.simple_list_item_1, new ArrayList<Bank>());

        servicesHelper.displayChangeReasons(changeCdabChangeReasonAdapter);
        servicesHelper.displayBankMa(bankAdapter);

        try {
            //Change Reason
            viewMeta = new ModelPropertyViewMeta(CdaBankAccount.FIELD_CHANGE_REASON);
            viewMeta.setIncludeTagId(R.id.services_change_reason);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DROP_DOWN);
            viewMeta.setSerialName(SerializedNames.SN_CDAB_CHANGE_REASON);
            viewMeta.setDropDownAdapter(changeCdabChangeReasonAdapter);
            viewMeta.setDropDownItemSelectedListener(
                    new BankChangeReasonItemSelectionListener(changeCdabChangeReasonAdapter));

            metaDataList.add(CdaBankAccount.class,viewMeta);

            //Change Reason Other Description
            viewMeta = new ModelPropertyViewMeta(CdaBankAccount.FIELD_CHANGE_REASON_OTHER);
            viewMeta.setIncludeTagId(R.id.services_change_reason_other);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);
            viewMeta.setSerialName(SerializedNames.SN_CDAB_CHANGE_REASON_OTHER);

            metaDataList.add(CdaBankAccount.class, viewMeta);

            //New CDA Bank
            viewMeta = new ModelPropertyViewMeta(CdaBankAccount.FIELD_BANK);
            viewMeta.setIncludeTagId(R.id.services_new_type);
            viewMeta.setMandatory(true);
            viewMeta.setEditable(true);
            viewMeta.setEditType(ModelPropertyEditType.DROP_DOWN);
            viewMeta.setDropDownAdapter(bankAdapter);
            viewMeta.setSerialName(SerializedNames.SN_CDAB_ID);

            metaDataList.add(CdaBankAccount.class, viewMeta);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
    }

    //--- HELPERS  ---------------------------------------------------------------------------------

    private void setDeclarationSection() {
        View declarationSection = listFooterView.findViewById(R.id.services_declaration_section);

        TextView tvDeclarationTitle = (TextView) declarationSection.findViewById(
                R.id.section_declaration);

        LinearLayout llDeclaration1 = (LinearLayout) declarationSection.findViewById(
                R.id.declaration_1);
        LinearLayout llDeclaration2 = (LinearLayout) declarationSection.findViewById(
                R.id.declaration_2);

        declarationSection.findViewById(R.id.declaration_3).setVisibility(View.GONE);
        declarationSection.findViewById(R.id.declaration_4).setVisibility(View.GONE);

        WebView wvDeclaration1 = (WebView) llDeclaration1.findViewById(R.id.wvLabel);
        WebView wvDeclaration2 = (WebView) llDeclaration2.findViewById(R.id.wvLabel);

        final CheckBox cbDeclaration1 = (CheckBox) llDeclaration1.findViewById(R.id.cbValue);
        final CheckBox cbDeclaration2 = (CheckBox) llDeclaration2.findViewById(R.id.cbValue);

        tvDeclarationTitle.setText(R.string.label_common_declaration);

        wvDeclaration1.loadData(StringHelper.getJustifiedString(context,
                R.string.desc_services_common_declaration_2, 0), "text/html", "utf-8");
        wvDeclaration2.loadData(StringHelper.getJustifiedString(context,
                getString(R.string.desc_services_common_declaration_3, BabyBonusConstants.MSF_CDA_URL),
                0), "text/html", "utf-8");

        cbDeclaration1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.getServiceChangeCdab().setDeclared1(cbDeclaration1.isChecked());
            }
        });
        cbDeclaration2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.getServiceChangeCdab().setDeclared2(cbDeclaration2.isChecked());
            }
        });
    }

    //--- DROPDOWN SELECTION LISTENERS  ------------------------------------------------------------

    private class BankChangeReasonItemSelectionListener
            implements AdapterView.OnItemSelectedListener {
        private ArrayAdapter<GenericDataItem> adapter;

        public BankChangeReasonItemSelectionListener(ArrayAdapter<GenericDataItem> adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            GenericDataItem item = adapter.getItem(position);

            LinearLayout otherReasonView = (LinearLayout)rootView.findViewById(
                    R.id.services_change_reason_other);

            if (item.getName().equals(DataCodes.CHANGE_REASON_OTHER)){
                otherReasonView.setVisibility(View.VISIBLE);
                cdaBankModelViewSynchronizer.setFieldMandatory(CdaBankAccount.FIELD_CHANGE_REASON_OTHER, true);
            } else {
                otherReasonView.setVisibility(View.GONE);
                cdaBankModelViewSynchronizer.setFieldMandatory(CdaBankAccount.FIELD_CHANGE_REASON_OTHER, false);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            LinearLayout otherReasonView = (LinearLayout)rootView.findViewById(
                    R.id.services_change_reason_other);
            otherReasonView.setVisibility(View.GONE);
        }
    }

    private void afterSubmit(final boolean isClientSubmit){
        servicesHelper.createClientValidationIssuesMessageBox(new MessageBoxButtonClickListener() {
            @Override
            public void onClickPositiveButton(DialogInterface dialog, int id) {
                ServiceChangeCdab serviceChangeCdab = app.getServiceChangeCdab();
                int firstErrorPage = serviceChangeCdab.getFirstErrorPage();

                if(isClientSubmit) {
                    serviceChangeCdab.setDisplayValidationErrors(serviceChangeCdab.hasClientValidations());
                } else {
                    serviceChangeCdab.setDisplayValidationErrors(serviceChangeCdab.hasAnyValidations());
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

    private class UpdateChildDevAccBankTask extends AsyncTask<Void, Void, ServerResponse> {

        private Context context;
        private ProgressDialog dialog;

        public UpdateChildDevAccBankTask(Context context) {
            this. context = context;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(StringHelper.getStringByResourceId(context,
                    R.string.progress_service_change_nan_updating));
            dialog.show();
        }

        @Override
        protected ServerResponse doInBackground(Void... params) {
            return ProxyFactory.getEServiceProxy().updateChildDevAccountBank(app.getServiceChangeCdab());
        }

        protected void onPostExecute(ServerResponse response) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if(response.getResponseType() == ServerResponseType.SUCCESS){
                servicesHelper.createServiceResponseMessageBox(app, response.getMessage(),
                        response.getAppId(), ChildListType.CHANGE_CDAB);
            } else if(response.getResponseType() == ServerResponseType.APPLICATION_ERROR){
                String message = StringHelper.getStringByResourceId(context, R.string.error_common_application_error);
                MessageBox.show(context, message, false, true, R.string.btn_ok, false, 0, null);
            } else {
                afterSubmit(false);
            }
        }
    }
}
