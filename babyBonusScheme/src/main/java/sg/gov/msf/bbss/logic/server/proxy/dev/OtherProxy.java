package sg.gov.msf.bbss.logic.server.proxy.dev;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.connect.HttpJsonCaller;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.server.login.LoginManager;
import sg.gov.msf.bbss.logic.server.proxy.BaseProxy;
import sg.gov.msf.bbss.logic.server.proxy.interfaces.IOtherProxy;
import sg.gov.msf.bbss.logic.server.response.ServerResponse;
import sg.gov.msf.bbss.logic.server.response.ServerResponseType;
import sg.gov.msf.bbss.logic.type.ServiceAppStatusType;
import sg.gov.msf.bbss.logic.type.ServiceAppType;
import sg.gov.msf.bbss.model.entity.ServiceStatus;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.entity.childdata.ChildNric;
import sg.gov.msf.bbss.model.entity.childdata.ChildStatement;
import sg.gov.msf.bbss.model.entity.common.BankAccount;
import sg.gov.msf.bbss.model.entity.common.CashGift;
import sg.gov.msf.bbss.model.entity.common.CdaBankAccount;
import sg.gov.msf.bbss.model.entity.common.ChildCareSubsidy;
import sg.gov.msf.bbss.model.entity.common.ChildDevAccountHistory;
import sg.gov.msf.bbss.model.entity.masterdata.Bank;
import sg.gov.msf.bbss.model.entity.people.Adult;
import sg.gov.msf.bbss.model.entity.people.Child;
import sg.gov.msf.bbss.model.wizardbase.SiblingCheck;
import sg.gov.msf.bbss.model.wizardbase.UpdateProfile;

/**
 * Created by bandaray
 */
public class OtherProxy extends BaseProxy implements IOtherProxy {

    //--- FAMILY VIEW ------------------------------------------------------------------------------

