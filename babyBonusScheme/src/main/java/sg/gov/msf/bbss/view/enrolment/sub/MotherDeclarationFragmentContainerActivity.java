package sg.gov.msf.bbss.view.enrolment.sub;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.ui.helper.CustomViewPager;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizardPagerAdapter;
import sg.gov.msf.bbss.logic.FragmentContainerActivityHelper;
import sg.gov.msf.bbss.view.enrolment.util.MotherDeclarationDataEntryUtils;

/**
 * Created by bandaray
 */
public class MotherDeclarationFragmentContainerActivity extends FragmentActivity {

    private Context context;
    private CustomViewPager viewPager;
    private List<Fragment> fragments;
    private FragmentWizardPagerAdapter pagerAdapter;
    private FragmentContainerActivityHelper helper;

    private ImageView backButton;
    private Button nextButton;
    private Button resetButton;
    private Button cancelButton;
    private BbssApplication app;

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_viewpager);
        context = this;
        getActionBar().hide();

        app = (BbssApplication) getApplication();

        pagerAdapter = new FragmentWizardPagerAdapter(getSupportFragmentManager(),
                getWizardPages());

        helper = new FragmentContainerActivityHelper(context, pagerAdapter);

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

    private List<Fragment> getWizardPages() {
        fragments = new ArrayList<Fragment>();
        fragments.add(M01MotherDeclarationSelectionListFragment.newInstance(0));
        fragments.add(M02MotherDeclarationDataEntryFragment.newInstance(1));
        return fragments;
    }

    public void jumpToPageWithIndex(int pageIndex) {
        viewPager.setCurrentItem(pageIndex, true);
    }

    public int getWizardPageCount() {
        return fragments.size();
    }

    public FragmentContainerActivityHelper getFragmentContainerActivityHelper() {
        return helper;
    }

    public void cancelActivity() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    //--- TITLE AND INSTRUCTIONS -------------------------------------------------------------------

    public void setFragmentTitle(View rootView) {
        helper.setFragmentTitle(rootView, R.string.title_activity_enrolment_sub_mother_dec);
    }

    public void setInstructions(View rootView, int currentPageNo, int descriptionStringId,
                                boolean isMandatoryMessageRequired, String errorMessage) {
        helper.setFragmentInstructions(rootView, currentPageNo,
                R.string.label_enrolment_fill_section_below_mother_dec, descriptionStringId,
                isMandatoryMessageRequired, getWizardPageCount(), errorMessage);
    }

    //--- SET BUTTONS ------------------------------------------------------------------------------

    public void setBackButtonClick(View rootView, int buttonId, final int currentPosition,
                                   final boolean isValidationRequired) {
        final FragmentWizard current = (FragmentWizard) pagerAdapter.getItem(currentPosition);

        backButton = (ImageView) rootView.findViewById(buttonId);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setNextButtonClick(View rootView, int buttonId, final int currentPosition,
                                   final boolean isValidationRequired) {
        nextButton = (Button) rootView.findViewById(buttonId);
        nextButton.setText(R.string.btn_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToPageWithIndex(currentPosition + 1);
            }
        });
    }

    public void setResetButtonClick(View rootView, int buttonId) {
        resetButton = (Button) rootView.findViewById(buttonId);
        resetButton.setText(R.string.btn_reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
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
                finish();
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
