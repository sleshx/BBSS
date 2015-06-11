package sg.gov.msf.bbss.logic.server.proxy.dep;

import org.json.JSONArray;
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

    @Override
    public ChildItem[] getChildItemList() {
        String user = LoginManager.getSessionContainer().getNric();
        String url = String.format(
                sg.gov.msf.bbss.logic.server.proxy.dep.AppUrls.HOME_FAMILYVIEW_CHILD_LIST, user);

        HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
        ArrayList<ChildItem> childItems = new ArrayList<ChildItem>();

        try {
            String jsonString = httpJsonCaller.get(url);
            JSONArray jsonArray = new JSONArray(jsonString);

            int arrayLength  = jsonArray.length();
            JSONObject jsonItem = null;
            ChildItem childItem = null;
            Child child = null;

            for (int index = 0; index< arrayLength; index ++){
                jsonItem = jsonArray.getJSONObject(index);

                child = new Child();
                child.setId(jsonItem.optString(SerializedNames.SN_PERSON_ID));
                child.setName(jsonItem.optString(SerializedNames.SN_PERSON_NAME));
                child.setNric(jsonItem.optString(SerializedNames.SN_PERSON_NRIC));
                child.setBirthCertNo(jsonItem.optString(SerializedNames.SN_CHILD_BIRTH_CERT_NO));
                child.setDateOfBirth(StringHelper.parseDate(jsonItem.optString(SerializedNames.SN_PERSON_BIRTHDAY)));

                childItem = new ChildItem();
                childItem.setChild(child);

                childItem.setShowChildSec(jsonItem.getBoolean(SerializedNames.SN_CHILD_ITEM_SHOW_CHILD));
                childItem.setShowNAH(jsonItem.getBoolean(SerializedNames.SN_CHILD_ITEM_SHOW_NAH));
                childItem.setShowCG(jsonItem.getBoolean(SerializedNames.SN_CHILD_ITEM_SHOW_CG));
                childItem.setShowCDA(jsonItem.getBoolean(SerializedNames.SN_CHILD_ITEM_SHOW_CDA));
                childItem.setShowGovtMatching(jsonItem.getBoolean(SerializedNames.SN_CHILD_ITEM_SHOW_GOV_MATCH));
                childItem.setShowChildCareSubsidy(jsonItem.getBoolean(SerializedNames.SN_CHILD_ITEM_SHOW_SUBSIDY));

                childItem.setCashGiftAmount(jsonItem.getDouble(SerializedNames.SN_CHILD_ITEM_CG_AMT));
                childItem.setChildCareSubsidyAmount(jsonItem.getDouble(SerializedNames.SN_CHILD_ITEM_CS_AMT));
                childItem.setGovMatchingAmount(jsonItem.getDouble(SerializedNames.SN_CHILD_ITEM_GM_AMT));
                childItem.calculateTotalAmountReceived();

                childItems.add(childItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return childItems.toArray(new ChildItem[0]);
    }

    @Override
    public ChildStatement getChildStatement(String childId){
        String user = LoginManager.getSessionContainer().getNric();
        String url = String.format(
                sg.gov.msf.bbss.logic.server.proxy.dep.AppUrls.HOME_FAMILYVIEW_CHILD_STATEMENT,
                user, childId);

        HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
        ChildStatement childStatement = null;

        try {
            String jsonString = httpJsonCaller.get(url);
            JSONObject jsonRoot = new JSONObject(jsonString);

            //---CDA TRUSTEE
            JSONObject jsonCdaTrustee = jsonRoot.getJSONObject(SerializedNames.SEC_HOME_FAMILY_VIEW_CDAT_PARTICULARS);
            Adult cdaTrustee = new Adult();
            cdaTrustee.setId(jsonCdaTrustee.optString(SerializedNames.SN_PERSON_ID));
            cdaTrustee.setName(jsonCdaTrustee.optString(SerializedNames.SN_PERSON_NAME));
            cdaTrustee.setNric(jsonCdaTrustee.optString(SerializedNames.SN_PERSON_NRIC));
            cdaTrustee.setDateOfBirth(StringHelper.parseDate(jsonCdaTrustee.optString(
                    SerializedNames.SN_PERSON_BIRTHDAY)));

            //---NAH
            JSONObject jsonNaHolder = jsonRoot.getJSONObject(SerializedNames.SEC_HOME_FAMILY_VIEW_NAH_PARTICULARS);
            Adult naHolder = new Adult();
            naHolder.setId(jsonNaHolder.optString(SerializedNames.SN_PERSON_ID));
            naHolder.setNric(jsonNaHolder.optString(SerializedNames.SN_PERSON_NRIC));
            naHolder.setName(jsonNaHolder.optString(SerializedNames.SN_PERSON_NAME));
            naHolder.setDateOfBirth(StringHelper.parseDate(jsonNaHolder.optString(
                    SerializedNames.SN_PERSON_BIRTHDAY)));

            //---CDA BANK ACCOUNT
            JSONObject jsonCda = jsonRoot.getJSONObject(SerializedNames.SEC_HOME_FAMILY_VIEW_CDA);

            JSONObject jsonCdaBank = jsonCda.getJSONObject(SerializedNames.SEC_HOME_FAMILY_VIEW_CDA_BANK);
            Bank cdaBank = new Bank();
            cdaBank.setId(jsonCdaBank.optString(SerializedNames.SN_BANK_ID));
            cdaBank.setName(jsonCdaBank.optString(SerializedNames.SN_BANK_NAME));

            CdaBankAccount childDevAccount = new CdaBankAccount();
            childDevAccount.setCapAmount(jsonCda.getDouble(
                    SerializedNames.SN_CDA_CAP_AMT));
            childDevAccount.setRemainingCapAmount(jsonCda.getDouble(
                    SerializedNames.SN_CDA_REMAIN_CAP_AMT));
            childDevAccount.setTotalGovtMatching(jsonCda.getDouble(
                    SerializedNames.SN_CDA_TOT_GOV_MATCH_AMT));
            childDevAccount.setTotalDeposit(jsonCda.getDouble(
                    SerializedNames.SN_CDA_TOT_DEPO_AMT));
            childDevAccount.setExpiryDate(StringHelper.parseDate(jsonCda.optString(
                    SerializedNames.SN_CDA_EXP_DATE)));
            childDevAccount.setBankAccountNo(jsonCdaBank.optString(SerializedNames.SN_BANK_ACC_NO));
            childDevAccount.setBank(cdaBank);

            //--- CDA HISTORY
            JSONArray jsonCdaHistory = jsonCda.getJSONArray(SerializedNames.SEC_HOME_FAMILY_VIEW_CDA_MATCH_HISTORY);
            List<ChildDevAccountHistory> cdaHistories = new ArrayList<ChildDevAccountHistory>();
            ChildDevAccountHistory cdaHistory;

            for (int index = 0; index < jsonCdaHistory.length(); index ++) {
                JSONObject jsonItem = jsonCdaHistory.getJSONObject(index);
                cdaHistory = new ChildDevAccountHistory();

                cdaHistory.setId(jsonItem.optString(
                        SerializedNames.SN_CDA_HISTORY_ID));
                cdaHistory.setDepositAmount(jsonItem.getDouble(
                        SerializedNames.SN_CDA_HISTORY_DEP_AMT));
                cdaHistory.setDepositDate(StringHelper.parseDate(jsonItem.optString(
                        SerializedNames.SN_CDA_HISTORY_DEP_DATE)));
                cdaHistory.setMatchedAmount(jsonItem.getDouble(
                        SerializedNames.SN_CDA_HISTORY_MATCH_AMT));
                cdaHistory.setMatchedDate(StringHelper.parseDate(jsonItem.optString(
                        SerializedNames.SN_CDA_HISTORY_MATCH_DATE)));

                cdaHistories.add(cdaHistory);
            }

            //---CASH GIFT
            JSONArray jsonCashGift = jsonRoot.getJSONArray(SerializedNames.SEC_HOME_FAMILY_VIEW_CASH_GIFT);
            List<CashGift> cashGifts = new ArrayList<CashGift>();
            CashGift cashGift;

            for (int index = 0; index < jsonCashGift.length(); index ++) {
                JSONObject jsonItem = jsonCashGift.getJSONObject(index);

                JSONObject jsonCgBank = jsonItem.getJSONObject(SerializedNames.SEC_HOME_FAMILY_VIEW_CASH_GIFT_BANK);
                Bank cgBank = new Bank();
                cgBank.setId(jsonCgBank.optString(SerializedNames.SN_BANK_ID));
                cgBank.setName(jsonCgBank.optString(SerializedNames.SN_BANK_NAME));

                BankAccount cgBankAccount = new BankAccount();
                cgBankAccount.setBankAccountNo(jsonCgBank.optString(SerializedNames.SN_BANK_ACC_NO));
                cgBankAccount.setBank(cgBank);

                cashGift = new CashGift();

                cashGift.setId(jsonItem.optString(
                        SerializedNames.SN_CG_ID));
                cashGift.setGiftAmount(jsonItem.getDouble(
                        SerializedNames.SN_CG_AMT));
                cashGift.setPaidDate(StringHelper.parseDate(jsonItem.optString(
                        SerializedNames.SN_CG_PAID_DATE)));
                cashGift.setScheduledDate(StringHelper.parseDate(jsonItem.optString(
                        SerializedNames.SN_CG_SCHEDULED_DATE)));
                cashGift.setBankAccount(cgBankAccount);

                cashGifts.add(cashGift);
            }

            //---CHILD CARE SUBSIDY
            JSONObject jsonChildCareSubsidy = jsonRoot.getJSONObject(SerializedNames.SEC_HOME_FAMILY_VIEW_CHILD_CARE_SUBSIDY);
            JSONArray jsonChildCareSubsides = jsonChildCareSubsidy.getJSONArray(SerializedNames.SEC_HOME_FAMILY_VIEW_CHILD_CARE_SUBSIDY_ORG);
            List<ChildCareSubsidy> childCareSubsidies = new ArrayList<ChildCareSubsidy>();
            ChildCareSubsidy childCareSubsidy;

            for (int index = 0; index< jsonChildCareSubsides.length(); index ++) {
                JSONObject jsonItem = jsonChildCareSubsides.getJSONObject(index);
                childCareSubsidy = new ChildCareSubsidy();

                childCareSubsidy.setId(jsonItem.optString(SerializedNames.SN_CC_SUBSIDY_ID));
                childCareSubsidy.setName(jsonItem.optString(SerializedNames.SN_CC_SUBSIDY_NAME).
                        toUpperCase(Locale.ENGLISH));
                childCareSubsidy.setMonth(jsonItem.optString(SerializedNames.SN_CC_SUBSIDY_MONTH));
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

    @Override
    public UpdateProfile getUserProfile(){
        String user = LoginManager.getSessionContainer().getNric();
        String url = String.format(
                sg.gov.msf.bbss.logic.server.proxy.dep.AppUrls.HOME_GET_USER_PROFILE, user);

        HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
        UpdateProfile updateProfile = new UpdateProfile();
        Adult adult = new Adult();

        try {
            String jsonString  = httpJsonCaller.get(url);
            JSONObject jsonAdult = new JSONObject(jsonString);

            adult = Adult.deserialize(jsonAdult, true);

        } catch (Exception e){
            e.printStackTrace();
        }

        updateProfile.setUserDetail(adult);

        return updateProfile;
    }

    @Override
    public ServerResponse updateUserProfile(UpdateProfile updateProfile) {
        String user = LoginManager.getSessionContainer().getNric();
        String url = AppUrls.HOME_UPDATE_USER_PROFILE;

        ServerResponse serverResponse = new ServerResponse();

        try {
            Adult profileUser = updateProfile.getUserDetail();
            JSONObject jsonUser = new JSONObject();

            jsonUser.put(SerializedNames.SN_USER_ID, user);
            jsonUser.put(SerializedNames.SN_ADULT_MODE_OF_COMM, profileUser.getModeOfCommunication().getCode());
            jsonUser.put(SerializedNames.SN_ADULT_MOBILE, profileUser.getMobileNumber());
            jsonUser.put(SerializedNames.SN_ADULT_EMAIL, profileUser.getEmailAddress());

            serverResponse = post(url, jsonUser);
            JSONObject jsonDataResponse = serverResponse.getJsonDataResponse();

            if (serverResponse.getResponseType() != ServerResponseType.SUCCESS) {
                if(jsonDataResponse != null){
                    String[] serialNames = new String []{
                            SerializedNames.SN_ADULT_MODE_OF_COMM,
                            SerializedNames.SN_ADULT_MOBILE,
                            SerializedNames.SN_ADULT_EMAIL
                    };

                    addValidationInfo(SerializedNames.SEC_HOME_UPDATE_PROFILE, serialNames,
                            jsonDataResponse, updateProfile, AppConstants.EMPTY_STRING);
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
        String user = LoginManager.getSessionContainer().getNric();
        String url = AppUrls.HOME_CHECK_SIBLING_HOOD;

        ServerResponse serverResponse = new ServerResponse();

        try {
            JSONObject jsonSiblings = new JSONObject();

            jsonSiblings.put(SerializedNames.SN_CHILD_NRIC1,
                    StringHelper.getCapitalizedNric(siblingCheck.getChildNric().getNric1()));
            jsonSiblings.put(SerializedNames.SN_CHILD_NRIC2,
                    StringHelper.getCapitalizedNric(siblingCheck.getChildNric().getNric2()));

            JSONObject jsonFinal = new JSONObject();
            jsonFinal.put(SerializedNames.SN_USER_ID, user);
            jsonFinal.put(SerializedNames.SEC_HOME_SIBLING_CHECK_DEV, jsonSiblings);

            serverResponse = post(url, jsonFinal);

            if (serverResponse.getResponseType() == ServerResponseType.SUCCESS) {
                JSONObject jsonSiblingCheck = serverResponse.getJsonDataResponse().getJSONObject(SerializedNames.SEC_HOME_SIBLING_CHECK);
                serverResponse.setResponseType(ServerResponseType.SUCCESS);
                serverResponse.setMessage(jsonSiblingCheck.getString(SerializedNames.SEC_RESPONSE_MESSAGE));
            }

        } catch (Exception e) {
            serverResponse.setResponseType(ServerResponseType.APPLICATION_ERROR);
            serverResponse.setMessage(e.getMessage());
        }

        return serverResponse;
    }

    //--- SERVICE STATUS ---------------------------------------------------------------------------

    @Override
    public ServiceStatus[] getServiceAppStatuses() {
        String user = LoginManager.getSessionContainer().getNric();
        String url = String.format(AppUrls.SERVICES_STATUS, user);

        HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
        ArrayList<ServiceStatus> serviceStatuses = new ArrayList<ServiceStatus>();

        try {
            String jsonString = httpJsonCaller.get(url);
            JSONArray jsonArray = new JSONArray(jsonString);

            int arrayLength  = jsonArray.length();
            JSONObject jsonItem = null;
            ServiceStatus serviceStatusItem = null;

            for (int index = 0; index< arrayLength; index ++){
                jsonItem = jsonArray.getJSONObject(index);

                serviceStatusItem = new ServiceStatus();
                serviceStatusItem.setAppId(jsonItem.optString(SerializedNames.SN_SERVICE_APP_ID));
                serviceStatusItem.setAppType(ServiceAppType.parseType(
                        jsonItem.optString(SerializedNames.SN_SERVICE_APP_TYPE)));
                serviceStatusItem.setAppStatusType(ServiceAppStatusType.parseType(
                        jsonItem.optString(SerializedNames.SN_SERVICE_APP_STATUS)));
                serviceStatusItem.setAppDate(StringHelper.parseDate(
                        jsonItem.optString(SerializedNames.SN_SERVICE_APP_DATE)));

                serviceStatuses.add(serviceStatusItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceStatuses.toArray(new ServiceStatus[0]);
    }
}
