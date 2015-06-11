package sg.gov.msf.bbss.view.eservice.cdabtc;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
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
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceCdabTc;
import sg.gov.msf.bbss.view.eservice.ServicesHelper;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class S02AcceptCdabDeclarationFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;

    private static  final String SERIALIZED_SECTION_NAME =
            SerializedNames.SEC_SERVICE_CHANGE_NAH_CG_AUTHORIZER;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private ListView listView;
    private View listFooterView;
    private View listHeaderView;

    private AcceptCdabTcFragmentContainerActivity fragmentContainer;
    private ServicesHelper servicesHelper;

    private DisplayChildArrayAdapter adapter;
    private List<ChildItem> childItems;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static S02AcceptCdabDeclarationFragment newInstance(int index) {
        S02AcceptCdabDeclarationFragment fragment = new S02AcceptCdabDeclarationFragment();
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
        fragmentContainer = ((AcceptCdabTcFragmentContainerActivity) getActivity());
        servicesHelper = new ServicesHelper(context);

        listView = (ListView) rootView.findViewById(R.id.lvMain);

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        Log.i(getClass().getName() , "----------onPauseFragment()");
        app.getServiceCdabTc().addSectionPage(SerializedNames.SEC_SERVICE_CHANGE_CDABTC_ROOT, CURRENT_POSITION);
        return false;
    }

    @Override
    public boolean onResumeFragment() {
        Log.i(getClass().getName() , "----------onResumeFragment()");

        childItems = app.getServiceCdabTc().getChildItems();
        adapter = new DisplayChildArrayAdapter(context, childItems, ChildListType.TERMS_AND_COND);

        displayData(getActivity().getLayoutInflater());
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
        Button submit = (Button) rootView.findViewById(R.id.btnFirstInTwo);
        submit.setText(R.string.btn_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!onPauseFragment(true)) {
                    ServiceCdabTc serviceCdabTc = app.getServiceCdabTc();

                    if (serviceCdabTc.hasClientValidations()) {
                        afterSubmit(true);
                    } else if(!serviceCdabTc.isDeclared1() || !serviceCdabTc.isDeclared2()){
                        servicesHelper.createDeclarationRequiredMessageBox();
                    } else {
                        new UpdateAcceptCdaBankTcTask(context).execute();
                    }
                }
            }
        });
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    private void displayData(LayoutInflater inflater) {
        Log.i(getClass().getName() , "----------displayData()");

        //Listview - Header
        listHeaderView = inflater.inflate(
                R.layout.section_header_row_and_instruction_row, null);
        if (listView.getHeaderViewsCount() == 0) {
            listView.addHeaderView(listHeaderView);
        }

        //Listview - Footer
        listFooterView = inflater.inflate(
                R.layout.fragment_service_list_footer_with_dec, null);
        ((RelativeLayout)listFooterView.findViewById(R.id.type_selection_section)).setVisibility(View.GONE);
        if (listView.getFooterViewsCount() == 0) {
            listView.addFooterView(listFooterView);
        }

        //Screen - Title and Instructions
        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(listHeaderView, CURRENT_POSITION, 0,
                false, AppConstants.EMPTY_STRING);

        //Listview - Populate Data
        listView.setAdapter(adapter);
        TextView tvHeader = (TextView) listHeaderView.findViewById(R.id.section_header);
        tvHeader.setText(R.string.label_child);

        //Screen - Populate Data - Declaration Section
        setDeclarationSection();
    }

    private String displayValidationErrors() {
        ServiceCdabTc serviceCdabTc = app.getServiceCdabTc();
        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;

        if(serviceCdabTc.isDisplayValidationErrors()){
            ValidationInfo validationInfo = serviceCdabTc.getPageSectionValidations(
                    CURRENT_POSITION, SerializedNames.SEC_SERVICE_CHANGE_CDABTC_ROOT);

            if(validationInfo.hasAnyValidationMessages()) {
                errorMessageList = validationInfo.getValidationMessages();

                for(ValidationMessage messageList : errorMessageList) {
                    errorMessage = errorMessage + messageList.getMessage() +
                            AppConstants.SYMBOL_BREAK_LINE;
                }
            }
        }
        return errorMessage;
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
                R.string.desc_services_common_declaration_3, 0), "text/html", "utf-8");

        cbDeclaration1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.getServiceCdabTc().setDeclared1(cbDeclaration1.isChecked());
            }
        });
        cbDeclaration2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.getServiceCdabTc().setDeclared2(cbDeclaration2.isChecked());
            }
        });
    }

    private void afterSubmit(final boolean isClientSubmit){
        servicesHelper.createClientValidationIssuesMessageBox(new MessageBoxButtonClickListener() {
            @Override
            public void onClickPositiveButton(DialogInterface dialog, int id) {
                ServiceCdabTc serviceCdabTc = app.getServiceCdabTc();
                int firstErrorPage = serviceCdabTc.getFirstErrorPage();

                if(isClientSubmit) {
                    serviceCdabTc.setDisplayValidationErrors(serviceCdabTc.hasClientValidations());
                } else {
                    serviceCdabTc.setDisplayValidationErrors(serviceCdabTc.hasAnyValidations());
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

    private class UpdateAcceptCdaBankTcTask extends AsyncTask<Void, Void, ServerResponse> {

        private Context context;
        private ProgressDialog dialog;

        public UpdateAcceptCdaBankTcTask(Context context) {
            this. context = context;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(StringHelper.getStringByResourceId(context,
                    R.string.progress_service_cdab_tc_updating));
            dialog.show();
        }

        @Override
        protected ServerResponse doInBackground(Void... params) {
            return ProxyFactory.getEServiceProxy().updateCdaBankTermsAndCond(app.getServiceCdabTc());
        }

        protected void onPostExecute(ServerResponse response) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if(response.getResponseType() == ServerResponseType.SUCCESS){
                servicesHelper.createServiceResponseMessageBox(app, response.getMessage(),
                        response.getAppId(), ChildListType.TERMS_AND_COND);
            } else if(response.getResponseType() == ServerResponseType.APPLICATION_ERROR){
                String message = StringHelper.getStringByResourceId(context, R.string.error_common_application_error);
                MessageBox.show(context, message, false, true, R.string.btn_ok, false, 0, null);
            } else {
                afterSubmit(false);
            }
        }
    }
}
