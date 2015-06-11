package sg.gov.msf.bbss.view.enrolment.sub;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.view.enrolment.util.MotherDeclarationDataEntryUtils;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class M02MotherDeclarationDataEntryFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;

    private View rootView;
    private Context context;
    private BbssApplication app;

    private MotherDeclarationFragmentContainerActivity fragmentContainer;
    private MotherDeclarationDataEntryUtils motherDeclarationDataEntryUtils;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static M02MotherDeclarationDataEntryFragment newInstance(int index) {
        M02MotherDeclarationDataEntryFragment fragment = new M02MotherDeclarationDataEntryFragment();
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

        context = getActivity();
        app = (BbssApplication) getActivity().getApplication();

        fragmentContainer = ((MotherDeclarationFragmentContainerActivity) getActivity());
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

    //--- ACTIVITY NAVIGATION ----------------------------------------------------------------------

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        motherDeclarationDataEntryUtils.onActivityResult(requestCode, resultCode, data);
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {
        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, false);
        setSaveButtonClick(rootView, R.id.btnFirstInThree);
        fragmentContainer.setResetButtonClick(rootView, R.id.btnSecondInThree);
        fragmentContainer.setCancelButtonClick(rootView, R.id.btnThirdInThree);

        motherDeclarationDataEntryUtils.setBrowseDocButtonClick();
    }

    public void setSaveButtonClick(View rootView, int buttonId) {
        Button saveButton = (Button) rootView.findViewById(R.id.btnFirstInThree);
        saveButton.setText(StringHelper.getStringByResourceId(context, R.string.btn_save));
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                motherDeclarationDataEntryUtils.onPauseFragment(true, CURRENT_POSITION);
                fragmentContainer.cancelActivity();
            }
        });
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
