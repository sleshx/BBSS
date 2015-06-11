package sg.gov.msf.bbss.view.enrolment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.view.enrolment.util.MotherDeclarationDataEntryUtils;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class E16MotherDeclarationDataEntryFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;

    private View rootView;
    private EnrolmentFragmentContainerActivity fragmentContainer;
    private MotherDeclarationDataEntryUtils motherDeclarationDataEntryUtils;
    private boolean isLoaded;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static E16MotherDeclarationDataEntryFragment newInstance(int index) {
        E16MotherDeclarationDataEntryFragment fragment = new E16MotherDeclarationDataEntryFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        motherDeclarationDataEntryUtils = new MotherDeclarationDataEntryUtils(getActivity());
        rootView = motherDeclarationDataEntryUtils.onCreateView();

        fragmentContainer = ((EnrolmentFragmentContainerActivity) getActivity());
        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        if(!isLoaded) {
            isLoaded = true;
            return motherDeclarationDataEntryUtils.onPauseFragment(isValidationRequired, CURRENT_POSITION);
        }
        return false;
    }

    @Override
    public boolean onResumeFragment() {
        displayData();
        setButtonClicks();

        return false;
    }

    //--- ACTIVITY NAVIGATION ----------------------------------------------------------------------

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        motherDeclarationDataEntryUtils.onActivityResult(requestCode, resultCode, data);
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {
        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, false);
        fragmentContainer.setNextButtonClick(rootView, R.id.btnFirstInThree, CURRENT_POSITION, true);
        fragmentContainer.setSaveAsDraftButtonClick(rootView, R.id.btnSecondInThree);
        fragmentContainer.setCancelButtonClick(rootView, R.id.btnThirdInThree);

        motherDeclarationDataEntryUtils.setBrowseDocButtonClick();
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    private void displayData() {
        motherDeclarationDataEntryUtils.displayData(fragmentContainer.getFragmentContainerActivityHelper());

        //Screen - Title and Instructions
        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(rootView, CURRENT_POSITION, 0, true,
                motherDeclarationDataEntryUtils.displayValidationErrors(CURRENT_POSITION));
    }
}
