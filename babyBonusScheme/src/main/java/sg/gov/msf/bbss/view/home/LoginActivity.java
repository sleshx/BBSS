package sg.gov.msf.bbss.view.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.impl.conn.LoggingSessionOutputBuffer;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.ui.BbssActionBarActivity;
import sg.gov.msf.bbss.apputils.ui.component.MessageBox;
import sg.gov.msf.bbss.apputils.ui.helper.MessageBoxButtonClickListener;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.util.ValidationHandler;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.server.login.LoginManager;
import sg.gov.msf.bbss.logic.server.login.SessionContainer;
import sg.gov.msf.bbss.logic.type.LoginModeType;
import sg.gov.msf.bbss.logic.type.LoginUserType;
import sg.gov.msf.bbss.view.MainActivity;

/**
 * Created by bandaray
 */
public class LoginActivity extends BbssActionBarActivity {

    private Context context;
    private WebView singpassWebView;
    private String url;
    private boolean isLogoutClicked;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_webview);
        setTitleBar();

        context = this;
        singpassWebView = (WebView)findViewById(R.id.wvMain);

        isLogoutClicked = getIntent().getExtras().getBoolean(BabyBonusConstants.IS_LOGOUT_CLICKED);
        bundle = getIntent().getExtras();

        if (isLogoutClicked) {
            url = BabyBonusConstants.LOGOUT_URL;
        } else {
            url = BabyBonusConstants.LOGIN_URL;
        }


        WebSettings settings = singpassWebView.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setJavaScriptEnabled(true);

        singpassWebView.loadUrl(url);
        singpassWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!isLogoutClicked) {
                    singPassLogin(view, url);
                }
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (isLogoutClicked) {
                    singPassLogout();
                }
            }
        });
    }

    //----------------------------------------------------------------------------------------------

    private void singPassLogout() {
        LoginManager.setSessionContainer(null);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    private void singPassLogin(final WebView view, String url) {
        if(url != null){
            Uri uri = Uri.parse(url);

            String token = uri.getQueryParameter("token");
            String nric = uri.getQueryParameter("NRIC");
            //Log.d(getClass().getName(), "NRIC = " + nric + "\n token = " + token);

            if(ValidationHandler.isValidNric(nric)) {
                nric = StringHelper.getCapitalizedNric(nric);

                if(token != null){
                    Class <?> toActivityClass = (Class<?>) getIntent().getExtras().
                            getSerializable(BabyBonusConstants.LOGIN_TO_ACTIVITY_CLASS);

                    LoginManager.setSessionContainer(new SessionContainer(nric, token, LoginUserType.PARENT));
                    LoginManager.login(LoginActivity.this, toActivityClass, bundle);

                    finish();
                } else {
                    MessageBox.show(context,
                            StringHelper.getStringByResourceId(context, R.string.error_common_not_allowed_to_login),
                            false, true, R.string.btn_ok, false, 0, new MessageBoxButtonClickListener() {
                                @Override
                                public void onClickPositiveButton(DialogInterface dialog, int id) {
                                    view.loadUrl(BabyBonusConstants.LOGIN_URL);
                                    dialog.dismiss();
                                }

                                @Override
                                public void onClickNegativeButton(DialogInterface dialog, int id) {

                                }
                            });
                }

            } else {
                MessageBox.show(context,
                        StringHelper.getStringByResourceId(context, R.string.error_common_not_allowed_to_login),
                        false, true, R.string.btn_ok, false, 0, new MessageBoxButtonClickListener() {
                            @Override
                            public void onClickPositiveButton(DialogInterface dialog, int id) {
                                view.loadUrl(BabyBonusConstants.LOGIN_URL);
                                dialog.dismiss();
                            }

                            @Override
                            public void onClickNegativeButton(DialogInterface dialog, int id) {

                            }
                        });
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    private void setTitleBar() {
        getActionBar().hide();

        RelativeLayout titleBar = (RelativeLayout)findViewById(R.id.screen_title_bar);

        //Screen Title
        TextView tvPageTitle = (TextView)titleBar.findViewById(R.id.tvPageTitle);
        tvPageTitle.setText(R.string.title_activity_login);

        //Back Button
        ImageView ivBackButton = (ImageView)titleBar.findViewById(R.id.ivBackButton);
        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}