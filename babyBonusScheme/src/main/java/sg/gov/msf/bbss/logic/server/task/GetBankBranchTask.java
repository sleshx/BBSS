package sg.gov.msf.bbss.logic.server.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import sg.gov.msf.bbss.logic.masterdata.MasterDataListener;
import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.model.entity.common.Address;
import sg.gov.msf.bbss.model.entity.common.BankAccount;
import sg.gov.msf.bbss.model.entity.masterdata.Bank;

/**
 * Created by bandaray
 */
public class GetBankBranchTask extends AsyncTask<String, Void, BankAccount> {
    private ProgressDialog dialog;
    private BankAccount bankAccount;
    private MasterDataListener<BankAccount> branchMasterDataListener;

    public GetBankBranchTask(Context context, BankAccount bankAccount,
                             MasterDataListener<BankAccount> branchMasterDataListener) {
        this.bankAccount = bankAccount;
        this.branchMasterDataListener = branchMasterDataListener;
        this.dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
//        dialog.setMessage("Requesting bank branch details");
//        if (!dialog.isShowing()) {
//            dialog.show();
//        }
    }


    @Override
    protected BankAccount doInBackground(String... params) {
        return ProxyFactory.getMasterDataProxy().getBankAccountBranch(bankAccount.getBank(), bankAccount.getBankAccountNo());
    }

    @Override
    protected void onPostExecute(BankAccount account) {
        branchMasterDataListener.onMasterData(account);
//
//        if (dialog.isShowing()) {
//            dialog.dismiss();
//        }
    }
}
