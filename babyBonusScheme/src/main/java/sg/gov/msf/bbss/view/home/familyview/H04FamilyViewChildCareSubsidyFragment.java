package sg.gov.msf.bbss.view.home.familyview;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyEditType;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMeta;
import sg.gov.msf.bbss.apputils.meta.ModelPropertyViewMetaList;
import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.adapter.familyview.FamilyViewChildCareSubsidyArrayAdapter;
import sg.gov.msf.bbss.model.entity.childdata.ChildStatement;
import sg.gov.msf.bbss.model.entity.common.ChildCareSubsidy;
import sg.gov.msf.bbss.model.entity.people.Child;

/**
 * Created by bandaray
 * Modifed by chuanhe to add the Loading bar
 */
public class H04FamilyViewChildCareSubsidyFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;
    private final static int ITEM_SIZE = 3;

    private static Class CHILD_CLASS = Child.class;

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
    public static H04FamilyViewChildCareSubsidyFragment newInstance(int index) {
        H04FamilyViewChildCareSubsidyFragment fragment = new H04FamilyViewChildCareSubsidyFragment();
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

        displayData();
        setButtonClicks();

        return rootView;
    }

    //--- WIZARD NAVIGATION ------------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        return false;
    }

    @Override
    public boolean onResumeFragment() {
        return false;
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {
        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, true);
        fragmentContainer.setBottomButtonClick(listFooterView, R.id.btnFirstInOne, CURRENT_POSITION, true);
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    public void displayData() {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        ChildStatement childStmt = app.getChildStatement();

        //---Listview - Header
        View listHeaderView = inflater.inflate(
                R.layout.fragment_family_view_child_care_sub_header, null);
        if (listView.getHeaderViewsCount() == 0) {
            listView.addHeaderView(listHeaderView);
        }
        displayHeaderAndTotalSections(listHeaderView);
        setChildSectionVisibility(listHeaderView, childStmt.isChildDataDisplayed());

        //---Listview - Footer
        listFooterView = inflater.inflate(
                R.layout.clickable_1buttons, null);
        if (listView.getFooterViewsCount() == 0) {
            listView.addFooterView(listFooterView);
        }

        //---Listview - Pull-to-refresh and sorted date list
        TextView tvHeader = (TextView) listHeaderView.findViewById(
                R.id.family_view_ccs_list_view_header);

        tvHeader.setText(R.string.title_activity_family_view_child_care_subsidies);

        if (childStmt.getChildCareSubsidies().size() == 0) {
            tvHeader.setVisibility(View.GONE);
            listView.setAdapter(null);
        } else {
            pullToRefreshAndDateSorting();

            adapter = new FamilyViewChildCareSubsidyArrayAdapter(context, childCareSubsidyList);

            tvHeader.setVisibility(View.VISIBLE);
            listView.setAdapter(adapter);
            listView.setOnScrollListener(new ChildCareSubsidyListScrollListener());
        }

        //---Screen - Title and Instructions
        fragmentContainer.setFragmentTitle(rootView,
                R.string.title_activity_family_view_child_care_subsidies);
        fragmentContainer.setInstructions(listHeaderView, CURRENT_POSITION, 0, false);

        //---Child
        ModelViewSynchronizer<Child> childModelViewSynchronizer =
                new ModelViewSynchronizer<Child>(CHILD_CLASS,
                        getChildMetadata(), listHeaderView, AppConstants.EMPTY_STRING);
        childModelViewSynchronizer.setLabels();
        childModelViewSynchronizer.setHeaderTitle(R.id.section_child_particulars,
                R.string.label_child);
        childModelViewSynchronizer.displayDataObject(childStmt.getChildItem().getChild());
    }

    private void displayHeaderAndTotalSections(View listHeaderView) {
        TextView tvHeaderTotal = (TextView) listHeaderView.findViewById(
                R.id.family_view_ccs_total_header);

        tvHeaderTotal.setText(R.string.title_activity_family_view_child_care_sub_total);

        LinearLayout llHeaderTotalAmount = (LinearLayout) listHeaderView.findViewById(
                R.id.family_view_ccs_total);

        TextView tvTotalLabel = (TextView) llHeaderTotalAmount.findViewById(R.id.tvLabel);
        TextView tvTotalDollar = (TextView) llHeaderTotalAmount.findViewById(R.id.tvDollar);
        TextView tvTotalValue = (TextView) llHeaderTotalAmount.findViewById(R.id.tvValue);

        tvTotalLabel.setText(R.string.label_child_care_subsidy_amount);
        tvTotalDollar.setText(AppConstants.SYMBOL_DOLLAR);
        tvTotalValue.setText(StringHelper.formatCurrencyNumber(Double.toString(
                app.getChildStatement().getChildCareSubsidyAmt())));
    }

    private void pullToRefreshAndDateSorting() {
        childCareSubsidyListTemp = new ArrayList<ChildCareSubsidy>();
        childCareSubsidyList = new ArrayList<ChildCareSubsidy>();

        childCareSubsidyListTemp = app.getChildStatement().getChildCareSubsidies();

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
    }

    //--- META DATA --------------------------------------------------------------------------------

    private ModelPropertyViewMetaList getChildMetadata() {
        ModelPropertyViewMetaList metaDataList = new ModelPropertyViewMetaList(getActivity());
        ModelPropertyViewMeta viewMeta = null;

        try {
            //---BIRTH CERTIFICATE NO
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_BIRTH_CERT_NO);
            viewMeta.setIncludeTagId(R.id.family_view_child_birth_cert);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(CHILD_CLASS, viewMeta);

            //---NAME
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_NAME);
            viewMeta.setIncludeTagId(R.id.family_view_child_name);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.TEXT);

            metaDataList.add(CHILD_CLASS, viewMeta);

            //---BIRTHDAY
            viewMeta = new ModelPropertyViewMeta(Child.FIELD_BIRTHDAY);
            viewMeta.setIncludeTagId(R.id.family_view_child_birthday);
            viewMeta.setMandatory(false);
            viewMeta.setEditable(false);
            viewMeta.setEditType(ModelPropertyEditType.DATE);

            metaDataList.add(CHILD_CLASS, viewMeta);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metaDataList;
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

    //--- HELPERS ----------------------------------------------------------------------------------

    private void setChildSectionVisibility(View view, boolean isAlreadyDisplayed) {
        int visible = isAlreadyDisplayed ? View.GONE : View.VISIBLE;

        (view.findViewById(R.id.section_child_particulars)).setVisibility(visible);
        (view.findViewById(R.id.family_view_child_birth_cert)).setVisibility(visible);
        (view.findViewById(R.id.family_view_child_name)).setVisibility(visible);
        (view.findViewById(R.id.family_view_child_birthday)).setVisibility(visible);

        if (!isAlreadyDisplayed) {
            app.getChildStatement().setChildDataDisplayed(true);
        }
    }

}
