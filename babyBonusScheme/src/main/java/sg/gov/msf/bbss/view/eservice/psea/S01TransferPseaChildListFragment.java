package sg.gov.msf.bbss.view.eservice.psea;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.adapter.SelectChildArrayAdapter;
import sg.gov.msf.bbss.logic.masterdata.MasterDataListener;
import sg.gov.msf.bbss.logic.server.task.ServicesGetChildListItemsTask;
import sg.gov.msf.bbss.logic.type.ChildListType;
import sg.gov.msf.bbss.logic.type.ServiceAppType;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceTransferToPsea;
import sg.gov.msf.bbss.view.eservice.ServicesHelper;
import sg.gov.msf.bbss.view.eservice.ServicesHomeActivity;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class S01TransferPseaChildListFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;

    private Context context;
    private BbssApplication app;
    private View rootView;

    private ListView listView;
    private ImageView backButton;
    private Button nextButton;
    private Button cancelButton;
    private View listHeaderView;

    private SelectChildArrayAdapter adapter;
    private List<ChildItem> childItems;
    private TransferPseaFragmentContainerActivity fragmentContainer;
    private ServicesHelper servicesHelper;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static S01TransferPseaChildListFragment newInstance(int index) {
        S01TransferPseaChildListFragment fragment = new S01TransferPseaChildListFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Log.i(getClass().getName() , "----------onCreateView()");

        rootView = inflater.inflate(R.layout.layout_listview, null);

        context = getActivity();
        app = (BbssApplication) getActivity().getApplication();
        fragmentContainer = ((TransferPseaFragmentContainerActivity) getActivity());
        servicesHelper = new ServicesHelper(context);

        listView = (ListView) rootView.findViewById(R.id.lvMain);
        childItems = new ArrayList<ChildItem>();
        adapter = new SelectChildArrayAdapter(context, childItems, ChildListType.CDA_TO_PSEA);

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        displayData();
        setButtonClicks();

        executeGetChildListAsyncTask();

        return rootView;
    }

    //--- FRAGMENT NAVIGATION ----------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        Log.i(getClass().getName() , "----------onPauseFragment()");

        if (isValidationRequired) {
            if (listView.getCheckedItemCount() <= 0) {
                servicesHelper.createNoChildSelectedMessageBox();
                return true;
            }
        }
        setSelectedChildList();
        return false;
    }

    @Override
    public boolean onResumeFragment() {
        Log.i(getClass().getName() , "----------onResumeFragment()");

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

        fragmentContainer.setNextButtonClick(rootView, R.id.btnFirstInTwo, CURRENT_POSITION, true);
        nextButton = fragmentContainer.getNextButton();
        nextButton.setVisibility(View.GONE);

        fragmentContainer.setCancelButtonClick(rootView, R.id.btnSecondInTwo);
        cancelButton = fragmentContainer.getCancelButton();
        cancelButton.setVisibility(View.GONE);
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    private void displayData() {
        Log.i(getClass().getName() , "----------displayData()");

        LayoutInflater inflater = getActivity().getLayoutInflater();

        //Listview - Header
        listHeaderView = inflater.inflate(
                R.layout.section_header_row_and_instruction_row, null);
        listView.addHeaderView(listHeaderView);

        //Listview - Footer
        LinearLayout listFooterView = (LinearLayout)inflater.inflate(
                R.layout.clickable_2buttons, null);
        listView.addFooterView(listFooterView);

        //Listview - Populate Data
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);
        TextView tvHeader = (TextView) listHeaderView.findViewById(R.id.section_header);
        tvHeader.setText(R.string.label_child);

        //Screen - Title and Instructions
        fragmentContainer.setFragmentTitle(rootView);
        fragmentContainer.setInstructions(listHeaderView, CURRENT_POSITION,
                R.string.desc_services_transfer_psea_instruction_desc, false,
                AppConstants.EMPTY_STRING);
    }

    private void setSelectedChildList() {
        Log.i(getClass().getName() , "----------setSelectedChildList()");

        ArrayList<ChildItem> selectedChildList = new ArrayList<ChildItem>();
        ArrayList<String> selectedChildIdList = new ArrayList<String>();

        ServiceTransferToPsea changeNah = app.getServiceTransferToPsea();
        if (changeNah != null) {
            changeNah.setChildItems(selectedChildList);
            changeNah.setChildIds(selectedChildIdList);
        }

        final SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
        int checkedItemsCount = sparseBooleanArray.size();

        for (int i = 0; i < checkedItemsCount; ++i) {
            if(sparseBooleanArray.valueAt(i)) {
                ChildItem childItem = adapter.getItem(sparseBooleanArray.keyAt(i) - 1);
                selectedChildList.add(childItem);
                selectedChildIdList.add(childItem.getChild().getId());
            }
        }

        app.setServiceTransferToPsea(new ServiceTransferToPsea(selectedChildList, selectedChildIdList));
    }

    //--- ASYNC TASK -------------------------------------------------------------------------------

    private void executeGetChildListAsyncTask() {
        Log.i(getClass().getName() , "----------executeGetChildListAsyncTask()");

        ServicesGetChildListItemsTask asyncTask = new ServicesGetChildListItemsTask(
                context, ServiceAppType.CDA_TO_PSEA,
                new MasterDataListener<ChildItem[]>() {
                    @Override
                    public void onMasterData(ChildItem[] items) {
                        if (items.length <= 0) {
                            servicesHelper.createNoChildToDisplayMessageBox();
                        } else {
                            Collections.addAll(childItems, items);
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
