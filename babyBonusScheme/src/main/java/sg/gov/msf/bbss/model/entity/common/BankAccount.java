package sg.gov.msf.bbss.model.entity.common;

import org.json.JSONException;
import org.json.JSONObject;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.annotation.DisplayNameId;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.model.entity.masterdata.Bank;

/**
 * Created by bandaray
 */
public class BankAccount {

    public static final String FIELD_BANK_ACCOUNT_ID = "id";
    public static final String FIELD_BANK = "bank";
    public static final String FIELD_BANK_BRANCH = "bankBranch";
    public static final String FIELD_BANK_ACCOUNT = "bankAccountNo";
    public static final String FIELD_BANK_TC_URL = "bankTCUrl";

    /*
     * WARNING : If you refactor any of the property names below,
     *           you have to rename the above constant values as well.
     */

    public static final String[] SERIAL_NAMES = new String []{
            SerializedNames.SN_BANK_ID,
            SerializedNames.SN_BRANCH_ID,
            SerializedNames.SN_BANK_ACC_NO
    };


    @DisplayNameId(R.string.label_common_id)
    private String id;

    @DisplayNameId(R.string.label_bank_name)
    private Bank bank;

    @DisplayNameId(R.string.label_bank_branch)
    private String bankBranchId;

    @DisplayNameId(R.string.label_bank_branch)
    private String bankBranch;

    @DisplayNameId(R.string.label_bank_account)
    private String bankAccountNo;

//    @SerializedName("tcUrl")
//    @DisplayNameId(R.string.label_bank_tc_url)
//    private String bankTCUrl;


    //-------------------------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public Bank getBank() {
        return bank;
    }

    public String getBankBranchId() {
        return bankBranchId;
    }

    public void setBankBranchId(String bankBranchId) {
        this.bankBranchId = bankBranchId;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    public String getBankAccountNo() {
        return bankAccountNo;
    }

    public void setBankAccountNo(String bankAccountNo) {
        this.bankAccountNo = bankAccountNo;
    }

    public static JSONObject serialize(BankAccount bankAccount) throws JSONException {
        JSONObject jsonBankAccount = new JSONObject();

        jsonBankAccount.put(SerializedNames.SN_BANK_ID, bankAccount.getBank().getId());
        jsonBankAccount.put(SerializedNames.SN_BRANCH_ID, bankAccount.getBankBranch());
        jsonBankAccount.put(SerializedNames.SN_BANK_ACC_NO, bankAccount.getBankAccountNo());

        return jsonBankAccount;
    }
}
