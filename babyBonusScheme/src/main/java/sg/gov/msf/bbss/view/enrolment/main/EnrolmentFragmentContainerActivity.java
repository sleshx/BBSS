package sg.gov.msf.bbss.view.enrolment.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.ui.component.MessageBox;
import sg.gov.msf.bbss.apputils.ui.helper.CustomViewPager;
import sg.gov.msf.bbss.apputils.ui.helper.MessageBoxButtonClickListener;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizardPagerAdapter;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.FragmentContainerActivityHelper;
import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.logic.type.ChildRegistrationType;
import sg.gov.msf.bbss.logic.type.EnrolmentAppType;
import sg.gov.msf.bbss.logic.type.MasterDataType;
import sg.gov.msf.bbss.model.entity.masterdata.Bank;
import sg.gov.msf.bbss.model.entity.masterdata.GenericDataItem;
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentForm;
import sg.gov.msf.bbss.view.MainActivity;

/**
 * Created by bandaray
 */
public class EnrolmentFragmentContainerActivity extends FragmentActivity {

    private Context context;
    private CustomViewPager viewPager;
    private int fragmentCount;
    private FragmentWizardPagerAdapter pagerAdapter;
    private FragmentContainerActivityHelper helper;

    private ImageView backButton;
    private Button nextButton;
    private Button saveAsDraftButton;
    private Button cancelButton;
    private BbssApplication app;

