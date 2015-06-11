package sg.gov.msf.bbss.logic.server.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.masterdata.MasterDataListener;
import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.logic.type.ServiceAppType;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;

/**
 * Created by bandaray
 */
public class ServicesGetChildListItemsTask extends AsyncTask<Void, Void, ChildItem[]> {

    private Context context;
    private ServiceAppType serviceType;
    private MasterDataListener<ChildItem[]> callBackListener;
    private boolean isPreviouslyLoaded;
    private ProgressDialog dialog;


    public ServicesGetChildListItemsTask(Context context, ServiceAppType serviceType,
                                         MasterDataListener<ChildItem[]> callBackListener) {
        Log.i(getClass().getName(), "----------ServicesGetChildListItemsTask()");

        this.context = context;
        this.serviceType = serviceType;
        this.callBackListener = callBackListener;
        dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        Log.i(getClass().getName() , "----------onPreExecute()");

        if (!isPreviouslyLoaded) {
            dialog.setMessage(StringHelper.getStringByResourceId(context,
                    R.string.progress_common_load_child_list));
            dialog.show();
        }
    }

    @Override
    protected ChildItem[] doInBackground(Void... params) {
        Log.i(getClass().getName() , "----------doInBackground()");

        ChildItem[] items = ProxyFactory.getEServiceProxy().getChildItemList(serviceType);
        return items;
    }

    protected void onPostExecute(ChildItem[] items) {
        Log.i(getClass().getName(), "----------onPostExecute()");

        callBackListener.onMasterData(items);

        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
