package sg.gov.msf.bbss.view.enrolment.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.ui.BbssActionBarActivity;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.server.login.LoginManager;
import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.logic.type.EnrolmentAppType;
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentForm;
import sg.gov.msf.bbss.view.MainActivity;

/**
 * Created by bandaray
 */
public class EnrolmentMainActivity extends BbssActionBarActivity {

    private Context context;
    private BbssApplication app;

    private boolean dummy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insturction_main);

        dummy = getIntent().getBooleanExtra("ENROLMENT_IS_PRE_PO", false);

        getActionBar().hide();
        context = this;
        app = (BbssApplication) getApplication();

        displayData();
        setButtonClicks();
    }

    //----------------------------------------------------------------------------------------------

    public void displayData() {
        //Screen Title
        ((TextView) findViewById(R.id.tvPageTitle)).setText(StringHelper.getStringByResourceId(this,
                R.string.title_activity_enrolment_main));
        this.getTitle();

        //Screen Instructions
        LinearLayout headerLayout = (LinearLayout) findViewById(R.id.screen_instructions);

        ((TextView) headerLayout.findViewById(R.id.tvInstructionTitle)).setText(
                R.string.label_enrolment_main_instruction);

        ((WebView) headerLayout.findViewById(R.id.wvInstructionDesc)).setVisibility(View.GONE);
        ((WebView) headerLayout.findViewById(R.id.wvErrorDesc)).setVisibility(View.GONE);
        ((TextView) headerLayout.findViewById(R.id.tvInstructionMandatory)).setVisibility(View.GONE);
        ((TextView) headerLayout.findViewById(R.id.tvInstructionStepNo)).setVisibility(View.GONE);

        //Screen Header
        ((TextView) findViewById(R.id.enrolment_main_screen_header))
                .setText(StringHelper.getStringByResourceId(this,
                        R.string.label_enrolment_main_header));

        //Set Description
        ((WebView) findViewById(R.id.wvScreenDescription)).loadData(
                StringHelper.getJustifiedString(context,
                        R.string.label_enrolment_main_description,
                        R.color.theme_gray_default_bg), "text/html", "utf-8");
    }

    private void setButtonClicks() {
        Button enrolButton = (Button) findViewById(R.id.btnFirstInOne);
        enrolButton.setText(R.string.btn_apply_now);
        enrolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(BabyBonusConstants.ENROLMENT_APP_MODE, EnrolmentAppType.NEW.getCode());
                //bundle.putBoolean(BabyBonusConstants.ENROLMENT_IS_PRE_PO, true);
                bundle.putBoolean(BabyBonusConstants.ENROLMENT_IS_PRE_PO, false);

                LoginManager.login(EnrolmentMainActivity.this, EnrolmentFragmentContainerActivity.class, bundle);

//                Intent intent1 = new Intent(context, EnrolmentFragmentContainerActivity.class);
//                intent1.putExtras(bundle);
//                startActivity(intent1);
//                finish();
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

    //----------------------------------------------------------------------------------------------

    private class GetEnrolmentDataTask extends AsyncTask<Void, Void, EnrolmentForm> {

        private ProgressDialog dialog;

        public GetEnrolmentDataTask() {
            this.dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading enrolment details");
            dialog.show();
        }

        @Override
        protected EnrolmentForm doInBackground(Void... params) {
            EnrolmentForm enrolmentForm = new EnrolmentForm(false, EnrolmentAppType.NEW);
            if (dummy) {
                enrolmentForm = ProxyFactory.getEnrolmentProxy().getPrePopulatedApplication(EnrolmentAppType.NEW);
            }
            return enrolmentForm;
        }

        protected void onPostExecute(EnrolmentForm enrolmentForm) {
            enrolmentForm.setAppType(EnrolmentAppType.NEW);
            app.setEnrolmentForm(enrolmentForm);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            startActivity(new Intent(context, EnrolmentFragmentContainerActivity.class));
        }
    }
}
