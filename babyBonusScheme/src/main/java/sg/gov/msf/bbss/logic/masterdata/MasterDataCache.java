package sg.gov.msf.bbss.logic.masterdata;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.util.Hashtable;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.logic.type.MasterDataType;
import sg.gov.msf.bbss.model.entity.masterdata.AccessibleService;
import sg.gov.msf.bbss.model.entity.masterdata.Bank;
import sg.gov.msf.bbss.model.entity.masterdata.GenericDataItem;

/**
 * Created by bandaray
 */
public class MasterDataCache {

    private static Hashtable<MasterDataType, GenericDataItem[]> genericDataItemCache =
                                            new Hashtable<MasterDataType, GenericDataItem[]>();

    private static Bank[] banks;
    private static AccessibleService[] accessibleServiceses;
    private Context context;

    public MasterDataCache(Context context) {
        this.context = context;
    }

    public void getGenericDataItems(MasterDataType masterDataType,
                                    MasterDataListener<GenericDataItem[]> dataListener){
        GenericDataItem[] dataItems = genericDataItemCache.get(masterDataType);

        if(dataItems != null && dataItems.length > 0){
            dataListener.onMasterData(dataItems);
        }else {
            int messageId = R.string.progress_common_fetching_master_data;
            if (masterDataType == MasterDataType.USER_ROLE) {
                messageId = R.string.progress_common_fetching_user_roles;
            }

            GetMasterDataTask task = new GetMasterDataTask(context, messageId);
            task.setGenericDataListener(dataListener);
            task.execute(masterDataType);
        }
    }

    public void getBanks(MasterDataType type, MasterDataListener<Bank[]> dataListener){
        if(banks != null && banks.length > 0){
            dataListener.onMasterData(banks);
        } else {
            GetMasterDataTask task = new GetMasterDataTask(context,
                    R.string.progress_common_fetching_banks);
            task.setBankDataListener(dataListener);
            task.execute(type);
        }
    }

    public void getAccessibleServices(MasterDataListener<AccessibleService[]> dataListener){
        if(accessibleServiceses != null && accessibleServiceses.length > 0){
            dataListener.onMasterData(accessibleServiceses);
        }else {
            GetMasterDataTask task = new GetMasterDataTask(context,
                    R.string.progress_common_fetching_accessible_services);
            task.setAccessibleServiceDataListener(dataListener);
            task.execute(MasterDataType.ACCESSIBLE_SERVICES);
        }
    }

    //--- ASYNC TASK -------------------------------------------------------------------------------

    private class GetMasterDataTask extends AsyncTask<MasterDataType, Void, MasterDataType> {

        private MasterDataListener<GenericDataItem[]> genericDataListener;
        private MasterDataListener<Bank[]> bankDataListener;
        private MasterDataListener<AccessibleService[]> serviceDataListener;

        private ProgressDialog dialog;
        private Context context;
        private int progressMessageId;

        public GetMasterDataTask(Context context, int progressMessageId) {
            this.dialog = new ProgressDialog(context);
            this.context = context;
            this.progressMessageId = progressMessageId;
        }

        public void setGenericDataListener(MasterDataListener<GenericDataItem[]> genericDataListener){
            this.genericDataListener = genericDataListener;
        }

        public void setBankDataListener(MasterDataListener<Bank[]> bankDataListener){
            this.bankDataListener = bankDataListener;
        }

        public void setAccessibleServiceDataListener(MasterDataListener<AccessibleService[]>
                                                             serviceDataListener){
            this.serviceDataListener = serviceDataListener;
        }

        @Override
        protected void onPreExecute() {
//            dialog.setMessage(StringHelper.getStringByResourceId(context, progressMessageId));
//            if (!dialog.isShowing()) {
//                dialog.show();
//            }
        }

        @Override
        protected MasterDataType doInBackground(MasterDataType... params) {
            MasterDataType dataType = params[0];

            if(dataType == MasterDataType.BANK || dataType == MasterDataType.BANK_MA){
                banks = ProxyFactory.getMasterDataProxy().getBanks(dataType);
            } else if(dataType == MasterDataType.ACCESSIBLE_SERVICES){
                accessibleServiceses = ProxyFactory.getMasterDataProxy().getAccessibleServices();
            } else {
                genericDataItemCache.put(dataType, ProxyFactory.getMasterDataProxy().getGenericDataItems(dataType));
            }

            return dataType;
        }

        @Override
        protected void onPostExecute(MasterDataType dataType) {
            if(dataType == MasterDataType.BANK || dataType == MasterDataType.BANK_MA){
                bankDataListener.onMasterData(banks);
            } else if(dataType == MasterDataType.ACCESSIBLE_SERVICES){
                serviceDataListener.onMasterData(accessibleServiceses);
            } else {
                genericDataListener.onMasterData(genericDataItemCache.get(dataType));
            }

//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }
        }
    }
}
