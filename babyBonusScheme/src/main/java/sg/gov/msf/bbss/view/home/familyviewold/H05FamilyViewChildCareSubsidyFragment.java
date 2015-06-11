package sg.gov.msf.bbss.view.home.familyviewold;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.adapter.familyview.FamilyViewChildCareSubsidyArrayAdapter;
import sg.gov.msf.bbss.model.entity.childdata.ChildStatement;
import sg.gov.msf.bbss.model.entity.common.ChildCareSubsidy;
import sg.gov.msf.bbss.view.MainActivity;

/**
 * Created by bandaray
 * Modifed by chuanhe to add the Loading bar
 */
public class H05FamilyViewChildCareSubsidyFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;
    private final static int ITEM_SIZE = 3;

    private View rootView;
    private Context context;
    private BbssApplication app;
    private FamilyViewFragmentContainerActivity fragmentContainer;

    private ListView listView;
    private View listFooterView;

    private FamilyViewChildCareSubsidyArrayAdapter adapter;
    private List<ChildCareSubsidy> childCareSubsidyListTemp;
    private List<ChildCareSubsidy> childCareSubsidyList;

    private int visibleLastIndex = 0;
    private int visibleItemCount;
    private int item = 0;
    private int size = 0;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static H05FamilyViewChildCareSubsidyFragment newInstance(int index) {
        H05FamilyViewChildCareSubsidyFragment fragment = new H05FamilyViewChildCareSubsidyFragment();
        Bundle args = new Bundle();
        args.putInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, index);
        fragment.setArguments(args);
        return fragment;
    }

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_listview, null);

        context = getActivity();
        app = (BbssApplication) getActivity().getApplication();
        fragmentContainer = ((FamilyViewFragmentContainerActivity) context);

        listView = (ListView) rootView.findViewById(R.id.lvMain);

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        return rootView;
    }

    //--- WIZARD NAVIGATION ------------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        return false;
    }

    @Override
    public boolean onResumeFragment() {
        displayData(getActivity().getLayoutInflater());
        setButtonClicks();

        return false;
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {
        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, true);

        Button cancelButton = (Button) listFooterView.findViewById(R.id.btnFirstInOne);
        cancelButton.setText(R.string.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MainActivity.class));
            }
        });
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    public void displayData(LayoutInflater inflater) {
        ChildStatement childStmt = app.getChildStatement();

        //Listview - Header
        View listHeaderView = inflater.inflate(
                R.layout.fragment_family_view_child_care_sub_header, null);
        if (listView.getHeaderViewsCount() == 0) {
            listView.addHeaderView(listHeaderView);
        }

        //Listview - Footer
        listFooterView = inflater.inflate(
                R.layout.clickable_1buttons, null);
        if (listView.getFooterViewsCount() == 0) {
            listView.addFooterView(listFooterView);
        }

        //Listview - Pull-to-refresh and sorted date list
        childCareSubsidyListTemp = new ArrayList<ChildCareSubsidy>();
        childCareSubsidyList = new ArrayList<ChildCareSubsidy>();

        childCareSubsidyListTemp = childStmt.getChildCareSubsidies();

        if(childCareSubsidyListTemp.size() <= ITEM_SIZE){
            for (int i = 0; i < childCareSubsidyListTemp.size(); i++){
                childCareSubsidyList.add(childCareSubsidyListTemp.get(i));
            }
        }else {
            for (int i = 0; i < ITEM_SIZE; i++){
                childCareSubsidyList.add(childCareSubsidyListTemp.get(i));
            }
        }

        item = 0;
        size = childCareSubsidyListTemp.size();

        adapter = new FamilyViewChildCareSubsidyArrayAdapter(context,
                childCareSubsidyList);

        listView.setAdapter(adapter);
        listView.setOnScrollListener(new ChildCareSubsidyListScrollListener());

        //Screen - List Header and Total Section
        TextView tvHeaderTotal = (TextView) listHeaderView.findViewById(
                R.id.family_view_ccs_total_header);
        TextView tvTotalLabel = (TextView) listHeaderView.findViewById(R.id.tvLabel);
        TextView tvTotalDollar = (TextView) listHeaderView.findViewById(R.id.tvDollar);
        TextView tvTotalValue = (TextView) listHeaderView.findViewById(R.id.tvValue);
        TextView tvHeader = (TextView) listHeaderView.findViewById(
                R.id.family_view_ccs_list_view_header);

        tvHeaderTotal.setText(R.string.title_activity_family_view_child_care_sub_total);
        tvTotalLabel.setText(R.string.label_child_care_subsidy_amount);
        tvTotalDollar.setText(AppConstants.SYMBOL_DOLLAR);
        tvTotalValue.setText(StringHelper.formatCurrencyNumber(Double.toString(
                childStmt.getChildCareSubsidyAmt())));
        tvHeader.setText(R.string.title_activity_family_view_child_care_subsidies);

        //Screen - Title and Instructions
        fragmentContainer.setFragmentTitle(rootView,
                R.string.title_activity_family_view_child_care_subsidies);
        fragmentContainer.setInstructions(listHeaderView, CURRENT_POSITION, 0, false);
    }

    //--- LISTENERS --------------------------------------------------------------------------------

    public final class ChildCareSubsidyListScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            int itemsLastIndex = adapter.getCount() - 1;
            int lastIndex = itemsLastIndex + 1 + 1;
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE &&
                    visibleLastIndex == lastIndex) {

                if(size <= ITEM_SIZE){
                    Toast.makeText(context,
                            StringHelper.getStringByResourceId(context, R.string.toast_no_more_data),
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (childCareSubsidyList.size() < size) {
                        Toast.makeText(context,
                                StringHelper.getStringByResourceId(context, R.string.toast_loading_data),
                                Toast.LENGTH_SHORT).show();
                        item++;
                        if ((size - item * ITEM_SIZE) >= ITEM_SIZE) {
                            for (int i = (ITEM_SIZE * item); i < ITEM_SIZE * (item + 1); i++) {
                                childCareSubsidyList.add(childCareSubsidyListTemp.get(i));
                            }
                        } else {
                            for (int i = (ITEM_SIZE * item); i < size; i++) {
                                childCareSubsidyList.add(childCareSubsidyListTemp.get(i));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(context,
                                StringHelper.getStringByResourceId(context, R.string.toast_no_more_data),
                                Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount2, int totalItemCount) {
            visibleItemCount = visibleItemCount2;
            visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
        }
    }

}