    private EnrolmentAppType appType;
    private boolean dummyIsPrePopulated;
    private boolean isPermitted = true;

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_viewpager);
        context = this;
        getActionBar().hide();

        app = (BbssApplication) getApplication();

        dummyIsPrePopulated = getIntent().getBooleanExtra(BabyBonusConstants.ENROLMENT_IS_PRE_PO, false);
        appType = EnrolmentAppType.parseType(getIntent().getStringExtra(BabyBonusConstants.ENROLMENT_APP_MODE));

        app.setEnrolmentForm(new EnrolmentForm(dummyIsPrePopulated, appType));

        switch (appType) {
            case EDIT:
            case VIEW:
                new GetEditViewEnrolmentDataTask(appType).execute();
                break;
            case NEW:
                if (dummyIsPrePopulated && isPermitted) {
                    new GetPrePopulatedEnrolmentDataTask(appType).execute();
                } else {
                    initializeViewPagerAndAdapter();
                }
                break;
        }
    }

    //--- BACK NAVIGATION --------------------------------------------------------------------------

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    private List<Fragment> getWizardPages() {
        List<Fragment> fragments = new ArrayList<Fragment>();
        EnrolmentForm enrolmentForm = app.getEnrolmentForm();
        EnrolmentAppType appType = enrolmentForm.getAppType();

        if (appType == null) {
            appType = EnrolmentAppType.NEW;
        }
        switch (appType) {
            case EDIT:
            case VIEW:
                fragments = getEditOrViewWizardPages(enrolmentForm);
                break;
            case NEW:
                if (enrolmentForm.isPrePopulated()) {
                    fragments = getNewPrePopulatesWizardPages(enrolmentForm);
                } else {
                    fragments = getNewAppWizardPages();
                }
                break;
        }

        fragmentCount = fragments.size();
        return fragments;
    }

    public void jumpToPageWithIndex(int pageIndex) {
        viewPager.setCurrentItem(pageIndex, true);
    }

    public int getWizardPageCount() {
        return fragmentCount;
    }

    //--- FRAGMENT NAVIGATION HELPERS --------------------------------------------------------------

    private List<Fragment> getEditOrViewWizardPages(EnrolmentForm enrolmentForm) {
        List<Fragment> fragments = new ArrayList<Fragment>();

        ChildRegistrationType regType = enrolmentForm.getChildRegistration().
                getRegistrationType();

        fragments.add(E01FatherParticularsFragment.newInstance(0));
        fragments.add(E02MotherParticularsFragment.newInstance(1));

        if (regType == ChildRegistrationType.CITIZENSHIP) {
            fragments.add(E05CitizenshipRegistrationFragment.newInstance(2));
        } else if(regType == ChildRegistrationType.ADOPTION) {
            fragments.add(E06AdoptionRegistrationFragment.newInstance(2));
        } else { //ChildRegistrationType.POST_BIRTH
            fragments.add(E04PostBirthRegistrationFragment.newInstance(2));
        }

        fragments.add(E07NaHolderTypeSelectionFragment.newInstance(3));
        fragments.add(E08NaHolderParticularsFragment.newInstance(4));
        fragments.add(E09NaHolderAddressFragment.newInstance(5));
        fragments.add(E10CashGiftBankFragment.newInstance(6));
        fragments.add(E11CdaTrusteeTypeSelectionFragment.newInstance(7));
        fragments.add(E12CdaTrusteeParticularsFragment.newInstance(8));
        fragments.add(E13CdaTrusteeAddressFragment.newInstance(9));
        fragments.add(E14CdaBankFragment.newInstance(10));
        fragments.add(E15MotherDeclarationSelectionListFragment.newInstance(11));
        fragments.add(E16MotherDeclarationDataEntryFragment.newInstance(12));
        fragments.add(E17MotherDeclarationSummaryFragment.newInstance(13));

        return fragments;
    }

    private List<Fragment> getNewPrePopulatesWizardPages(EnrolmentForm enrolmentForm) {
        List<Fragment> fragments = new ArrayList<Fragment>();

        ChildRegistrationType regType = enrolmentForm.getChildRegistration().
                getRegistrationType();

        fragments.add(E01FatherParticularsFragment.newInstance(0));
        fragments.add(E02MotherParticularsFragment.newInstance(1));

        if (regType == ChildRegistrationType.CITIZENSHIP) {
            fragments.add(E05CitizenshipRegistrationFragment.newInstance(2));
        } else if(regType == ChildRegistrationType.ADOPTION) {
            fragments.add(E06AdoptionRegistrationFragment.newInstance(2));
        } else { //ChildRegistrationType.POST_BIRTH
            fragments.add(E04PostBirthRegistrationFragment.newInstance(2));
        }

        fragments.add(E07NaHolderTypeSelectionFragment.newInstance(3));
        fragments.add(E08NaHolderParticularsFragment.newInstance(4));
        fragments.add(E09NaHolderAddressFragment.newInstance(5));
        fragments.add(E10CashGiftBankFragment.newInstance(6));
        fragments.add(E11CdaTrusteeTypeSelectionFragment.newInstance(7));
        fragments.add(E12CdaTrusteeParticularsFragment.newInstance(8));
        fragments.add(E13CdaTrusteeAddressFragment.newInstance(9));
        fragments.add(E14CdaBankFragment.newInstance(10));
        fragments.add(E15MotherDeclarationSelectionListFragment.newInstance(11));
        fragments.add(E16MotherDeclarationDataEntryFragment.newInstance(12));
        fragments.add(E17MotherDeclarationSummaryFragment.newInstance(13));

        return fragments;
    }

    private List<Fragment> getNewAppWizardPages() {
        List<Fragment> fragments = new ArrayList<Fragment>();

        fragments.add(E01FatherParticularsFragment.newInstance(0));
        fragments.add(E02MotherParticularsFragment.newInstance(1));
        fragments.add(E03PreBirthRegistrationFragment.newInstance(2));
        fragments.add(E04PostBirthRegistrationFragment.newInstance(3));
        fragments.add(E05CitizenshipRegistrationFragment.newInstance(4));
        fragments.add(E07NaHolderTypeSelectionFragment.newInstance(5));
        fragments.add(E08NaHolderParticularsFragment.newInstance(6));
        fragments.add(E09NaHolderAddressFragment.newInstance(7));
        fragments.add(E10CashGiftBankFragment.newInstance(8));
        fragments.add(E11CdaTrusteeTypeSelectionFragment.newInstance(9));
        fragments.add(E12CdaTrusteeParticularsFragment.newInstance(10));
        fragments.add(E13CdaTrusteeAddressFragment.newInstance(11));
        fragments.add(E14CdaBankFragment.newInstance(12));
        fragments.add(E15MotherDeclarationSelectionListFragment.newInstance(13));
        fragments.add(E16MotherDeclarationDataEntryFragment.newInstance(14));
        fragments.add(E17MotherDeclarationSummaryFragment.newInstance(15));

        return fragments;
    }

    public FragmentContainerActivityHelper getFragmentContainerActivityHelper() {
        return helper;
    }

    public void initializeViewPagerAndAdapter() {
        pagerAdapter = new FragmentWizardPagerAdapter(getSupportFragmentManager(),
                getWizardPages());

        helper = new FragmentContainerActivityHelper(context, pagerAdapter);

        viewPager = (CustomViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPagingEnabled(AppConstants.APP_IS_PAGING_ENABLED);

        viewPager.setOnPageChangeListener(helper.pageChangeListener);
    }

    //--- TITLE AND INSTRUCTIONS -------------------------------------------------------------------

    public void setFragmentTitle(View rootView) {
        helper.setFragmentTitle(rootView, R.string.title_activity_enrolment_main);
    }

    public void setInstructions(View rootView, int currentPageNo, int descriptionStringId,
                                boolean isMandatoryMessageRequired, String errorMessage) {
        EnrolmentForm enrolmentForm = app.getEnrolmentForm();

        if (enrolmentForm.getAppType() == EnrolmentAppType.NEW) {
            currentPageNo--;
        }

        helper.setFragmentInstructions(rootView, currentPageNo, 0, descriptionStringId,
                isMandatoryMessageRequired, getWizardPageCount(), errorMessage);
    }

    //--- SET BUTTONS ------------------------------------------------------------------------------

    public void setBackButtonClick(View rootView, int buttonId, final int currentPosition,
                                   final boolean isValidationRequired) {
        final FragmentWizard current = (FragmentWizard) pagerAdapter.getItem(currentPosition);

        backButton = (ImageView) rootView.findViewById(buttonId);
        //backButton.setVisibility(currentPosition == 0 ? View.INVISIBLE : View.VISIBLE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!current.onPauseFragment(isValidationRequired)) {
                    if(currentPosition > 0) {
                        jumpToPageWithIndex(currentPosition - 1);
                    } else {
                        createOkToBackFromFirstPageMessageBox();
                    }
                }
            }
        });
    }

    public void setNextButtonClick(View rootView, int buttonId, final int currentPosition,
                                   final boolean isValidationRequired) {
        final FragmentWizard current = (FragmentWizard) pagerAdapter.getItem(currentPosition);

        nextButton = (Button) rootView.findViewById(buttonId);
        nextButton.setText(R.string.btn_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!current.onPauseFragment(isValidationRequired)) {
                    jumpToPageWithIndex(currentPosition + 1);
                }
            }
        });
    }

    public void setSaveAsDraftButtonClick(View rootView, int buttonId) {
        saveAsDraftButton = (Button) rootView.findViewById(buttonId);
        saveAsDraftButton.setText(R.string.btn_save_as_draft);
        saveAsDraftButton.setVisibility(appType == EnrolmentAppType.VIEW ? View.GONE : View.VISIBLE);
        saveAsDraftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void setCancelButtonClick(View rootView, int buttonId) {
        cancelButton = (Button) rootView.findViewById(buttonId);
        cancelButton.setText(R.string.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOkToCancelMessageBox();
            }
        });
    }

    //--- GET BUTTONS ------------------------------------------------------------------------------

    public ImageView getBackButton() {
        return backButton;
    }

    public Button getNextButton() {
        return nextButton;
    }

    public Button getSaveAsDraftButton() {
        return saveAsDraftButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    //--- HELPERS ----------------------------------------------------------------------------------

    private void createOkToCancelMessageBox() {
        Log.i(getClass().getName(), "----------createOkToCancelMessageBox()");

        helper.createOkToCancelMessageBox(new MessageBoxButtonClickListener() {
            @Override
            public void onClickPositiveButton(DialogInterface dialog, int id) {
                Log.i(getClass().getName(), "----------onClickPositiveButton()");
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }

            @Override
            public void onClickNegativeButton(DialogInterface dialog, int id) {
                Log.i(getClass().getName(), "----------onClickNegativeButton()");
            }
        });
    }

    private void createOkToBackFromFirstPageMessageBox() {
        Log.i(getClass().getName(), "----------createOkToBackFromFirstPageMessageBox()");

        MessageBox.show(context,
                StringHelper.getStringByResourceId(context, R.string.alert_do_you_want_to_cancel),
                true, true, R.string.btn_ok, true, R.string.btn_cancel,
                new MessageBoxButtonClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        Log.i(getClass().getName(), "----------onClickPositiveButton()");

                        startActivity(new Intent(context, MainActivity.class));
                    }

                    @Override
                    public void onClickNegativeButton(DialogInterface dialog, int id) {
                        Log.i(getClass().getName(), "----------onClickNegativeButton()");
                        dialog.dismiss();
                    }
                });
    }

    public void displayBanks(ArrayAdapter<Bank> adapter){
        helper.displayBanks(adapter);
    }

    public void displayGenericData(MasterDataType type, ArrayAdapter<GenericDataItem> adapter){
        helper.displayGenericData(type, adapter);
    }

    //--- ASYNC TASKS ------------------------------------------------------------------------------

    private class GetPrePopulatedEnrolmentDataTask extends AsyncTask<Void, Void, EnrolmentForm> {

        private ProgressDialog dialog;
        EnrolmentAppType appType;

        public GetPrePopulatedEnrolmentDataTask(EnrolmentAppType appType) {
            this.dialog = new ProgressDialog(context);
            this.appType = appType;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading enrolment details");
            dialog.show();
        }

        @Override
        protected EnrolmentForm doInBackground(Void... params) {
            EnrolmentForm enrolmentForm = app.getEnrolmentForm();
            if (enrolmentForm != null && enrolmentForm.isPrePopulated()) {
                enrolmentForm = ProxyFactory.getEnrolmentProxy().getPrePopulatedApplication(appType);
            }
            return enrolmentForm;
        }

        protected void onPostExecute(EnrolmentForm enrolmentForm) {
            app.setEnrolmentForm(enrolmentForm);

            initializeViewPagerAndAdapter();

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private class GetEditViewEnrolmentDataTask extends AsyncTask<Void, Void, EnrolmentForm> {

        private ProgressDialog dialog;
        private EnrolmentAppType appType;

        public GetEditViewEnrolmentDataTask(EnrolmentAppType appType) {
            this.dialog = new ProgressDialog(context);
            this.appType = appType;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading enrolment details");
            dialog.show();
        }

        @Override
        protected EnrolmentForm doInBackground(Void... params) {
            return ProxyFactory.getEnrolmentProxy().getSavedApplication(appType);
        }

        protected void onPostExecute(EnrolmentForm enrolmentForm) {
            app.setEnrolmentForm(enrolmentForm);

            initializeViewPagerAndAdapter();

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
}
