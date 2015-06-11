package sg.gov.msf.bbss.view.eservice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.ui.BbssActionBarActivity;
import sg.gov.msf.bbss.apputils.ui.component.MessageBox;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.adapter.CustomGridListViewAdapter;
import sg.gov.msf.bbss.logic.adapter.util.CustomGridListViewItem;
import sg.gov.msf.bbss.logic.masterdata.MasterDataCache;
import sg.gov.msf.bbss.logic.masterdata.MasterDataListener;
import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.logic.server.task.DataCodes;
import sg.gov.msf.bbss.logic.type.EnvironmentType;
import sg.gov.msf.bbss.logic.type.ServiceAppType;
import sg.gov.msf.bbss.model.entity.masterdata.AccessibleService;
import sg.gov.msf.bbss.view.MainActivity;
import sg.gov.msf.bbss.view.eservice.bo.ChangeBoFragmentContainerActivity;
import sg.gov.msf.bbss.view.eservice.cdab.ChangeCdabFragmentContainerActivity;
import sg.gov.msf.bbss.view.eservice.cdabtc.AcceptCdabTcFragmentContainerActivity;
import sg.gov.msf.bbss.view.eservice.cdat.ChangeCdatFragmentContainerActivity;
import sg.gov.msf.bbss.view.eservice.nah.ChangeNahFragmentContainerActivity;
import sg.gov.msf.bbss.view.eservice.nan.ChangeNanFragmentContainerActivity;
import sg.gov.msf.bbss.view.eservice.opencda.OpenCdaFragmentContainerActivity;
import sg.gov.msf.bbss.view.eservice.psea.TransferPseaFragmentContainerActivity;

/**
 * Created by bandaray
 */
public class ServicesHomeActivity extends BbssActionBarActivity {

    private static int[] IMG_ARR = {
            R.drawable.ic_service_change_nah, R.drawable.ic_service_change_nan,
            R.drawable.ic_service_change_cdat,R.drawable.ic_service_change_cdab,
            R.drawable.ic_service_cda_psea, R.drawable.ic_service_change_bo,
            R.drawable.ic_service_cdab_tc, R.drawable.ic_service_open_cda
    };

    private static int[] TXT_ARR = {
            R.string.label_service_change_nah, R.string.label_service_change_nan,
            R.string.label_service_change_cdat, R.string.label_service_change_cdab,
            R.string.label_service_cda_psea, R.string.label_service_change_bo,
            R.string.label_service_cdab_tc, R.string.label_service_open_cda
    };

    private Context context;

    //--- CREATION ---------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_home_screen);
        context = this;
        getActionBar().hide();

