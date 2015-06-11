package sg.gov.msf.bbss.logic.server.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import sg.gov.msf.bbss.logic.masterdata.MasterDataListener;
import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.model.entity.common.Address;

/**
 * Created by bandaray
 */
public class GetLocalAddressTask extends AsyncTask<Integer, Void, Address> {
    private ProgressDialog dialog;
    private Context context;
    private MasterDataListener<Address> addressDataListener;

    public GetLocalAddressTask(Context context, MasterDataListener<Address> addressDataListener) {
        this.context = context;
        this.addressDataListener = addressDataListener;
        this.dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Requesting address details");
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    protected Address doInBackground(Integer... params) {
        return ProxyFactory.getMasterDataProxy().getLocalAddress(params[0]);
    }

    @Override
    protected void onPostExecute(Address address) {
        addressDataListener.onMasterData(address);

        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
