package sg.gov.msf.bbss.view.home;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.logic.BabyBonusConstants;

/**
 * Created by chuanhe
 */
public class AboutActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_webview);
        setTitleBar();

        WebView feedbackWebView = (WebView)findViewById(R.id.wvMain);

        WebSettings settings = feedbackWebView.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setJavaScriptEnabled(true);

        feedbackWebView.loadUrl(BabyBonusConstants.ABOUT_URL);
        feedbackWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                return true;
            }
        });
    }

    private void setTitleBar() {
        getActionBar().hide();

        RelativeLayout titleBar = (RelativeLayout)findViewById(R.id.screen_title_bar);

        //Screen Title
        TextView tvPageTitle = (TextView)titleBar.findViewById(R.id.tvPageTitle);
        tvPageTitle.setText(R.string.title_activity_home_about);

        //Back Button
        ImageView ivBackButton = (ImageView)titleBar.findViewById(R.id.ivBackButton);
        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.this.finish();
            }
        });
    }
}
