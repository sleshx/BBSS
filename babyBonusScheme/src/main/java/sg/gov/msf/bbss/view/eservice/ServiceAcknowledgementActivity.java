package sg.gov.msf.bbss.view.eservice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.ui.BbssActionBarActivity;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.BabyBonusConstants;

/**
 * Created by chuanhe
 */
public class ServiceAcknowledgementActivity extends BbssActionBarActivity {
    private Context context;
    private static String APP_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insturction_main);

        getActionBar().hide();
        context = this;

        APP_ID = getIntent().getExtras().getString(BabyBonusConstants.SERVICES_APP_ID);

        displayData();
        setButtonClicks();
    }

    public void displayData() {
        //Screen Title
        ((TextView) findViewById(R.id.tvPageTitle)).setText(
                StringHelper.getStringByResourceId(context,
                        R.string.title_activity_services_acknowledgement));
        this.getTitle();

        //Screen Instructions
        LinearLayout headerLayout = (LinearLayout) findViewById(R.id.screen_instructions);
        ((WebView) headerLayout.findViewById(R.id.wvInstructionDesc)).loadData(StringHelper.getJustifiedString(context,
                R.string.desc_services_common_acknowledgement_title_desc, R.color.theme_creme), "text/html", "utf-8");
        hideUnwantedLayouts(headerLayout);

        //Screen Header
        ((TextView) findViewById(R.id.enrolment_main_screen_header))
                .setText(StringHelper.getStringByResourceId(context,
                        R.string.desc_services_common_acknowledgement_header));

        //Set Description
        ((WebView) findViewById(R.id.wvScreenDescription)).loadData(getString(
                R.string.desc_services_common_acknowledgement_desc, APP_ID),
                "text/html", "utf-8");
    }

    private void hideUnwantedLayouts(LinearLayout headerLayout) {
        ((TextView) headerLayout.findViewById(R.id.tvInstructionTitle)).setVisibility(View.GONE);
        ((WebView) headerLayout.findViewById(R.id.wvErrorDesc)).setVisibility(View.GONE);
        ((TextView) headerLayout.findViewById(R.id.tvInstructionMandatory)).setVisibility(View.GONE);
        ((TextView) headerLayout.findViewById(R.id.tvInstructionStepNo)).setVisibility(View.GONE);
    }

    private void setButtonClicks() {
        Button enrolButton = (Button) findViewById(R.id.btnFirstInOne);
        enrolButton.setText(R.string.btn_ok);
        enrolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ServicesHomeActivity.class));
            }
        });

        ImageView backButton = (ImageView) findViewById(R.id.ivBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ServicesHomeActivity.class));
            }
        });
    }
}
