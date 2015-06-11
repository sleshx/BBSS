package sg.gov.msf.bbss.view;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.connect.ConnectionHelper;
import sg.gov.msf.bbss.apputils.ui.component.MessageBox;
import sg.gov.msf.bbss.apputils.ui.helper.ActionBarCreator;
import sg.gov.msf.bbss.apputils.util.BitmapImageHandler;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.adapter.CustomGridListViewAdapter;
import sg.gov.msf.bbss.logic.adapter.util.CustomGridListViewItem;
import sg.gov.msf.bbss.logic.masterdata.MasterDataCache;
import sg.gov.msf.bbss.logic.masterdata.MasterDataListener;
import sg.gov.msf.bbss.logic.server.ServerConnectionHelper;
import sg.gov.msf.bbss.logic.server.login.LoginManager;
import sg.gov.msf.bbss.logic.type.EnrolmentAppType;
import sg.gov.msf.bbss.logic.type.LoginModeType;
import sg.gov.msf.bbss.model.entity.masterdata.AccessibleService;
import sg.gov.msf.bbss.view.enrolment.main.EnrolmentFragmentContainerActivity;
import sg.gov.msf.bbss.view.enrolment.EnrolmentStatusActivity;
import sg.gov.msf.bbss.view.enrolment.main.EnrolmentMainActivity;
import sg.gov.msf.bbss.view.eservice.ServiceStatusActivity;
import sg.gov.msf.bbss.view.eservice.ServicesHomeActivity;
import sg.gov.msf.bbss.view.home.AiLocatorActivity;
import sg.gov.msf.bbss.view.home.EligibilityCheckActivity;
import sg.gov.msf.bbss.view.home.SiblingCheckActivity;
import sg.gov.msf.bbss.view.home.UpdateProfileActivity;
import sg.gov.msf.bbss.view.home.AboutActivity;
import sg.gov.msf.bbss.view.home.FaqActivity;
import sg.gov.msf.bbss.view.home.FeedbackActivity;
import sg.gov.msf.bbss.view.home.LoginActivity;
import sg.gov.msf.bbss.view.home.familyview.FamilyViewMainActivity;

/**
 * Created by bandaray
 */
public class MainActivity extends ActionBarActivity {

    private static int ACCESSIBLE_SERVICE_COUNT = 0;

    private Context context;
    private GridView gridView;
    private PopupWindow popupWindow;
    private View parentView;
    private LayoutInflater layoutInflater;

    private String loginLogout;
    private boolean isLogoutClicked;

    private static int[] IMG_ARR = {
            R.drawable.ic_home_family_view,
            R.drawable.ic_home_update_profile, R.drawable.ic_home_sibling_check,
            R.drawable.ic_home_enrolment,
            R.drawable.ic_home_enrolment_status, R.drawable.ic_home_eligibility_check,
            R.drawable.ic_home_services,
            R.drawable.ic_home_services_status, R.drawable.ic_home_ai_locator
    };

    private static int[] TXT_ARR = {
            R.string.label_home_family_view,
            R.string.label_home_update_profile, R.string.label_home_sibling_check,
            R.string.label_home_enrolment,
            R.string.label_home_enrolment_status, R.string.label_home_eligibility_check,
            R.string.label_home_services,
            R.string.label_home_services_status, R.string.label_home_ai_locator
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home_screen);

        context = this;
        layoutInflater = LayoutInflater.from(MainActivity.this);
        parentView = layoutInflater.inflate(R.layout.activity_main_home_screen, null);

        popList(MainActivity.this);

        ActionBarCreator actionBarCreator = new ActionBarCreator(context,
                this.getSupportActionBar());
        actionBarCreator.setupActionBarForHome(popupWindow);

