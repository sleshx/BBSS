package sg.gov.msf.bbss.view.eservice.bo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.view.eservice.ServicesHomeActivity;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class S01ChangeBoDescriptionFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private ImageView backButton;
    private ChangeBoFragmentContainerActivity fragmentContainer;

    private boolean isVisible = false;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static S01ChangeBoDescriptionFragment newInstance(int index) {
        S01ChangeBoDescriptionFragment fragment = new S01ChangeBoDescriptionFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(getClass().getName(), "----------onCreateView()");

        rootView = inflater.inflate(R.layout.fragment_service_change_bo_description,
                container, false);

        context = getActivity();
        app = (BbssApplication) getActivity().getApplication();
        fragmentContainer =(ChangeBoFragmentContainerActivity)getActivity();

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        displayData();
        setButtonClicks();

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        Log.i(getClass().getName(), "----------onPauseFragment()");

        return false;
    }

    @Override
    public boolean onResumeFragment() {
        Log.i(getClass().getName(), "----------onResumeFragment()");

        return false;
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setBackButtonClick() {
        Log.i(getClass().getName() , "----------setBackButtonClick()");

        backButton = (ImageView) rootView.findViewById(R.id.ivBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!onPauseFragment(false)) {
                    startActivity(new Intent(context, ServicesHomeActivity.class));
                }
            }
        });
    }

    private void setButtonClicks() {
        Log.i(getClass().getName() , "----------setButtonClicks()");

        setBackButtonClick();

        fragmentContainer.setNextButtonClick(rootView, R.id.btnFirstInTwo, 0, true);
        fragmentContainer.setCancelButtonClick(rootView, R.id.btnSecondInTwo);
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    private void displayData() {
        Log.i(getClass().getName(), "----------displayData()");

        //Screen -- Title and Instructions
        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(rootView, CURRENT_POSITION, 0, false,
                AppConstants.EMPTY_STRING);

        //Expandable Listview -- Title
        TextView textView1 = (TextView)rootView.findViewById(R.id.section_declaration);
        textView1.setText(R.string.desc_services_change_bo_child_list_instruction_desc_header);

        //Expandable Listview -- Items
        ((LinearLayout)rootView.findViewById(R.id.ll_two)).setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout llExample = (LinearLayout)rootView.findViewById(R.id.ll_example);
                if (!isVisible){
                    ((ImageView)rootView.findViewById(R.id.ivArrow)).
                            setBackgroundResource(android.R.drawable.arrow_up_float);
                    llExample.setVisibility(View.VISIBLE);
                    isVisible = true;
                }else {
                    ((ImageView)rootView.findViewById(R.id.ivArrow)).
                            setBackgroundResource(android.R.drawable.arrow_down_float);
                    llExample.setVisibility(View.GONE);
                    isVisible = false;
                }
            }
        });
    }
}
