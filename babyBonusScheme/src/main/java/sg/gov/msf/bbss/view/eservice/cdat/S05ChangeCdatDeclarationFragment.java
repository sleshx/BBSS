package sg.gov.msf.bbss.view.eservice.cdat;


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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.ui.component.MessageBox;
import sg.gov.msf.bbss.apputils.ui.helper.MessageBoxButtonClickListener;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.logic.server.response.ServerResponse;
import sg.gov.msf.bbss.logic.server.response.ServerResponseType;
import sg.gov.msf.bbss.logic.type.ChildListType;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceChangeCdat;
import sg.gov.msf.bbss.view.eservice.ServicesHelper;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class S05ChangeCdatDeclarationFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private ChangeCdatFragmentContainerActivity fragmentContainer;
    private ServicesHelper servicesHelper;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static S05ChangeCdatDeclarationFragment newInstance(int index) {
        S05ChangeCdatDeclarationFragment fragment = new S05ChangeCdatDeclarationFragment();
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
        fragmentContainer = ((ChangeCdatFragmentContainerActivity) getActivity());
        servicesHelper = new ServicesHelper(context);

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        return false;
    }

    @Override
    public boolean onResumeFragment() {
        displayData();
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
                    ServiceChangeCdat serviceChangeCdat = app.getServiceChangeCdat();
                    if (serviceChangeCdat.hasClientValidations()) {
                        afterSubmit(true);
                    } else if(!serviceChangeCdat.isDeclared1() ||
                              !serviceChangeCdat.isDeclared2()) {
                        servicesHelper.createDeclarationRequiredMessageBox();
                    } else {
                        new UpdateChildDevAccountTrusteeTask(context).execute();
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

    private void displayData() {
        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(rootView, CURRENT_POSITION, 0, true, AppConstants.EMPTY_STRING);

        setDeclarationSection();
        hideUnwantedIncludeLayouts();
    }

    //--- HELPERS ----------------------------------------------------------------------------------

    private void setDeclarationSection() {
        TextView tvDeclarationTitle = (TextView) rootView.findViewById( R.id.section_declaration);
        LinearLayout llDeclaration1 = (LinearLayout) rootView.findViewById(R.id.declaration_1);
        LinearLayout llDeclaration2 = (LinearLayout) rootView.findViewById(R.id.declaration_2);

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
                app.getServiceChangeCdat().setDeclared1(cbDeclaration1.isChecked());
            }
        });
        cbDeclaration2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.getServiceChangeCdat().setDeclared2(cbDeclaration2.isChecked());
            }
        });
    }

    private void hideUnwantedIncludeLayouts() {
        rootView.findViewById(R.id.bank_account_details_section).setVisibility(View.GONE);
        rootView.findViewById(R.id.declaration_3).setVisibility(View.GONE);
        rootView.findViewById(R.id.declaration_4).setVisibility(View.GONE);
    }

    private void afterSubmit(final boolean isClientSubmit){
        servicesHelper.createClientValidationIssuesMessageBox(new MessageBoxButtonClickListener() {
            @Override
            public void onClickPositiveButton(DialogInterface dialog, int id) {
                ServiceChangeCdat serviceChangeCdat = app.getServiceChangeCdat();

                if(isClientSubmit) {
                    serviceChangeCdat.setDisplayValidationErrors(serviceChangeCdat.hasClientValidations());
                } else {
                    serviceChangeCdat.setDisplayValidationErrors(serviceChangeCdat.hasAnyValidations());
                }

                fragmentContainer.jumpToPageWithIndex(serviceChangeCdat.getFirstErrorPage());
            }

            @Override
            public void onClickNegativeButton(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
    }

    //--- ASYNC TASKS ------------------------------------------------------------------------------

    private class UpdateChildDevAccountTrusteeTask extends AsyncTask<Void, Void, ServerResponse> {
        private Context context;
        private ProgressDialog dialog;

        public UpdateChildDevAccountTrusteeTask(Context context) {
            this. context = context;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(StringHelper.getStringByResourceId(context,
                    R.string.progress_service_change_cdat_updating));
            dialog.show();
        }

        @Override
        protected ServerResponse doInBackground(Void... params) {
            return ProxyFactory.getEServiceProxy().updateChildDevAccountTrustee(app.getServiceChangeCdat());
        }

        protected void onPostExecute(ServerResponse response) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if(response.getResponseType() == ServerResponseType.SUCCESS){
                servicesHelper.createServiceResponseMessageBox(app, response.getMessage(),
                        response.getAppId(), ChildListType.CHANGE_CDAT);
            } else if(response.getResponseType() == ServerResponseType.APPLICATION_ERROR){
                String message = StringHelper.getStringByResourceId(context, R.string.error_common_application_error);
                MessageBox.show(context, message, false, true, R.string.btn_ok, false, 0, null);
            } else {
                afterSubmit(false);
            }
        }
    }

}
