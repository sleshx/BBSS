package sg.gov.msf.bbss.view.home.familyviewold;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import sg.gov.msf.bbss.apputils.ui.helper.CustomViewPager;
import sg.gov.msf.bbss.apputils.ui.helper.MessageBoxButtonClickListener;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizardPagerAdapter;
import sg.gov.msf.bbss.logic.FragmentContainerActivityHelper;
import sg.gov.msf.bbss.logic.server.ServerConnectionHelper;
import sg.gov.msf.bbss.logic.server.login.LoginManager;
import sg.gov.msf.bbss.logic.type.LoginUserType;
import sg.gov.msf.bbss.model.entity.childdata.ChildStatement;
import sg.gov.msf.bbss.view.MainActivity;

/**
 * Created by bandaray
 */
public class FamilyViewFragmentContainerActivity extends FragmentActivity {

    private Context context;
    private BbssApplication app;

    private CustomViewPager viewPager;
    private List<Fragment> fragments;
    private FragmentWizardPagerAdapter pagerAdapter;
    private FragmentContainerActivityHelper helper;

    private ImageView backButton;
    private Button nextButton;


    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_viewpager);

        context = this;
        app = (BbssApplication) getApplication();

        getActionBar().hide();

        pagerAdapter = new FragmentWizardPagerAdapter(getSupportFragmentManager(),
                getWizardPages());

        helper = new FragmentContainerActivityHelper(context, pagerAdapter);

        viewPager = (CustomViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPagingEnabled(true);

        viewPager.setOnPageChangeListener(helper.pageChangeListener);

        app.setChildStatement(new ChildStatement());
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

        fragments.add(H01FamilyViewChildListFragment.newInstance(0));
        fragments.add(H02FamilyViewCashGiftDetailsFragment.newInstance(1));
        fragments.add(H03FamilyViewChildDevAccDetailsFragment.newInstance(2));
        fragments.add(H04FamilyViewChildDevAccHistoryFragment.newInstance(3));
        fragments.add(H05FamilyViewChildCareSubsidyFragment.newInstance(4));

        return fragments;
    }

    public void jumpToPageWithIndex(int pageIndex) {
        viewPager.setCurrentItem(pageIndex, true);
    }

    public int getWizardPageCount() {
        return fragments.size();
    }

    //--- TITLE AND INSTRUCTIONS -------------------------------------------------------------------

    public void setFragmentTitle(View rootView, int titleStringId) {
        helper.setFragmentTitle(rootView, titleStringId);
    }

    public void setInstructions(View rootView, int currentPageNo, int descriptionStringId,
                                boolean isMandatoryMessageRequired) {
        helper.setFragmentInstructions(rootView, currentPageNo, 0, descriptionStringId,
                isMandatoryMessageRequired, getWizardPageCount(), AppConstants.EMPTY_STRING);
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
                    if (currentPosition == 0) {
                        startActivity(new Intent(context, MainActivity.class));
                    } else if (currentPosition == 2 && LoginManager.getSessionContainer()
                                                    .getLoginType() == LoginUserType.CDA_TRUSTEE ) {
                        jumpToPageWithIndex(currentPosition - 2);
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
                    if (currentPosition == 0 && LoginManager.getSessionContainer()
                                                    .getLoginType() == LoginUserType.CDA_TRUSTEE) {
                        jumpToPageWithIndex(currentPosition + 2);
                    } else {
                        jumpToPageWithIndex(currentPosition + 1);
                    }
                }
            }
        });
    }

    //--- GET BUTTONS ------------------------------------------------------------------------------

    public Button getNextButton() {
        return nextButton;
    }

    //--- DIALOG BOXES ------------------------------------------------------------------------------

    public void createDeviceOfflineMessageBox() {
        Log.i(getClass().getName(), "----------createOkToCancelMessageBox()");

        ServerConnectionHelper.createDeviceOfflineMessageBox(context,
                new MessageBoxButtonClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        startActivity(new Intent(context, MainActivity.class));
                    }

                    @Override
                    public void onClickNegativeButton(DialogInterface dialog, int id) {

                    }
                });
    }

}
