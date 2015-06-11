package sg.gov.msf.bbss.view.home.familyview;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.adapter.familyview.FamilyViewCdaHistoryArrayAdapter;
import sg.gov.msf.bbss.model.entity.childdata.ChildStatement;
import sg.gov.msf.bbss.model.entity.common.ChildDevAccountHistory;

/**
 * Created by bandaray
 * Modifed by chuanhe to add the Loading bar
 */
public class H03FamilyViewChildDevAccHistoryFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;
    private final static int ITEM_SIZE = 3;

    private View rootView;
    private Context context;
    private BbssApplication app;
    private FamilyViewFragmentContainerActivity fragmentContainer;

    private ListView listView;
    private ChildStatement childStatement;

    private FamilyViewCdaHistoryArrayAdapter adapter;
    private List<ChildDevAccountHistory> cdaHistoryListUnsorted;
    private List<ChildDevAccountHistory> cdaHistoryListSorted;

    private int visibleLastIndex = 0;
    private int visibleItemCount;
    private int item = 0;
    private int size = 0;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static H03FamilyViewChildDevAccHistoryFragment newInstance(int index) {
        H03FamilyViewChildDevAccHistoryFragment fragment = new H03FamilyViewChildDevAccHistoryFragment();
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
        displayData();
        setButtonClicks();

        return false;
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {
        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, true);
        fragmentContainer.setBottomButtonClick(rootView, R.id.btnFirstInOne, CURRENT_POSITION, true);
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    public void displayData() {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        cdaHistoryListSorted = new ArrayList<ChildDevAccountHistory>();
        childStatement = app.getChildStatement();

        //Listview - Header
        View listHeaderView = inflater.inflate(
                R.layout.fragment_family_view_cda_history_header, null);
        if (listView.getHeaderViewsCount() == 0) {
            listView.addHeaderView(listHeaderView);
        }

        //Listview - Footer
        View listFooterView = inflater.inflate(
                R.layout.clickable_1buttons, null);
        if (listView.getFooterViewsCount() == 0) {
            listView.addFooterView(listFooterView);
        }

        //Listview - Pull-to-refresh and sorted date list
        cdaHistoryListUnsorted = getFilteredChildDevAccHistoryList();

        if(cdaHistoryListUnsorted.size() <= ITEM_SIZE){
            for (int i = 0;i< cdaHistoryListUnsorted.size();i++){
                cdaHistoryListSorted.add(cdaHistoryListUnsorted.get(i));
            }
        } else {
            for (int i = 0; i < ITEM_SIZE; i++){
                cdaHistoryListSorted.add(cdaHistoryListUnsorted.get(i));
            }
        }

        item = 0;
        size = cdaHistoryListUnsorted.size();

        adapter = new FamilyViewCdaHistoryArrayAdapter(context, cdaHistoryListSorted);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new CdaMatchingHistoryListScrollListener());

        //Screen - Header
        TextView tvHeader = (TextView) listHeaderView.findViewById(
                R.id.family_view_cdah_list_view_header);
        tvHeader.setText(R.string.label_cda_matching_history);

        //Screen - Title and Instructions
        fragmentContainer.setFragmentTitle(rootView,
                R.string.title_activity_family_view_cda_details);
        fragmentContainer.setInstructions(listHeaderView, CURRENT_POSITION, 0, false);
    }

    //--- HELPERS ----------------------------------------------------------------------------------

    private List<ChildDevAccountHistory> getFilteredChildDevAccHistoryList(){
        Date fromDate = childStatement.getFromDate();
        Date toDate = childStatement.getToDate();

        List<ChildDevAccountHistory> historyList = new ArrayList<ChildDevAccountHistory>();

        if (fromDate != null && toDate != null) {
            for (ChildDevAccountHistory history : childStatement.getChildDevAccountHistories()) {
                Date matchDate = history.getMatchedDate();

                if (matchDate.compareTo(fromDate) >= 0 && matchDate.compareTo(toDate) <= 0) {
                    historyList.add(history);
                }
            }
        } else if (fromDate == null && toDate == null) {
            for (ChildDevAccountHistory history : childStatement.getChildDevAccountHistories()) {
                historyList.add(history);
            }
        } else if (fromDate != null && toDate == null) {
            for (ChildDevAccountHistory childDevAccountHistory : childStatement.getChildDevAccountHistories()) {
                Date matchDate = childDevAccountHistory.getMatchedDate();

                if (matchDate.compareTo(fromDate) > 0) {
                    historyList.add(childDevAccountHistory);
                }
            }
        } else if (fromDate == null && toDate != null) {
            for (ChildDevAccountHistory history : childStatement.getChildDevAccountHistories()) {
                Date matchDate = history.getMatchedDate();

                if (matchDate.compareTo(toDate) < 0) {
                    historyList.add(history);
                }
            }
        }

        return historyList;
    }

    //--- LISTENERS --------------------------------------------------------------------------------

    public final class CdaMatchingHistoryListScrollListener implements AbsListView.OnScrollListener {

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
                    if (cdaHistoryListSorted.size() < size) {
                        Toast.makeText(context,
                                StringHelper.getStringByResourceId(context, R.string.toast_loading_data),
                                Toast.LENGTH_SHORT).show();
                        item++;
                        if ((size - item * ITEM_SIZE) >= ITEM_SIZE) {
                            for (int i = (ITEM_SIZE * item); i < ITEM_SIZE * (item + 1); i++) {
                                cdaHistoryListSorted.add(cdaHistoryListUnsorted.get(i));
                            }
                        } else {
                            for (int i = (ITEM_SIZE * item); i < size; i++) {
                                cdaHistoryListSorted.add(cdaHistoryListUnsorted.get(i));
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
