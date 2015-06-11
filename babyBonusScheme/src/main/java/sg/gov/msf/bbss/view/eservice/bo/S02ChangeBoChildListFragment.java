package sg.gov.msf.bbss.view.eservice.bo;

import android.content.Context;
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
import java.util.Collections;
import java.util.List;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.connect.ConnectionHelper;
import sg.gov.msf.bbss.apputils.validation.ValidationInfo;
import sg.gov.msf.bbss.apputils.validation.ValidationMessage;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.adapter.SelectChildArrayAdapter;
import sg.gov.msf.bbss.logic.masterdata.MasterDataListener;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.server.task.ServicesGetChildListItemsTask;
import sg.gov.msf.bbss.logic.type.ChildListType;
import sg.gov.msf.bbss.logic.type.ServiceAppType;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceChangeBo;
import sg.gov.msf.bbss.view.eservice.ServicesHelper;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class S02ChangeBoChildListFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private ListView listView;
    private Button nextButton;
    private Button cancelButton;

    private List<ChildItem> childItems;
    private SelectChildArrayAdapter adapter;
    private ChangeBoFragmentContainerActivity fragmentContainer;
    private ServicesHelper servicesHelper;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static S02ChangeBoChildListFragment newInstance(int index) {
        S02ChangeBoChildListFragment fragment = new S02ChangeBoChildListFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.layout_listview, container, false);

        context = getActivity();
        app = (BbssApplication) getActivity().getApplication();
        fragmentContainer = ((ChangeBoFragmentContainerActivity) getActivity());
        servicesHelper = new ServicesHelper(context);

        listView = (ListView)rootView.findViewById(R.id.lvMain);
        childItems = new ArrayList<ChildItem>();
        adapter = new SelectChildArrayAdapter(context, childItems, ChildListType.CHANGE_BO,app);

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        ServiceChangeBo updateBirthOrder = app.getServiceChangeBo();
        updateBirthOrder.clearClientPageValidations(CURRENT_POSITION);
        updateBirthOrder.addSectionPage(SerializedNames.SEC_CHILD_LIST_ROOT, CURRENT_POSITION);

        return false;
    }

    @Override
    public boolean onResumeFragment() {
        displayData(displayValidationErrors());
        setButtonClicks();
        executeGetChildListAsyncTask();

        return false;
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {
        Log.i(getClass().getName() , "----------setButtonClicks()");

        fragmentContainer.setNextButtonClick(rootView, R.id.btnFirstInTwo, 1, true);
        nextButton = fragmentContainer.getNextButton();
        nextButton.setVisibility(View.GONE);

        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, 1, false);

        fragmentContainer.setCancelButtonClick(rootView, R.id.btnSecondInTwo);
        cancelButton = fragmentContainer.getCancelButton();
        cancelButton.setVisibility(View.GONE);
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    private void displayData(String errorMessage) {
        Log.i(getClass().getName() , "----------displayData()");

        LayoutInflater inflater = getActivity().getLayoutInflater();

        //Listview - Header
        View listHeaderView = inflater.inflate(
                R.layout.section_header_row_and_instruction_row, null);
        if (listView.getHeaderViewsCount() == 0) {
            listView.addHeaderView(listHeaderView);
        }

        //Listview - Footer
        LinearLayout listFooterView = (LinearLayout)inflater.inflate(
                R.layout.clickable_2buttons, null);
        if (listView.getFooterViewsCount() == 0) {
            listView.addFooterView(listFooterView);
        }

        //Screen - Instructions and Title
        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(listHeaderView, CURRENT_POSITION,
                R.string.desc_services_change_bo_child_list_instruction_desc, true, errorMessage);

        //Listview - Populate Data
        listView.setAdapter(adapter);
        TextView textView = (TextView)rootView.findViewById(R.id.section_header);
        textView.setText(R.string.label_child);
    }

    private String displayValidationErrors() {
        ServiceChangeBo updateBirthOrder = app.getServiceChangeBo();
        List<ValidationMessage> errorMessageList;
        String errorMessage = AppConstants.EMPTY_STRING;

        if(updateBirthOrder.isDisplayValidationErrors()){
            ValidationInfo validationInfo = updateBirthOrder.getPageSectionValidations(
                    CURRENT_POSITION, SerializedNames.SEC_CHILD_LIST_ROOT);

            if(validationInfo.hasAnyValidationMessages()) {
                errorMessageList = validationInfo.getValidationMessages();

                for(ValidationMessage messageList : errorMessageList) {
                    errorMessage = errorMessage + messageList.getMessage() +
                            AppConstants.SYMBOL_BREAK_LINE;
                }
            }
        }

        return errorMessage;
    }

    //--- ASYNC TASK -------------------------------------------------------------------------------

    private void executeGetChildListAsyncTask() {
        Log.i(getClass().getName(), "----------executeGetChildListAsyncTask()");

        ServicesGetChildListItemsTask asyncTask = new ServicesGetChildListItemsTask(
                context, ServiceAppType.CHANGE_BO,
                new MasterDataListener<ChildItem[]>() {
                    @Override
                    public void onMasterData(ChildItem[] items) {
                        if (items.length <= 0) {
                            servicesHelper.createNoChildToDisplayMessageBox();
                        } else {
                            childItems.clear();
                            Collections.addAll(childItems, items);
                            app.getServiceChangeBo().setChildItems((ArrayList<ChildItem>) childItems);
                            adapter.notifyDataSetChanged();

                            if (adapter.getCount() > 0) {
                                nextButton.setVisibility(View.VISIBLE);
                                cancelButton.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

        if (ConnectionHelper.isNetworkConnected(context)) {
            asyncTask.execute();
        } else {
            servicesHelper.createDeviceOfflineMessageBox();
        }
    }


}
