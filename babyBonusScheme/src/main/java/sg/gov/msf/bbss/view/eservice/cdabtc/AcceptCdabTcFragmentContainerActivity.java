package sg.gov.msf.bbss.view.eservice.cdabtc;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.ui.BbssActionBarActivity;
import sg.gov.msf.bbss.apputils.ui.component.MessageBox;
import sg.gov.msf.bbss.apputils.ui.helper.CustomViewPager;
import sg.gov.msf.bbss.apputils.ui.helper.MessageBoxButtonClickListener;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizardPagerAdapter;
import sg.gov.msf.bbss.logic.FragmentContainerActivityHelper;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceCdabTc;
import sg.gov.msf.bbss.view.eservice.ServicesHelper;
import sg.gov.msf.bbss.view.eservice.ServicesHomeActivity;
import sg.gov.msf.bbss.view.eservice.cdab.S01ChangeCdabChildListFragment;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class AcceptCdabTcFragmentContainerActivity extends BbssActionBarActivity {

    private Context context;
    private CustomViewPager viewPager;
    private List<Fragment> fragments;

    private BbssApplication app;
    private FragmentWizardPagerAdapter pagerAdapter;
    private FragmentContainerActivityHelper helper;
    private ServicesHelper servicesHelper;

    private ImageView backButton;
    private Button nextButton;
    private Button cancelButton;

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_viewpager);
        context = this;
        getActionBar().hide();

        app = (BbssApplication) getApplication();
        app.setServiceCdabTc(new ServiceCdabTc());

        pagerAdapter = new FragmentWizardPagerAdapter(getSupportFragmentManager(),
                getFamilyViewWizardPages());

        helper = new FragmentContainerActivityHelper(context, pagerAdapter);
        servicesHelper = new ServicesHelper(context);

        viewPager = (CustomViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPagingEnabled(AppConstants.APP_IS_PAGING_ENABLED);

        viewPager.setOnPageChangeListener(helper.pageChangeListener);
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

    private List<Fragment> getFamilyViewWizardPages() {
        fragments = new ArrayList<Fragment>();
        fragments.add(S01AcceptCdabTcFragment.newInstance(0));
        fragments.add(S02AcceptCdabDeclarationFragment.newInstance(1));
        return fragments;
    }

    public void jumpToPageWithIndex(int pageIndex) {
        viewPager.setCurrentItem(pageIndex, true);
    }

    public int getWizardPageCount() {
        return fragments.size();
    }

    //--- TITLE AND INSTRUCTIONS -------------------------------------------------------------------

    public void setFragmentTitle(View rootView) {
        helper.setFragmentTitle(rootView, R.string.title_activity_services_cdab_tc);
    }

    public void setInstructions(View rootView, int currentPageNo, int descriptionStringId,
                                boolean isMandatoryMessageRequired,String errorMessage) {
        helper.setFragmentInstructions(rootView, currentPageNo,
                R.string.desc_services_accept_cdab_tc_instruction_title, descriptionStringId,
                isMandatoryMessageRequired, getWizardPageCount(),errorMessage);
    }

    //--- SET BUTTONS ------------------------------------------------------------------------------

    public void setBackButtonClick(View rootView, int buttonId, final int currentPosition,
                                   final boolean isValidationRequired) {
        final FragmentWizard current = (FragmentWizard) pagerAdapter.getItem(currentPosition);

        backButton = (ImageView) rootView.findViewById(buttonId);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!current.onPauseFragment(isValidationRequired)) {

                    if (currentPosition == 4 && !app.getServiceCdabTc().isThirdParty()) {
                        jumpToPageWithIndex(currentPosition - 3);
                    } else {
                        jumpToPageWithIndex(currentPosition - 1);
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

                    if (currentPosition == 1 && !app.getServiceCdabTc().isThirdParty()) {
                        jumpToPageWithIndex(currentPosition + 3);
                    } else {
                        jumpToPageWithIndex(currentPosition + 1);
                    }
                }
            }
        });
    }

    public void setCancelButtonClick(View rootView, int buttonId) {
        cancelButton = (Button) rootView.findViewById(buttonId);
        cancelButton.setText(R.string.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                servicesHelper.createOkToCancelMessageBox();
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

    public Button getCancelButton() {
        return cancelButton;
    }

}
