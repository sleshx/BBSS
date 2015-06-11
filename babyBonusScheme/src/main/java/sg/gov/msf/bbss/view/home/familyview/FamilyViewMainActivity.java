package sg.gov.msf.bbss.view.home.familyview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
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
import sg.gov.msf.bbss.logic.adapter.SelectChildArrayAdapter;
import sg.gov.msf.bbss.logic.server.ServerConnectionHelper;
import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.logic.type.ChildListType;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.entity.childdata.ChildStatement;
import sg.gov.msf.bbss.view.MainActivity;
import sg.gov.msf.bbss.view.home.UpdateProfileActivity;

/**
 * Created by bandaray
 */
public class FamilyViewMainActivity extends Activity{

    private View rootView;
    private Context context;
    private BbssApplication app;

    private ListView listView;
    private Button pageButton;

    private SelectChildArrayAdapter adapter;
    private List<ChildItem> childItems;

    private boolean isRowSelected = false;
    private boolean isPreviouslyLoaded = false;

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_listview);

        context = this;
        getActionBar().hide();

        rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        app = (BbssApplication) getApplication();

        listView = (ListView) rootView.findViewById(R.id.lvMain);
        childItems = new ArrayList<ChildItem>();
        adapter = new SelectChildArrayAdapter(context, childItems, ChildListType.FAMILY_VIEW, app);

        displayData();
        setButtonClicks();

        executeGetChildListAsyncTask();
    }

    //--- BACK NAVIGATION --------------------------------------------------------------------------

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {
        ImageView ivBackButton = (ImageView) rootView.findViewById(R.id.ivBackButton);
        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        });

        setBottomButtonClicks();
    }

    private void setBottomButtonClicks() {
        pageButton = (Button) rootView.findViewById(R.id.btnFirstInOne);
        pageButton.setText(R.string.btn_next);
        pageButton.setVisibility(View.GONE);
        pageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRowSelected) {
                    MessageBox.show(context,
                            StringHelper.getStringByResourceId(context, R.string.error_family_view_select_child),
                            false, true, R.string.btn_ok, false, 0, null);
                } else {
                    startActivity(new Intent(context, FamilyViewFragmentContainerActivity.class));
                    finish();
                }
            }
        });

    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    private void displayData() {

        //Listview - Header
        View listHeader = getLayoutInflater().inflate(
                R.layout.section_header_row_and_instruction_row, null);
        if (listView.getHeaderViewsCount() == 0) {
            listView.addHeaderView(listHeader);
        }

        //Listview - Footer
        View  listFooter = getLayoutInflater().inflate(R.layout.clickable_1buttons, null);
        listView.addFooterView(listFooter);

        //Listview - Populate Data
        populateListViewData();
        TextView tvHeader = (TextView) listHeader.findViewById(R.id.section_header);
        tvHeader.setText(StringHelper.getStringByResourceId(context, R.string.label_child));

        //Screen - Title and Instructions
        //fragmentContainer.setFragmentTitle(rootView, R.string.title_activity_family_view);
        WebView webView = (WebView) listHeader.findViewById(R.id.wvInstructionDesc);
        webView.loadData(StringHelper.getJustifiedString(this, R.string.error_family_view_select_child,
                R.color.theme_creme), "text/html", "utf-8");

        ((TextView) findViewById(R.id.tvInstructionTitle)).setVisibility(View.GONE);
        ((WebView) findViewById(R.id.wvErrorDesc)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.tvInstructionStepNo)).setVisibility(View.GONE);
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

    //--- HELPERS ----------------------------------------------------------------------------------

    private void executeGetChildListAsyncTask() {
        if (ConnectionHelper.isNetworkConnected(context)) {
            new GetChildListItemsTask().execute();
        } else {
            createDeviceOfflineMessageBox();
        }
    }

    private void executeGetChildStatementAsyncTask() {
        if (ConnectionHelper.isNetworkConnected(context)) {
            new GetChildStatementTask().execute(app.getChildStatement().getChildItem());
        } else {
            createDeviceOfflineMessageBox();
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

            pageButton.setVisibility(View.VISIBLE);

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

    //--- DIALOG BOXES ------------------------------------------------------------------------------

    public void createDeviceOfflineMessageBox() {
        Log.i(getClass().getName(), "----------createOkToCancelMessageBox()");

        ServerConnectionHelper.createDeviceOfflineMessageBox(context,
                new MessageBoxButtonClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        startActivity(new Intent(context, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onClickNegativeButton(DialogInterface dialog, int id) {

                    }
                });
    }
}
