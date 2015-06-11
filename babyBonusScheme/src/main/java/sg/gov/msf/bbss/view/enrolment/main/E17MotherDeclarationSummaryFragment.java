package sg.gov.msf.bbss.view.enrolment.main;


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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
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
import sg.gov.msf.bbss.logic.adapter.enrolment.EnrolmentMotherDeclarationChildListAdapter;
import sg.gov.msf.bbss.logic.adapter.util.AddChildListItem;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.logic.server.response.ServerResponse;
import sg.gov.msf.bbss.logic.server.response.ServerResponseType;
import sg.gov.msf.bbss.logic.type.ChildListItemType;
import sg.gov.msf.bbss.logic.type.SubmitType;
import sg.gov.msf.bbss.model.entity.childdata.ChildDeclaration;
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentForm;
import sg.gov.msf.bbss.view.enrolment.sub.MotherDeclarationFragmentContainerActivity;
import sg.gov.msf.bbss.view.eservice.ServicesHomeActivity;

/**
 * Created by bandaray
 */
public class E17MotherDeclarationSummaryFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;

    public static int ADD_DECLARATION_REQUEST_CODE = 0;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private ListView listView;

    private EnrolmentFragmentContainerActivity fragmentContainer;

    private boolean isHeaderLoaded;
    private boolean isFooterLoaded;

    private EnrolmentMotherDeclarationChildListAdapter adapter;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static E17MotherDeclarationSummaryFragment newInstance(int index) {
        E17MotherDeclarationSummaryFragment fragment = new E17MotherDeclarationSummaryFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Log.i(getClass().getName(), "----------onCreateView()");

        rootView = inflater.inflate(R.layout.layout_listview, null);

        context = getActivity();
        app = (BbssApplication) getActivity().getApplication();
        fragmentContainer = ((EnrolmentFragmentContainerActivity) getActivity());

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        listView = (ListView) rootView.findViewById(R.id.lvMain);
        isHeaderLoaded = false;
        isFooterLoaded = false;

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        return false;
    }

    @Override
    public boolean onResumeFragment() {
        Log.i(getClass().getName() , "----------onResumeFragment()");

        displayData();
        setButtonClicks();

        return false;
    }

    //--- ACTIVITY NAVIGATION ----------------------------------------------------------------------

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(getClass().getName() , "----------onActivityResult()");

        if (requestCode == ADD_DECLARATION_REQUEST_CODE) {
            populateListView(app.getEnrolmentForm().getChildDeclarations());
        }
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {
        Log.i(getClass().getName() , "----------setButtonClicks()");

        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, false);
        //fragmentContainer.setNextButtonClick(rootView, R.id.btnFirstInThree, CURRENT_POSITION, true);
        setSubmitButtonClick();
        fragmentContainer.setSaveAsDraftButtonClick(rootView, R.id.btnSecondInThree);
        fragmentContainer.setCancelButtonClick(rootView, R.id.btnThirdInThree);

        setAddChildButtonClick();
    }

    private void setAddChildButtonClick() {
        Log.i(getClass().getName() , "----------setAddChildButtonClick()");

        Button addChildButton = (Button) rootView.findViewById(R.id.btnFirstInOne);
        addChildButton.setText(R.string.btn_add_declaration);
        addChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context,
                        MotherDeclarationFragmentContainerActivity.class), ADD_DECLARATION_REQUEST_CODE);
            }
        });
    }

    private void afterSubmit(final boolean isClientSubmit) {
        MessageBox.show(context, StringHelper.getStringByResourceId(
                        context, R.string.error_common_form_not_properly_completed),
                false, true, R.string.btn_ok, false, 0,
                new MessageBoxButtonClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        EnrolmentForm enrolmentForm = app.getEnrolmentForm();
                        int firstErrorPage = enrolmentForm.getFirstErrorPage();

                        if (isClientSubmit) {
                            enrolmentForm.setDisplayValidationErrors(enrolmentForm.hasClientValidations());
                        } else {
                            enrolmentForm.setDisplayValidationErrors(enrolmentForm.hasAnyValidations());
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

    private void setSubmitButtonClick() {
        Button next = (Button) rootView.findViewById(R.id.btnFirstInThree);
        next.setText(R.string.btn_submit);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!onPauseFragment(true)) {
                    if (app.getEnrolmentForm().hasClientValidations()) {
                        afterSubmit(true);
                    } else {
                        new UpdateMotherDeclarationTask(context).execute();
                    }
                }
            }
        });
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    private void displayData() {
        Log.i(getClass().getName() , "----------displayData()");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        LinearLayout listFooterView;
        List<ChildDeclaration> childDeclarations = app.getEnrolmentForm().getChildDeclarations();

        if(childDeclarations == null){
            childDeclarations = new ArrayList<ChildDeclaration>();
        }

        View listHeaderView = inflater.inflate(
                R.layout.section_header_row_and_instruction_row, null);
        listFooterView = (LinearLayout) inflater.inflate(
                R.layout.fragment_enrolment_mother_dec_summary_footer, null);

        //Listview - Header
        if (!isHeaderLoaded) {
            listView.addHeaderView(listHeaderView);
            isHeaderLoaded = true;
        }

        //Listview - Footer
        if (! isFooterLoaded) {
            listView.addFooterView(listFooterView);
            isFooterLoaded = true;
        }

        //Listview - Populate Data
        TextView tvHeader = (TextView) listHeaderView.findViewById(R.id.section_header);
        tvHeader.setText(R.string.label_child_dec_header);
        populateListView(childDeclarations);

        //Screen - Title and Instructions
        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(listHeaderView, CURRENT_POSITION,
                R.string.desc_services_change_cdab_instruction_desc, false,
                AppConstants.EMPTY_STRING);
    }

    private String displayValidationErrors() {
        EnrolmentForm enrolmentForm = app.getEnrolmentForm();
        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;

        if(enrolmentForm.isDisplayValidationErrors()){
            ValidationInfo validationInfo = enrolmentForm.getPageSectionValidations(
                    CURRENT_POSITION, SerializedNames.SEC_SERVICE_CHANGE_NAH_CG_AUTHORIZER);

            if(validationInfo.hasAnyValidationMessages()) {
                errorMessageList = validationInfo.getValidationMessages();
                //bankAccountModelViewSynchronizer.displayValidationErrors(errorMessageList);

                for(ValidationMessage messageList : errorMessageList) {
                    errorMessage = errorMessage + messageList.getMessage() +
                            AppConstants.SYMBOL_BREAK_LINE;
                }
            }
        }

        return errorMessage;
    }

    //--- LIST ADAPTER -----------------------------------------------------------------------------

    private void populateListView(List<ChildDeclaration> childDeclarations) {
        adapter = new EnrolmentMotherDeclarationChildListAdapter(context, app,
                R.layout.layout_add_child_declaration_item, childDeclarations, listButtonClickListener);

        listView.setAdapter(adapter);
    }

    private View.OnClickListener listButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.imageButton) {
                AddChildListItem item = (AddChildListItem) v.getTag();
                int position = item.getListPosition();

                if (item.getType().equals(ChildListItemType.DELETE)){
                    adapter.remove(adapter.getItem(position));
                }
            }
        }
    };

    //--- ASYNC TASKS ------------------------------------------------------------------------------

    private class UpdateMotherDeclarationTask extends AsyncTask<Void, Void, ServerResponse> {

        private Context context;
        private ProgressDialog dialog;

        public UpdateMotherDeclarationTask(Context context) {
            this. context = context;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(StringHelper.getStringByResourceId(context,
                    R.string.progress_enrolment_insert_application));
            dialog.show();
        }

        @Override
        protected ServerResponse doInBackground(Void... params) {
            return ProxyFactory.getEnrolmentProxy().updateEnrolmentApplication(app.getEnrolmentForm(), SubmitType.SAVE);
        }

        protected void onPostExecute(ServerResponse response) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if(response.getResponseType() == ServerResponseType.SUCCESS){
                MessageBox.show(context, response.getMessage(), false, true, R.string.btn_ok, false, 0,
                        new MessageBoxButtonClickListener() {
                            @Override
                            public void onClickPositiveButton(DialogInterface dialog, int id) {
                                app.setEnrolmentForm(null);
                                context.startActivity(new Intent(context, ServicesHomeActivity.class));
                            }

                            @Override
                            public void onClickNegativeButton(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
            } else if(response.getResponseType() == ServerResponseType.APPLICATION_ERROR){
                String message = StringHelper.getStringByResourceId(context, R.string.error_common_application_error);
                MessageBox.show(context, message, false, true, R.string.btn_ok, false, 0, null);
            } else {
                afterSubmit(false);
            }
        }
    }
}