        setScreenTitle();
        displayAccessibleServices();
        setBackButtonClick();
    }

    @Override
    protected void onPause() {
        super.onPause();

        BbssApplication app = (BbssApplication) getApplication();
        if (app.getProgressDialog() != null) {
            app.getProgressDialog().dismiss();
        }
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

    private void setBackButtonClick() {
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

    private void displayAccessibleServices(){
        MasterDataCache masterDataCache = new MasterDataCache(context);
        masterDataCache.getAccessibleServices(new MasterDataListener<AccessibleService[]>() {
            @Override
            public void onMasterData(AccessibleService[] accessibleServices) {
                ArrayList<CustomGridListViewItem> listArray = new ArrayList<CustomGridListViewItem>();

                if(ProxyFactory.ENVIRONMENT == EnvironmentType.DEV){
                    listArray = serviceListArrayDev(accessibleServices);
                } else if (ProxyFactory.ENVIRONMENT == EnvironmentType.DEP){
                    listArray = serviceListArray(accessibleServices);
                }

                if (listArray.size() > 0) {
                    populateServicesList(listArray);
                } else {
                    MessageBox.show(context,
                            StringHelper.getStringByResourceId(context, R.string.error_services_no_accessible_services),
                            false, true, R.string.btn_ok, false, 0, null);
                }
            }
        });
    }

    private void populateServicesList(final ArrayList<CustomGridListViewItem> listArray) {
        ListView listView = (ListView) findViewById(R.id.gridView1);

        listView.setAdapter(new CustomGridListViewAdapter(this, R.layout.layout_item_services_list,
                listArray, false));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ServiceAppType appType = listArray.get(position).getServiceType();

                switch (appType) {
                    case CHANGE_NAH:
                        //Change Nominated Account Holder
                        startActivity(new Intent(context,
                                ChangeNahFragmentContainerActivity.class));
                        break;
                    case CHANGE_NAN:
                        //Change Nominated Account Number
                        startActivity(new Intent(context,
                                ChangeNanFragmentContainerActivity.class));
                        break;
                    case CHANGE_CDAT:
                        //Change CDA Trustee
                        startActivity(new Intent(context,
                                ChangeCdatFragmentContainerActivity.class));
                        break;
                    case CHANGE_CDAB:
                        //Change CDA Bank
                        startActivity(new Intent(context,
                                ChangeCdabFragmentContainerActivity.class));
                        break;
                    case CDA_TO_PSEA:
                        //Transfer CDA to PSEA
                        startActivity(new Intent(context,
                                TransferPseaFragmentContainerActivity.class));
                        break;
                    case CHANGE_BO:
                        //Change Birth Order
                        startActivity(new Intent(context,
                                ChangeBoFragmentContainerActivity.class));
                        break;
                    case TERMS_AND_COND:
                        //Accept CDA Bank T&C
                        startActivity(new Intent(context,
                                AcceptCdabTcFragmentContainerActivity.class));
                        break;
                    case OPEN_CDA:
                        //Open CDA
                        startActivity(new Intent(context,
                                OpenCdaFragmentContainerActivity.class));
                        break;
                }
            }
        });

    }

    //--- HELPERS ----------------------------------------------------------------------------------

    public void setScreenTitle() {
        TextView title = (TextView) findViewById(R.id.tvPageTitle);
        title.setText(StringHelper.getStringByResourceId(this,
                R.string.title_activity_services));
        this.getTitle();
    }

    private ArrayList serviceListArray(AccessibleService[] accessibleServices) {
        ArrayList<CustomGridListViewItem> listArray = new ArrayList<CustomGridListViewItem>();

        AccessibleService accessibleService;
        CustomGridListViewItem listItem;
        int serviceCount = accessibleServices.length;

        for(int position = 0; position < serviceCount; position ++) {
            accessibleService = accessibleServices[position];

            if(accessibleService.getCode().equals(ServiceAppType.CHANGE_NAH.getCode())) {
                listItem = new CustomGridListViewItem(context, ServiceAppType.CHANGE_NAH);

            } else if(accessibleService.getCode().equals(ServiceAppType.CHANGE_NAN.getCode())) {
                listItem = new CustomGridListViewItem(context, ServiceAppType.CHANGE_NAN);

            } else if(accessibleService.getCode().equals(ServiceAppType.CHANGE_CDAT.getCode())) {
                listItem = new CustomGridListViewItem(context, ServiceAppType.CHANGE_CDAT);

            } else if(accessibleService.getCode().equals(ServiceAppType.CHANGE_CDAB.getCode())) {
                listItem = new CustomGridListViewItem(context, ServiceAppType.CHANGE_CDAB);

            } else if(accessibleService.getCode().equals(ServiceAppType.CDA_TO_PSEA.getCode())) {
                listItem = new CustomGridListViewItem(context, ServiceAppType.CDA_TO_PSEA);

            } else if(accessibleService.getCode().equals(ServiceAppType.CHANGE_BO.getCode())) {
                listItem = new CustomGridListViewItem(context, ServiceAppType.CHANGE_BO);

            } else if(accessibleService.getCode().equals(ServiceAppType.TERMS_AND_COND.getCode())) {
                listItem = new CustomGridListViewItem(context, ServiceAppType.TERMS_AND_COND);

            } else if(accessibleService.getCode().equals(ServiceAppType.OPEN_CDA.getCode())) {
                listItem = new CustomGridListViewItem(context, ServiceAppType.OPEN_CDA);
            } else {
                continue;
            }

            if(accessibleService.isOutstanding()){
                listItem.setBadgeCount(1);
            }

            listArray.add(listItem);
        }
        return listArray;
    }

    private ArrayList serviceListArrayDev(AccessibleService[] accessibleServices) {
        ArrayList<CustomGridListViewItem> listArray = new ArrayList<CustomGridListViewItem>();

        AccessibleService accessibleService;
        CustomGridListViewItem listItem;
        int serviceCount = accessibleServices.length;

        for(int position = 0; position < serviceCount; position ++) {
            accessibleService = accessibleServices[position];

            if(accessibleService.getCode().equals(DataCodes.ACCESS_SERVICE_CHANGE_NAH_CODE)) {
                listItem = new CustomGridListViewItem(context, ServiceAppType.CHANGE_NAH);

            } else if(accessibleService.getCode().equals(DataCodes.ACCESS_SERVICE_CHANGE_NAN_CODE)) {
                listItem = new CustomGridListViewItem(context, ServiceAppType.CHANGE_NAN);

            } else if(accessibleService.getCode().equals(DataCodes.ACCESS_SERVICE_CHANGE_CDAT_CODE)) {
                listItem = new CustomGridListViewItem(context, ServiceAppType.CHANGE_CDAT);

            } else if(accessibleService.getCode().equals(DataCodes.ACCESS_SERVICE_CHANGE_CDAB_CODE)) {
                listItem = new CustomGridListViewItem(context, ServiceAppType.CHANGE_CDAB);

            } else if(accessibleService.getCode().equals(DataCodes.ACCESS_SERVICE_CHANGE_CDA_PSEA_CODE)) {
                listItem = new CustomGridListViewItem(context, ServiceAppType.CDA_TO_PSEA);

            } else if(accessibleService.getCode().equals( DataCodes.ACCESS_SERVICE_CHANGE_BO_CODE)) {
                listItem = new CustomGridListViewItem(context, ServiceAppType.CHANGE_BO);

            } else if(accessibleService.getCode().equals(DataCodes.ACCESS_SERVICE_CHANGE_CDAB_TC_CODE)) {
                listItem = new CustomGridListViewItem(context, ServiceAppType.TERMS_AND_COND);

            } else if(accessibleService.getCode().equals(DataCodes.ACCESS_SERVICE_CHANGE_OPEN_CDA_CODE)) {
                listItem = new CustomGridListViewItem(context, ServiceAppType.OPEN_CDA);
            } else {
                continue;
            }

            if(accessibleService.isOutstanding()){
                listItem.setBadgeCount(1);
            }

            listArray.add(listItem);
        }
        return listArray;
    }
}
