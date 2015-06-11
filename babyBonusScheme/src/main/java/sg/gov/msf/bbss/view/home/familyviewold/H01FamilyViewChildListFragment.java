package sg.gov.msf.bbss.view.home.familyviewold;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.connect.ConnectionHelper;
import sg.gov.msf.bbss.apputils.ui.component.MessageBox;
import sg.gov.msf.bbss.apputils.ui.helper.MessageBoxButtonClickListener;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.wizard.FragmentWizard;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.adapter.SelectChildArrayAdapter;
import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.logic.type.ChildListType;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.entity.childdata.ChildStatement;

/**
 * Created by bandaray
 */
public class H01FamilyViewChildListFragment extends Fragment implements FragmentWizard {

    private static int CURRENT_POSITION;

    private View rootView;
    private Context context;
    private BbssApplication app;
    private FamilyViewFragmentContainerActivity fragmentContainer;

    private ListView listView;
    private Button nextButton;

    private SelectChildArrayAdapter adapter;
    private List<ChildItem> childItems;

    private boolean isRowSelected = false;
    private boolean isPreviouslyLoaded = false;

    /**
     * Static factory method that takes an current position, initializes the fragment's arguments,
     * and returns the new fragment to the FragmentActivity.
     */
    public static H01FamilyViewChildListFragment newInstance(int index) {
        H01FamilyViewChildListFragment fragment = new H01FamilyViewChildListFragment();
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

        childItems = new ArrayList<ChildItem>();
        adapter = new SelectChildArrayAdapter(context, childItems, ChildListType.FAMILY_VIEW, app);

        CURRENT_POSITION = getArguments().getInt(BabyBonusConstants.CURRENT_FRAGMENT_POSITION, -1);

        displayData(getActivity().getLayoutInflater());
        setButtonClicks();

        executeGetChildListAsyncTask();

        return rootView;
    }

    //--- WIZARD NAVIGATION ------------------------------------------------------------------------

    @Override
    public boolean onPauseFragment(boolean isValidationRequired) {
        if (isValidationRequired) {
            if (!isRowSelected) {
                createNoChildSelectedMessageBox();
                fragmentContainer.jumpToPageWithIndex(CURRENT_POSITION);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onResumeFragment() {
        return false;
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {
        fragmentContainer.setBackButtonClick(rootView, R.id.ivBackButton, CURRENT_POSITION, false);

        fragmentContainer.setNextButtonClick(rootView, R.id.btnFirstInOne, CURRENT_POSITION, true);
        nextButton = fragmentContainer.getNextButton();
        nextButton.setVisibility(View.GONE);
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    private void displayData(LayoutInflater inflater) {

        //Listview - Header
        View listHeader = inflater.inflate(R.layout.section_header_row_and_instruction_row, null);
        if (listView.getHeaderViewsCount() == 0) {
            listView.addHeaderView(listHeader);
        }

        //Listview - Footer
        View  listFooter = inflater.inflate(R.layout.clickable_1buttons, null);
        listView.addFooterView(listFooter);

        //Listview - Populate Data
        populateListViewData();
        TextView tvHeader = (TextView) listHeader.findViewById(R.id.section_header);
        tvHeader.setText(StringHelper.getStringByResourceId(getActivity(), R.string.label_child));

        //Screen - Title and Instructions
        fragmentContainer.setFragmentTitle(rootView, R.string.title_activity_family_view);
        fragmentContainer.setInstructions(listHeader, CURRENT_POSITION,
                R.string.error_family_view_select_child, false);
    }

    private void populateListViewData() {
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChildStatement childStatement = new ChildStatement();
                app.setChildStatement(childStatement);

                childStatement.setChildItem(adapter.getItem(position - 1));
                app.setChildStatement(childStatement);

                executeGetChildStatementAsyncTask();

                isRowSelected = true;
            }
        });
    }

    //--- DIALOG BOXES -----------------------------------------------------------------------------

    private void createNoChildSelectedMessageBox() {
        MessageBox.show(context,
                StringHelper.getStringByResourceId(context, R.string.error_family_view_select_child),
                false, true, R.string.btn_ok, false, 0, new MessageBoxButtonClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickNegativeButton(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
    }

    //--- HELPERS ----------------------------------------------------------------------------------

    private void executeGetChildListAsyncTask() {
        if (ConnectionHelper.isNetworkConnected(context)) {
            new GetChildListItemsTask().execute();
        } else {
            fragmentContainer.createDeviceOfflineMessageBox();
        }
    }

    private void executeGetChildStatementAsyncTask() {
        if (ConnectionHelper.isNetworkConnected(context)) {
            new GetChildStatementTask().execute(app.getChildStatement().getChildItem());
        } else {
            fragmentContainer.createDeviceOfflineMessageBox();
        }
    }

    //--- ASYNC TASK -------------------------------------------------------------------------------

    private class GetChildListItemsTask extends AsyncTask<Void, Void, ChildItem[]> {

        private ProgressDialog dialog;

        public GetChildListItemsTask() {
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            if (!isPreviouslyLoaded) {
                dialog.setMessage(StringHelper.getStringByResourceId(context,
                        R.string.progress_common_load_child_list));
                dialog.show();
            }
        }

        @Override
        protected ChildItem[] doInBackground(Void... params) {
            ChildItem[] items = ProxyFactory.getOtherProxy().getChildItemList();
            return items;
        }

        protected void onPostExecute(ChildItem[] items) {
            Collections.addAll(childItems, items);
            adapter.notifyDataSetChanged();

            nextButton.setVisibility(View.VISIBLE);

            isPreviouslyLoaded = true;
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private class GetChildStatementTask extends AsyncTask<ChildItem, Void, ChildStatement> {

        private ProgressDialog dialog;

        public GetChildStatementTask() {
            this.dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(StringHelper.getStringByResourceId(context,
                    R.string.progress_family_view_load_cash_gift_details));
            dialog.show();
        }

        @Override
        protected ChildStatement doInBackground(ChildItem... params) {
            ChildStatement childStatement = ProxyFactory.getOtherProxy().getChildStatement(
                    params[0].getChild().getNric());
            childStatement.setChildItem(params[0]);
            return childStatement;
        }

        protected void onPostExecute(ChildStatement childStmt) {
            app.setChildStatement(childStmt);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
}
