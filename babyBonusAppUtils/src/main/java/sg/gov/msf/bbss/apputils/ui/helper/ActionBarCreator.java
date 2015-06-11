package sg.gov.msf.bbss.apputils.ui.helper;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import sg.gov.msf.bbss.apputils.R;

/**
 * Created by bandaray on 11/12/2014.
 */
public class ActionBarCreator {

    private ActionBar actionBar;
    private Context context;
    private boolean isShow = true;
    private ImageView showListBtn;

    public ActionBarCreator(Context context, ActionBar actionBar) {
        this.actionBar = actionBar;
        this.context = context;

        initializeActionBar();
    }

    private void initializeActionBar() {
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
    }

    public void setupActionBarForHome(final PopupWindow popupWindow) {
        View customNav = LayoutInflater.from(context).inflate(R.layout.action_bar_main,
                new LinearLayout(context), false);
        LayoutParams layoutParam = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

        actionBar.setCustomView(customNav, layoutParam);

        // custom action bar click listener
        showListBtn = ((ImageView) actionBar.getCustomView().findViewById(R.id.ivLogout));
        ((ImageView) actionBar.getCustomView().findViewById(R.id.ivLogout))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!popupWindow.isShowing()){
                            isShow = true;
                        }

                        if (isShow){
                            popupWindow.showAsDropDown(showListBtn,-30,30);
                            isShow=false;
                        }else {
                            popupWindow.dismiss();
                            isShow=true;
                        }
                    }
                });
    }

    public void setupActionBarOther(String title) {
        View customNav = LayoutInflater.from(context).inflate(R.layout.action_bar_fragment,
                new LinearLayout(context), false);
        LayoutParams layoutParam = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

        actionBar.setCustomView(customNav, layoutParam);

        TextView tvTitle = (TextView) actionBar.getCustomView().findViewById(R.id.tvPageTitle);
        tvTitle.setText(title);
    }
}
