package sg.gov.msf.bbss.view.eservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.ui.BbssActionBarActivity;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.adapter.eservice.ServiceStatusListViewAdapter;

import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.logic.server.proxy.dev.OtherProxy;
import sg.gov.msf.bbss.model.entity.ServiceStatus;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceAppStatus;
import sg.gov.msf.bbss.view.MainActivity;


/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class ServiceStatusActivity extends BbssActionBarActivity {
    private Context context;

    private ListView listView;
    private LayoutInflater inflater;

    private ServiceStatusListViewAdapter adapter;
    private ServiceAppStatus serviceAppStatus;
    private List<ServiceStatus> statusDetailList = new ArrayList<ServiceStatus>();

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_listview);

        context = this;
        getActionBar().hide();

        inflater = (LayoutInflater)ServiceStatusActivity.this.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        listView = (ListView) findViewById(R.id.lvMain);
        adapter = new ServiceStatusListViewAdapter(context, statusDetailList);

        displayData();
        setButtonClicks();

        new GetServiceStatusAsync().execute();
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
        ImageView backButton = (ImageView) findViewById(R.id.ivBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        });
    }

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    public void displayData(){
        //Listview
        listView.setAdapter(adapter);

        //Listview - Header
        LinearLayout listHeaderView = (LinearLayout) inflater.inflate(
                R.layout.section_header_row_and_instruction_row, null);
        listView.addHeaderView(listHeaderView);

        //Screen - Title
        ((TextView) findViewById(R.id.tvPageTitle)).setText(StringHelper.getStringByResourceId(this,
                R.string.title_activity_services_status));

        //Screen - Instructions
        LinearLayout instructionLayout = (LinearLayout) findViewById(R.id.screen_instructions);

        ((TextView) instructionLayout.findViewById(
                R.id.tvInstructionTitle)).setVisibility(View.GONE);
        ((TextView) instructionLayout.findViewById(
                R.id.tvInstructionStepNo)).setVisibility(View.GONE);
        ((TextView) instructionLayout.findViewById(
                R.id.tvInstructionMandatory)).setVisibility(View.GONE);

        ((WebView) instructionLayout.findViewById(R.id.wvInstructionDesc)).loadData(
                StringHelper.getJustifiedString(context, R.string.desc_services_common_status,
                        R.color.theme_creme), "text/html", "utf-8");

        //Screen - Header
        ((TextView) findViewById(R.id.section_header)).setText(
                R.string.title_activity_services_status);
    }

    //--- ASYNC TASK -------------------------------------------------------------------------------

    private class GetServiceStatusAsync extends AsyncTask<Void, Void, ServiceStatus[]> {

        private ProgressDialog dialog;

        public GetServiceStatusAsync() {
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(StringHelper.getStringByResourceId(context,
                    R.string.progress_service_load_status));
            dialog.show();
        }

        @Override
        protected ServiceStatus[] doInBackground(Void... params) {
            ServiceStatus[] items = ProxyFactory.getOtherProxy().getServiceAppStatuses();
            return items;
        }

        @Override
        protected void onPostExecute(ServiceStatus[] items) {
            Collections.addAll(statusDetailList, items);
            adapter.notifyDataSetChanged();

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
}
