package sg.gov.msf.bbss.view.enrolment.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.view.enrolment.util.MotherDeclarationListUtils;

/**
 * Created by bandaray
 */
public class E15MotherDeclarationSelectionListFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;

    private View rootView;
    private EnrolmentFragmentContainerActivity fragmentContainer;
    private MotherDeclarationListUtils motherDeclarationListUtils;


    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static E15MotherDeclarationSelectionListFragment newInstance(int index) {
        E15MotherDeclarationSelectionListFragment fragment =
                new E15MotherDeclarationSelectionListFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        motherDeclarationListUtils = new MotherDeclarationListUtils(getActivity());
        rootView = motherDeclarationListUtils.onCreateView();

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);
        fragmentContainer = ((EnrolmentFragmentContainerActivity) getActivity());

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        return motherDeclarationListUtils.onPauseFragment(isValidationRequired);
    }

    @Override
    public boolean onResumeFragment() {
        displayData();
        setButtonClicks();

        return false;
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {
        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, false);
        fragmentContainer.setNextButtonClick(rootView, R.id.btnFirstInThree, CURRENT_POSITION, true);
        fragmentContainer.setSaveAsDraftButtonClick(rootView, R.id.btnSecondInThree);
        fragmentContainer.setCancelButtonClick(rootView, R.id.btnThirdInThree);
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    private void displayData() {
        motherDeclarationListUtils.displayData();

        //Screen - Title and Instructions
        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(rootView, CURRENT_POSITION,
                R.string.label_enrolment_fill_section_below_mother_dec,
                false, AppConstants.EMPTY_STRING);
    }

}
