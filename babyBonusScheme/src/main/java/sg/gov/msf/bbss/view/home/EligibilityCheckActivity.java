package sg.gov.msf.bbss.view.home;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.view.MainActivity;

/**
 * Created by chuanhe
 */
public class EligibilityCheckActivity extends ActionBarActivity {

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_webview);
        setTitleBar();

        WebView faqWebView = (WebView)findViewById(R.id.wvMain);

        WebSettings settings = faqWebView.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setJavaScriptEnabled(true);

        faqWebView.loadUrl(BabyBonusConstants.ELIGIBILITY_CHECK_URL);
        faqWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
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

    //--- HELPERS ----------------------------------------------------------------------------------

    private void setTitleBar() {
        getActionBar().hide();

        RelativeLayout titleBar = (RelativeLayout)findViewById(R.id.screen_title_bar);

        //Screen Title
        TextView tvPageTitle = (TextView)titleBar.findViewById(R.id.tvPageTitle);
        tvPageTitle.setText(R.string.title_activity_eligibility_check);

        //Back Button
        ImageView ivBackButton = (ImageView)titleBar.findViewById(R.id.ivBackButton);
        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EligibilityCheckActivity.this, MainActivity.class));
                finish();
            }
        });
    }


}