        populateHomeGrid();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (LoginManager.getSessionContainer() != null) {
            setOutStandingServiceCount();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    //--- CONTEXT MENU -----------------------------------------------------------------------------

    private void popList(final Context context) {

        View popupView = layoutInflater.inflate(R.layout.layout_pop_list, null);
        ListView listView=(ListView)popupView.findViewById(R.id.lvPop);

        if (LoginManager.getSessionContainer() == null) {
            loginLogout = "Login";
            isLogoutClicked = false;
        } else {
            loginLogout = "Logout";
            isLogoutClicked = true;
        }

        String[] adapterData = new String[]{loginLogout,"FAQ","Feedback","About"};
        listView.setAdapter(new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, adapterData));
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Bundle bundle =  new Bundle();
                        bundle.putBoolean(BabyBonusConstants.IS_LOGOUT_CLICKED, isLogoutClicked);

                        LoginManager.login(MainActivity.this, MainActivity.class, bundle);
                        break;
                    case 1:
                        startActivity(new Intent(context, FaqActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(context, FeedbackActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(context, AboutActivity.class));
                        break;
                }
                popupWindow.dismiss();
            }
        });

        popupWindow = new PopupWindow(popupView,
                ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
    }

    //--- GRID VIEW --------------------------------------------------------------------------------

    private void populateHomeGrid() {
        final ArrayList<CustomGridListViewItem> gridArray = new ArrayList<CustomGridListViewItem>();
        gridView = (GridView) findViewById(R.id.gridView1);

        if (IMG_ARR.length == TXT_ARR.length) {
            for (int i = 0; i < IMG_ARR.length; i++) {
                gridArray.add(new CustomGridListViewItem(context, IMG_ARR[i], TXT_ARR[i]));
            }
        }

        gridView.setAdapter(new CustomGridListViewAdapter(context, R.layout.layout_item_home_grid,
                gridArray, true));

        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Context context = MainActivity.this;
                String title = gridArray.get(position).getTitle();

//                CustomGridListViewItem item = gridArray.get(position);
//                item.setImage(BitmapImageHandler.toGrayscale(item.getImage()));
//
//                gridArray.set(position, item);

                Bundle bundle =  new Bundle();
                bundle.putBoolean(BabyBonusConstants.IS_LOGOUT_CLICKED, false);

                switch (position) {
                    case 0:
                        LoginManager.login(MainActivity.this, FamilyViewMainActivity.class, bundle);
                        break;
                    case 1:
                        LoginManager.login(MainActivity.this, UpdateProfileActivity.class, bundle);
                        break;
                    case 2:
                        LoginManager.login(MainActivity.this, SiblingCheckActivity.class, bundle);
                        break;
                    case 3:
                        startActivity(new Intent(context, EnrolmentMainActivity.class));
                        finish();
                        break;
                    case 4:
                        LoginManager.login(MainActivity.this, EnrolmentStatusActivity.class, bundle);
                        break;
                    case 5:
                        startActivity(new Intent(context, EligibilityCheckActivity.class));
                        finish();
                        break;
                    case 6:
                        LoginManager.login(MainActivity.this, ServicesHomeActivity.class, bundle);
                        break;
                    case 7:
                        LoginManager.login(MainActivity.this, ServiceStatusActivity.class, bundle);
                        break;
                    case 8:
                        startActivity(new Intent(context, AiLocatorActivity.class));
                        finish();
                        break;
                }
            }
        });
    }

    private void setOutStandingServiceCount(){
        int outstandingCount = 0;

        AccessibleService[] accessibleServices = LoginManager.getSessionContainer().getAccessibleServices();
        if (accessibleServices != null) {
            ACCESSIBLE_SERVICE_COUNT = accessibleServices.length;
        }

        if(ACCESSIBLE_SERVICE_COUNT > 0) {
            for (AccessibleService accessibleService : accessibleServices) {
                if (accessibleService.isOutstanding()) {
                    outstandingCount++;
                }
            }

            if (outstandingCount > 0) {
                CustomGridListViewAdapter adapter = (CustomGridListViewAdapter)gridView.getAdapter();
                adapter.getItem(6).setBadgeCount(outstandingCount);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
