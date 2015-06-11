package sg.gov.msf.bbss.view.enrolment.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;

/**
 * Created by bandaray on 10/5/2015.
 */
public class E00EnrolmentMainFragment  extends Fragment implements FragmentWizard  {

    private static int CURRENT_POSITION;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private EnrolmentFragmentContainerActivity fragmentContainer;
    private boolean isPermissionGranted = true;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static E00EnrolmentMainFragment newInstance(int index) {
        E00EnrolmentMainFragment fragment = new E00EnrolmentMainFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_enrolment_main, null);

        context = getActivity();
        app = (BbssApplication) getActivity().getApplication();
        fragmentContainer = ((EnrolmentFragmentContainerActivity) getActivity());

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        displayData();
        setButtonClicks();

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        return false;
    }

    @Override
    public boolean onResumeFragment() {
        return false;
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    public void displayData() {
        //Screen Title
        ((TextView) rootView.findViewById(R.id.tvPageTitle))
                .setText(StringHelper.getStringByResourceId(context,
                        R.string.title_activity_enrolment_main));

        //Screen Instructions
        LinearLayout headerLayout = (LinearLayout) rootView.findViewById(R.id.screen_instructions);

        ((TextView) headerLayout.findViewById(R.id.tvInstructionTitle)).setText(
                R.string.label_enrolment_main_instruction);

        ((WebView) headerLayout.findViewById(R.id.wvInstructionDesc)).setVisibility(View.GONE);
        ((WebView) headerLayout.findViewById(R.id.wvErrorDesc)).setVisibility(View.GONE);
        ((TextView) headerLayout.findViewById(R.id.tvInstructionMandatory)).setVisibility(View.GONE);
        ((TextView) headerLayout.findViewById(R.id.tvInstructionStepNo)).setVisibility(View.GONE);

        //Screen Header
        TextView tv = (TextView) rootView.findViewById(R.id.enrolment_main_screen_header);
        ((TextView) rootView.findViewById(R.id.enrolment_main_screen_header))
                .setText(StringHelper.getStringByResourceId(context,
                        R.string.label_enrolment_main_header));

        //Set Description
        ((WebView) rootView.findViewById(R.id.wvScreenDescription)).loadData(
                StringHelper.getJustifiedString(context,
                        R.string.label_enrolment_main_description,
                        R.color.theme_gray_default_bg), "text/html", "utf-8");
    }

    private void setButtonClicks() {
        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, false);
        setApplyNowButtonClicks();
        fragmentContainer.setSaveAsDraftButtonClick(rootView, R.id.btnSecondInThree);
        fragmentContainer.setCancelButtonClick(rootView, R.id.btnThirdInThree);

        fragmentContainer.getCancelButton().setVisibility(View.GONE);
        fragmentContainer.getSaveAsDraftButton().setVisibility(View.GONE);
    }

    private void setApplyNowButtonClicks() {
        Button enrolButton = (Button) rootView.findViewById(R.id.btnFirstInThree);
        enrolButton.setText(R.string.btn_apply_now);
        enrolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPermissionGranted) {
                    fragmentContainer.jumpToPageWithIndex(CURRENT_POSITION + 1);
                } else {
                    Toast.makeText(context, "Sorry, please login using SingPass", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