    public ChildItem[] getChildItemList() {
        HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
        ArrayList<ChildItem> childItems = new ArrayList<ChildItem>();

        try {
            String jsonString = httpJsonCaller.get(AppUrls.CHILD_LIST_URL);
            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray(
                    SerializedNames.SEC_CHILD_LIST_ROOT);

            int arrayLength  = jsonArray.length();
            JSONObject jsonItem;
            ChildItem childItem;
            Child child;

            for (int index = 0; index< arrayLength; index ++){
                jsonItem = jsonArray.getJSONObject(index);

                child = new Child();
                child.setId(jsonItem.getString(
                        SerializedNames.SN_PERSON_ID));
                child.setName(jsonItem.getString(
                        SerializedNames.SN_PERSON_NAME));
                child.setNric(jsonItem.getString(
                        SerializedNames.SN_PERSON_NRIC));
                child.setBirthCertNo(jsonItem.getString(
                        SerializedNames.SN_CHILD_BIRTH_CERT_NO));
                child.setDateOfBirth(StringHelper.parseDate(jsonItem.getString(
                        SerializedNames.SN_PERSON_BIRTHDAY)));

                childItem = new ChildItem();
                childItem.setChild(child);

                childItem.setShowChildSec(true);
                childItem.setShowNAH(true);
                childItem.setShowCG(true);
                childItem.setShowCDA(true);
                childItem.setShowGovtMatching(true);
                childItem.setShowChildCareSubsidy(true);

                childItem.setCashGiftAmount(jsonItem.getDouble(
                        SerializedNames.SN_CHILD_ITEM_CG_AMT));
                childItem.setChildCareSubsidyAmount(jsonItem.getDouble(
                        SerializedNames.SN_CHILD_ITEM_CS_AMT));
                childItem.setGovMatchingAmount(jsonItem.getDouble(
                        SerializedNames.SN_CHILD_ITEM_GM_AMT));
                childItem.calculateTotalAmountReceived();

                childItems.add(childItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return childItems.toArray(new ChildItem[childItems.size()]);
    }

    public ChildStatement getChildStatement(String childId){
        HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
        ChildStatement childStatement = null;
        int arrayLength;

        try {
            String url = String.format(AppUrls.CHILD_STATEMENT_URL, childId);
            String jsonString = httpJsonCaller.get(url);
            JSONObject jsonRoot = new JSONObject(jsonString);

            //---CDA TRUSTEE
            JSONObject jsonCdaTrustee = jsonRoot.getJSONObject("cdaTrustee");
            Adult cdaTrustee = new Adult();
            cdaTrustee.setId(jsonCdaTrustee.getString(SerializedNames.SN_PERSON_ID));
            cdaTrustee.setName(jsonCdaTrustee.getString(SerializedNames.SN_PERSON_NAME));
            cdaTrustee.setNric(jsonCdaTrustee.getString(SerializedNames.SN_PERSON_NRIC));
            cdaTrustee.setDateOfBirth(StringHelper.parseDate(jsonCdaTrustee.getString(
                    SerializedNames.SN_PERSON_BIRTHDAY)));

            //---NAH
            JSONObject jsonNaHolder = jsonRoot.getJSONObject("naHolder");
            Adult naHolder = new Adult();
            naHolder.setId(jsonNaHolder.getString(SerializedNames.SN_PERSON_ID));
            naHolder.setNric(jsonNaHolder.getString(SerializedNames.SN_PERSON_NRIC));
            naHolder.setName(jsonNaHolder.getString(SerializedNames.SN_PERSON_NAME));
            naHolder.setDateOfBirth(StringHelper.parseDate(jsonNaHolder.getString(
                    SerializedNames.SN_PERSON_BIRTHDAY)));

            //---CDA BANK ACCOUNT
            JSONObject jsonCda = jsonRoot.getJSONObject("cda");
            CdaBankAccount childDevAccount = new CdaBankAccount();
            childDevAccount.setCapAmount(jsonCda.getDouble(
                    SerializedNames.SN_CDA_CAP_AMT));
            childDevAccount.setRemainingCapAmount(jsonCda.getDouble(
                    SerializedNames.SN_CDA_REMAIN_CAP_AMT));
            childDevAccount.setTotalGovtMatching(jsonCda.getDouble(
                    SerializedNames.SN_CDA_TOT_GOV_MATCH_AMT));
            childDevAccount.setTotalDeposit(jsonCda.getDouble(
                    SerializedNames.SN_CDA_TOT_DEPO_AMT));
            childDevAccount.setExpiryDate(StringHelper.parseDate(jsonCda.getString(
                    SerializedNames.SN_CDA_EXP_DATE)));

            JSONObject jsonCdaBank = jsonCda.getJSONObject("cdaBank");
            childDevAccount.setBankAccountNo(jsonCdaBank.getString(SerializedNames.SN_BANK_ACC_NO));

            Bank cdaBank = new Bank();
            cdaBank.setId(jsonCdaBank.getString(SerializedNames.SN_BANK_ID));
            cdaBank.setName(jsonCdaBank.getString(SerializedNames.SN_BANK_NAME));
            childDevAccount.setBank(cdaBank);

            //--- CDA HISTORY
            JSONArray jsonCdaHistory = jsonCda.getJSONArray("cdaMatchingHistory");
            List<ChildDevAccountHistory> cdaHistories = new ArrayList<ChildDevAccountHistory>();
            arrayLength = jsonCdaHistory.length();
            ChildDevAccountHistory cdaHistory;

            for (int index = 0; index< arrayLength; index ++) {
                JSONObject jsonItem = jsonCdaHistory.getJSONObject(index);
                cdaHistory = new ChildDevAccountHistory();

                cdaHistory.setId(jsonItem.getString(
                        SerializedNames.SN_CDA_HISTORY_ID));
                cdaHistory.setDepositAmount(jsonItem.getDouble(
                        SerializedNames.SN_CDA_HISTORY_DEP_AMT));
                cdaHistory.setDepositDate(StringHelper.parseDate(jsonItem.getString(
                        SerializedNames.SN_CDA_HISTORY_DEP_DATE)));
                cdaHistory.setMatchedAmount(jsonItem.getDouble(
                        SerializedNames.SN_CDA_HISTORY_MATCH_AMT));
                cdaHistory.setMatchedDate(StringHelper.parseDate(jsonItem.getString(
                        SerializedNames.SN_CDA_HISTORY_MATCH_DATE)));

                cdaHistories.add(cdaHistory);
            }

            //---CASH GIFT
            JSONArray jsonCashGift = jsonRoot.getJSONArray("cg");
            List<CashGift> cashGifts = new ArrayList<CashGift>();
            arrayLength = jsonCashGift.length();
            CashGift cashGift;

            for (int index = 0; index< arrayLength; index ++) {
                JSONObject jsonItem = jsonCashGift.getJSONObject(index);
                cashGift = new CashGift();

                cashGift.setId(jsonItem.getString(
                        SerializedNames.SN_CG_ID));
                cashGift.setGiftAmount(jsonItem.getDouble(
                        SerializedNames.SN_CG_AMT));
                cashGift.setPaidDate(StringHelper.parseDate(jsonItem.getString(
                        SerializedNames.SN_CG_PAID_DATE)));
                cashGift.setScheduledDate(StringHelper.parseDate(jsonItem.getString(
                        SerializedNames.SN_CG_SCHEDULED_DATE)));

                jsonItem = jsonItem.getJSONObject("cgBank");
                Bank cgBank = new Bank();
                cgBank.setId(jsonItem.getString(SerializedNames.SN_BANK_ID));
                cgBank.setName(jsonItem.getString(SerializedNames.SN_BANK_NAME));

                BankAccount cgBankAccount = new BankAccount();
                cgBankAccount.setBankAccountNo(jsonCdaBank.getString(SerializedNames.SN_BANK_ACC_NO));
                cgBankAccount.setBank(cgBank);
                cashGift.setBankAccount(cgBankAccount);

                cashGifts.add(cashGift);
            }

            //---CHILD CARE SUBSIDY
            JSONObject jsonChildCareSubsidy = jsonRoot.getJSONObject("childCareSubsidies");
            JSONArray jsonChildCareSubsides = jsonChildCareSubsidy.getJSONArray("organisation");
            List<ChildCareSubsidy> childCareSubsidies = new ArrayList<ChildCareSubsidy>();
            arrayLength = jsonChildCareSubsides.length();
            ChildCareSubsidy childCareSubsidy;

            for (int index = 0; index< arrayLength; index ++) {
                JSONObject jsonItem = jsonChildCareSubsides.getJSONObject(index);
                childCareSubsidy = new ChildCareSubsidy();

                childCareSubsidy.setId(jsonItem.getString(SerializedNames.SN_CC_SUBSIDY_ID));
                childCareSubsidy.setName(jsonItem.getString(SerializedNames.SN_CC_SUBSIDY_NAME).
                        toUpperCase(Locale.ENGLISH));
                childCareSubsidy.setMonth(jsonItem.getString(SerializedNames.SN_CC_SUBSIDY_MONTH));
                childCareSubsidy.setAmount(jsonItem.getDouble(SerializedNames.SN_CC_SUBSIDY_AMOUNT));

                childCareSubsidies.add(childCareSubsidy);
            }

            //---CHILD STATEMENT
            childStatement = new ChildStatement();
            childStatement.setChildCareSubsidyAmt(jsonChildCareSubsidy.getDouble(
                    SerializedNames.SN_CC_SUBSIDY_TOT_AMOUNT));
            childStatement.setNominatedAccountHolder(naHolder);
            childStatement.setChildDevAccountTrustee(cdaTrustee);
            childStatement.setCdaBankAccount(childDevAccount);
            childStatement.setCashGiftList(cashGifts);
            childStatement.setChildCareSubsidies(childCareSubsidies);
            childStatement.setChildDevAccountHistories(cdaHistories);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return childStatement;
    }

    //--- UPDATE PROFILE ---------------------------------------------------------------------------

    public UpdateProfile getUserProfile(){
        HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
        UpdateProfile updateProfile = new UpdateProfile();
        Adult adult = new Adult();

        try {
            String jsonString  = httpJsonCaller.get(AppUrls.GET_USER_PROFILE);
            JSONObject jsonAdult = new JSONObject(jsonString).getJSONObject("userDetail");

            adult = Adult.deserialize(jsonAdult, true);
        } catch (Exception e){
            e.printStackTrace();
        }

        updateProfile.setUserDetail(adult);

        return updateProfile;
    }

    @Override
    public ServerResponse updateUserProfile(UpdateProfile updateProfile) {
        //Change update profile - Success
        //testOutputJsonString = "{\"status\":{\"code\":\"200\",\"messages\":\"Profile updated successfully\"}}";
        //Change update profile - Error
        //testOutputJsonString = "{\"status\":{\"code\":\"900005\",\"messages\":\"Updated profile failed\"},\"data\":{\"application\":{\"userDetail\":{\"commType\":\"Invalid Communication Type\",\"mobileNo\":\"Invalid Mobile no\",\"email\":\"Invalid Email\"}}}}";

        // Change update profile - Success
        testOutputJsonString = "{\"status\":{\"code\":\"200\",\"messages\":\"User profile is updated successfully\"},\"data\":{\"userDetail\":{}}}";
        //Change update profile - Error
        //testOutputJsonString = "{"status":{"code":"417","messages":"Failed to update profile"},"data":{"userDetail":{"mobileNo":"Mobile Number is mandatory."}}}";

        ServerResponse serverResponse = new ServerResponse();

        try {
            Adult user = updateProfile.getUserDetail();
            JSONObject jsonUser = Adult.serialize(user, true);

            JSONObject jsonFinal = new JSONObject();
            jsonFinal.put(SerializedNames.SEC_HOME_UPDATE_PROFILE, jsonUser);

            serverResponse = post(AppUrls.NAH_UPDATE_URL, jsonFinal);
            JSONObject jsonDataResponse = serverResponse.getJsonDataResponse();

            if (serverResponse.getResponseType() != ServerResponseType.SUCCESS) {
                if(jsonDataResponse != null){
                    JSONObject jsonUpdateProfile = jsonDataResponse.getJSONObject("application");
                    String[] serialNames = new String []{
                            SerializedNames.SN_ADULT_MODE_OF_COMM,
                            SerializedNames.SN_ADULT_MOBILE,
                            SerializedNames.SN_ADULT_EMAIL
                    };

                    addValidationInfo(SerializedNames.SEC_HOME_UPDATE_PROFILE, serialNames, jsonUpdateProfile, updateProfile, AppConstants.EMPTY_STRING);
                }
            }
        } catch (Exception e) {
            serverResponse.setResponseType(ServerResponseType.APPLICATION_ERROR);
            serverResponse.setMessage(e.getMessage());
        }

        return serverResponse;
    }

    //--- SIBLING CHECK ---------------------------------------------------------------------------

    @Override
    public ServerResponse checkSiblingHood(SiblingCheck siblingCheck){
        // Change update profile - Success
        testOutputJsonString = "{\"status\":{\"code\":\"200\",\"messages\":\"Check sibling successfully\"},\"data\":{\"siblingCheckStatus\":{\"isSibling\":true,\"message\":\"Specified siblings are matching.\"}}}";
        //Change update profile - Error
        //testOutputJsonString = "{\"status\":{\"code\":\"200\",\"messages\":\"Check sibling successfully\"},\"data\":{\"siblingCheckStatus\":{\"isSibling\":false,\"message\":\"Child Ty and child ttt is not sibling.\"}}}";

        ServerResponse updateResponse = new ServerResponse();

        try {
            JSONObject jsonSiblings = new JSONObject();

            ChildNric childNric = siblingCheck.getChildNric();

            jsonSiblings.put(SerializedNames.SN_CHILD_NRIC1, childNric.getNric1());
            jsonSiblings.put(SerializedNames.SN_CHILD_NRIC2, childNric.getNric2());

            JSONObject jsonFinal = new JSONObject();
            jsonFinal.put(SerializedNames.SEC_HOME_SIBLING_CHECK_DEV, jsonSiblings);

            HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
            String jsonString = httpJsonCaller.post(AppUrls.CHECK_SIBLINGHOOD,
                    jsonFinal.toString());
            JSONObject jsonResponse = new JSONObject(jsonString).getJSONObject(
                    SerializedNames.SEC_RESPONSE_DATA).getJSONObject(
                    SerializedNames.SEC_HOME_SIBLING_CHECK_DEV);

            updateResponse.setResponseType(ServerResponseType.SUCCESS);
            updateResponse.setAppId(jsonResponse.getString(SerializedNames.SEC_RESPONSE_SERVICE_APP_ID));
            updateResponse.setMessage(jsonResponse.getString(SerializedNames.SEC_RESPONSE_MESSAGE));

        } catch (JSONException e) {
            updateResponse.setResponseType(ServerResponseType.APPLICATION_ERROR);
            updateResponse.setMessage(e.getMessage());
        } catch (Exception e) {
            updateResponse.setResponseType(ServerResponseType.SERVICE_ERROR);
            updateResponse.setMessage(e.getMessage());
        }

        return updateResponse;
    }

    //--- SERVICE STATUS ---------------------------------------------------------------------------

    public ServiceStatus[] getServiceAppStatuses() {
        HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
        ArrayList<ServiceStatus> serviceStatuses = new ArrayList<ServiceStatus>();

        try {
            String jsonString = httpJsonCaller.get(AppUrls.GET_SERVICE_STATUS);
            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray(
                    SerializedNames.SEC_SERVICE_APP_STATUS_ROOT);

            int arrayLength  = jsonArray.length();
            JSONObject jsonItem;
            ServiceStatus serviceStatusItem;

            for (int index = 0; index< arrayLength; index ++){
                jsonItem = jsonArray.getJSONObject(index);

                serviceStatusItem = new ServiceStatus();
                serviceStatusItem.setAppId(jsonItem.getString(SerializedNames.SN_SERVICE_APP_ID));
                serviceStatusItem.setAppType(ServiceAppType.parseTypeDev(
                        jsonItem.getString(SerializedNames.SN_SERVICE_APP_TYPE)));
                serviceStatusItem.setAppStatusType(ServiceAppStatusType.parseType(
                        jsonItem.getString("serviceStatus")));
                serviceStatusItem.setAppDate(StringHelper.parseDate(
                        jsonItem.getString(SerializedNames.SN_SERVICE_APP_DATE)));

                serviceStatuses.add(serviceStatusItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceStatuses.toArray(new ServiceStatus[serviceStatuses.size()]);
    }
}
