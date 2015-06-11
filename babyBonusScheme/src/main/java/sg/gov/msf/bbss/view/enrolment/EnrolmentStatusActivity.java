package sg.gov.msf.bbss.view.enrolment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.adapter.enrolment.EnrolmentStatusListViewAdapter;
import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.logic.type.EnrolmentAppType;
import sg.gov.msf.bbss.model.entity.EnrolmentStatus;
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentFormStatus;
import sg.gov.msf.bbss.view.MainActivity;
import sg.gov.msf.bbss.view.enrolment.main.EnrolmentFragmentContainerActivity;

/**
 * Created by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class EnrolmentStatusActivity extends ActionBarActivity {

    private Context context;

    private ExpandableListView expandableListView;
    private LayoutInflater inflater;

    private EnrolmentStatusListViewAdapter adapter;
    private List<EnrolmentStatus> enrolmentStatusList = new ArrayList<EnrolmentStatus>();

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_expandable_listview);
        context = this;
        getActionBar().hide();

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        expandableListView = (ExpandableListView)findViewById(R.id.lvExpandableMain);

        displayData();
        setButtonClicks();

        new EnrolmentStatusAsync().execute();
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

    //--- DISPLAY DATA IN UI -----------------------------------------------------------------------

    public void displayData(){
        //Listview
        expandableListView.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener(){
            public boolean onGroupClick(ExpandableListView arg0, View itemView,
                                        int itemPosition, long itemId) {
                expandableListView.expandGroup(itemPosition);

                RadioButton radioButton = (RadioButton)itemView.findViewById(R.id.checkableChoice);
                radioButton.setChecked(true);

                return true;
            }
        });

        //Listview - Header
        LinearLayout listHeaderView = (LinearLayout) inflater.inflate(
                R.layout.section_header_row_and_instruction_row, null);
        expandableListView.addHeaderView(listHeaderView);

        //Listview - Footer
        LinearLayout listFooterView = (LinearLayout) inflater.inflate(
                R.layout.clickable_2buttons, null);
        expandableListView.addFooterView(listFooterView);

        //Screen - Title
        ((TextView) findViewById(R.id.tvPageTitle)).setText(StringHelper.getStringByResourceId(this,
                R.string.title_activity_enrolment_status));

        //Screen - Instructions
        LinearLayout instructionLayout = (LinearLayout) findViewById(R.id.screen_instructions);

        ((TextView) instructionLayout.findViewById(
                R.id.tvInstructionTitle)).setVisibility(View.GONE);
        ((TextView) instructionLayout.findViewById(
                R.id.tvInstructionStepNo)).setVisibility(View.GONE);
        ((TextView) instructionLayout.findViewById(
                R.id.tvInstructionMandatory)).setVisibility(View.GONE);

        ((WebView) instructionLayout.findViewById(R.id.wvInstructionDesc)).loadData(
                StringHelper.getJustifiedString(context, R.string.label_enrolment_status,
                R.color.theme_creme), "text/html", "utf-8");

        //Screen - Header
        ((TextView) findViewById(R.id.section_header)).setText(
                R.string.title_activity_enrolment_status);
    }

    //--- BUTTON CLICKS ----------------------------------------------------------------------------

    private void setButtonClicks() {

        Button editButton = (Button) findViewById(R.id.btnFirstInTwo);
        editButton.setText(R.string.btn_edit_app);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EnrolmentFragmentContainerActivity.class);
                intent.putExtra(BabyBonusConstants.ENROLMENT_APP_MODE, EnrolmentAppType.EDIT.getCode());
                startActivity(intent);
            }
        });

        Button viewButton = (Button) findViewById(R.id.btnSecondInTwo);
        viewButton.setText(R.string.btn_view_app);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EnrolmentFragmentContainerActivity.class);
                intent.putExtra(BabyBonusConstants.ENROLMENT_APP_MODE, EnrolmentAppType.VIEW.getCode());
                startActivity(intent);
            }
        });

        ImageView backButton = (ImageView) findViewById(R.id.ivBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        });
    }

    //--- ASYNC TASKS ------------------------------------------------------------------------------

    public class EnrolmentStatusAsync extends AsyncTask<String,String,EnrolmentFormStatus> {
        private ProgressDialog dialog;

        public EnrolmentStatusAsync(){
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(StringHelper.getStringByResourceId(context,
                    R.string.progress_enrolment_load_status));
            dialog.show();
        }

        @Override
        protected EnrolmentFormStatus doInBackground(String... params) {
            return ProxyFactory.getEnrolmentProxy().getEnrolmentFormStatus();
        }

        @Override
        protected void onPostExecute(EnrolmentFormStatus enrolmentFormStatus) {
            Collections.addAll(enrolmentStatusList, enrolmentFormStatus.getEnrollStatus());
            adapter = new EnrolmentStatusListViewAdapter(context, enrolmentStatusList);

            expandableListView.setAdapter(adapter);

            adapter.notifyDataSetChanged();
            for (int i=0; i<adapter.getGroupCount(); i++) {
                expandableListView.expandGroup(i);
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }


}
