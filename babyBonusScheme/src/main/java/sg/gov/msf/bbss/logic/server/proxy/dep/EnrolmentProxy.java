package sg.gov.msf.bbss.logic.server.proxy.dep;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import sg.gov.msf.bbss.apputils.connect.HttpJsonCaller;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.server.login.LoginManager;
import sg.gov.msf.bbss.logic.server.proxy.dev.AppUrls;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.server.proxy.interfaces.IEnrolmentProxy;
import sg.gov.msf.bbss.logic.server.response.ServerResponse;
import sg.gov.msf.bbss.logic.server.response.ServerResponseType;
import sg.gov.msf.bbss.logic.type.AddressType;
import sg.gov.msf.bbss.logic.type.ChildDeclarationType;
import sg.gov.msf.bbss.logic.type.ChildRegistrationType;
import sg.gov.msf.bbss.logic.type.EnrolmentAppType;
import sg.gov.msf.bbss.logic.type.LoginUserType;
import sg.gov.msf.bbss.logic.type.RelationshipType;
import sg.gov.msf.bbss.logic.type.SubmitType;
import sg.gov.msf.bbss.logic.type.YesNoType;
import sg.gov.msf.bbss.model.entity.EnrolmentStatus;
import sg.gov.msf.bbss.model.entity.childdata.ChildDeclaration;
import sg.gov.msf.bbss.model.entity.childdata.ChildRegistration;
import sg.gov.msf.bbss.model.entity.common.Address;
import sg.gov.msf.bbss.model.entity.common.BankAccount;
import sg.gov.msf.bbss.model.entity.common.CdaBankAccount;
import sg.gov.msf.bbss.model.entity.common.SupportingFile;
import sg.gov.msf.bbss.model.entity.masterdata.Bank;
import sg.gov.msf.bbss.model.entity.masterdata.GenericDataItem;
import sg.gov.msf.bbss.model.entity.people.Adult;
import sg.gov.msf.bbss.model.entity.people.Child;
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentChildStatus;
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentForm;
import sg.gov.msf.bbss.model.wizardbase.enrolment.EnrolmentFormStatus;

/**
 * Created by bandaray
 */
public class EnrolmentProxy implements IEnrolmentProxy {

    @Override
    public EnrolmentForm getPrePopulatedApplication(EnrolmentAppType enrolmentAppType){
        HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
        EnrolmentForm enrolmentForm = null;

        try {
            String jsonString = httpJsonCaller.get(AppUrls.GET_ENROLMENT_PRE_POPULATED_DATA);
            JSONObject jsonRoot = new JSONObject(jsonString).getJSONObject(SerializedNames.SEC_ENROLMENT_ROOT);

            JSONObject jsonEnrolChild = jsonRoot.getJSONObject(SerializedNames.SEC_ENROLMENT_ENROL_CHILD);
            JSONArray jsonChildren = jsonEnrolChild.getJSONArray(SerializedNames.SEC_ENROLMENT_CHILD_DETAILS);
            ArrayList<Child> children = new ArrayList<Child>();

            int arrayLength = jsonChildren.length();
            JSONObject jsonChild = null;
            Child child = null;

            for (int index = 0; index < arrayLength; index++) {
                jsonChild = jsonChildren.getJSONObject(index);
                child = new Child();
                child.setNric(jsonChild.getString(SerializedNames.SN_PERSON_NRIC));
                child.setId(jsonChild.getString(SerializedNames.SN_PERSON_ID));
                child.setDateOfBirth(StringHelper.parseDate(jsonChild.getString(SerializedNames.SN_PERSON_BIRTHDAY)));
                child.setBornOnOverseas(YesNoType.parseType(jsonChild.getString(SerializedNames.SN_CHILD_IS_BORN_OVERSEAS)));
                child.setBirthCertNo(jsonChild.getString(SerializedNames.SN_CHILD_BIRTH_CERT_NO));
                child.setName(jsonChild.getString(SerializedNames.SN_PERSON_NAME));

                children.add(child);
            }

            ChildRegistration childRegistration = new ChildRegistration();
            childRegistration.setRegistrationType(ChildRegistrationType.parseType(jsonEnrolChild.getString(SerializedNames.SN_CHILD_REG_TYPE)));
            childRegistration.setChildren(children);

            enrolmentForm = new EnrolmentForm(true, EnrolmentAppType.NEW);
            enrolmentForm.setFather(deserializeEnrolmentAdult(jsonRoot.getJSONObject(SerializedNames.SEC_ENROLMENT_FATHER_PARTICULARS), LoginUserType.PARENT));
            enrolmentForm.setMother(deserializeEnrolmentAdult(jsonRoot.getJSONObject(SerializedNames.SEC_ENROLMENT_MOTHER_PARTICULARS), LoginUserType.PARENT));
            enrolmentForm.setChildRegistration(childRegistration);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enrolmentForm;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public EnrolmentForm getSavedApplication(EnrolmentAppType enrolmentAppType){
        HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
        EnrolmentForm enrolmentForm = null;

        try {
            String jsonString = httpJsonCaller.get(AppUrls.GET_ENROLMENT_SAVED_DATA);
            JSONObject jsonRoot = new JSONObject(jsonString).getJSONObject(SerializedNames.SEC_ENROLMENT_ROOT);

            JSONObject jsonCgAuthorizer = jsonRoot.getJSONObject(SerializedNames.SEC_ENROLMENT_CASH_GIFT_ACCOUNT);
            JSONObject declaration = jsonRoot.getJSONObject(SerializedNames.SEC_ENROLMENT_DECLARE);

            Bank cgBank = new Bank();
            cgBank.setId(jsonCgAuthorizer.getString(SerializedNames.SN_BANK_ID));
            //cgBank.setName(jsonCgAuthorizer.getString(SerializedNames.SN_BANK_NAME));

            BankAccount cgAccountBankAccount = new BankAccount();
            cgAccountBankAccount.setBank(cgBank);
            //cgAccountBankAccount.setBankBranch(jsonCgAuthorizer.getString("branchName"));
            cgAccountBankAccount.setBankAccountNo(jsonCgAuthorizer.getString(SerializedNames.SN_BANK_ACC_NO));

            JSONObject jsonCda = jsonRoot.getJSONObject(SerializedNames.SEC_ENROLMENT_CHILD_DEV_ACCOUNT);
            JSONObject jsonCdaBank = jsonCda.getJSONObject("cdaBank");
            Bank cdaBank = new Bank();
            cdaBank.setId(jsonCdaBank.getString(SerializedNames.SN_BANK_ID));
            cdaBank.setName(jsonCdaBank.getString(SerializedNames.SN_BANK_NAME));

            CdaBankAccount cdaBankAccount = new CdaBankAccount();
            cdaBankAccount.setNetsCardName(jsonCda.getString(SerializedNames.SN_CDAB_NETS_CARD_NAME));
            cdaBankAccount.setBank(cdaBank);

            enrolmentForm = new EnrolmentForm(true, enrolmentAppType);
            enrolmentForm.setFather(deserializeEnrolmentAdult(jsonRoot.getJSONObject(SerializedNames.SEC_ENROLMENT_FATHER_PARTICULARS), LoginUserType.PARENT));
            enrolmentForm.setMother(deserializeEnrolmentAdult(jsonRoot.getJSONObject(SerializedNames.SEC_ENROLMENT_MOTHER_PARTICULARS), LoginUserType.PARENT));
            enrolmentForm.setChildRegistration(deserializeEnrolChild(jsonRoot.getJSONObject(SerializedNames.SEC_ENROLMENT_ENROL_CHILD)));
            enrolmentForm.setNaHolder(deserializeEnrolmentAdult(jsonRoot.getJSONObject(SerializedNames.SEC_ENROLMENT_NAH_PARTICULARS), LoginUserType.NOMINATED_ACCOUNT_HOLDER));
            enrolmentForm.setCdaTrustee(deserializeEnrolmentAdult(jsonRoot.getJSONObject(SerializedNames.SEC_ENROLMENT_NAH_PARTICULARS), LoginUserType.CDA_TRUSTEE));
            enrolmentForm.setCashGiftBankAccount(cgAccountBankAccount);
            enrolmentForm.setCdaBankAccount(cdaBankAccount);
            enrolmentForm.setChildDeclarations(deserializeChildDeclarations(jsonRoot.getJSONArray(SerializedNames.SEC_ENROLMENT_MOTHER_DECLARE)));
            enrolmentForm.setDeclare1(declaration.getBoolean(SerializedNames.SN_ENROLMENT_IS_IS_DECLARE1));
            enrolmentForm.setDeclare2(declaration.getBoolean(SerializedNames.SN_ENROLMENT_IS_IS_DECLARE2));
            enrolmentForm.setDeclare3(declaration.getBoolean(SerializedNames.SN_ENROLMENT_IS_IS_DECLARE3));
            enrolmentForm.setDeclare4(declaration.getBoolean(SerializedNames.SN_ENROLMENT_IS_IS_DECLARE4));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return enrolmentForm;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public ServerResponse updateEnrolmentApplication(EnrolmentForm enrolmentForm, SubmitType submitType){
        ServerResponse updateResponse = new ServerResponse();
        JSONObject jsonEnrolDetail = new JSONObject();

        try{
            jsonEnrolDetail.put(SerializedNames.SN_ENROLMENT_APP_ID, null);
            jsonEnrolDetail.put(SerializedNames.SN_ENROLMENT_SUBMIT_TYPE, submitType.getCode());
            jsonEnrolDetail.put(SerializedNames.SN_ENROLMENT_IS_PRE_POPULATED, enrolmentForm.isPrePopulated());
            jsonEnrolDetail.put(SerializedNames.SEC_ENROLMENT_FATHER_PARTICULARS, serializeEnrolmentAdult(enrolmentForm.getFather(), LoginUserType.PARENT));
            jsonEnrolDetail.put(SerializedNames.SEC_ENROLMENT_MOTHER_PARTICULARS, serializeEnrolmentAdult(enrolmentForm.getFather(), LoginUserType.PARENT));
            jsonEnrolDetail.put(SerializedNames.SEC_ENROLMENT_ENROL_CHILD, serializeEnrolChild(enrolmentForm.getChildRegistration()));
            jsonEnrolDetail.put(SerializedNames.SEC_ENROLMENT_NAH_PARTICULARS, serializeEnrolmentAdult(enrolmentForm.getNaHolder(), LoginUserType.NOMINATED_ACCOUNT_HOLDER));
            jsonEnrolDetail.put(SerializedNames.SEC_ENROLMENT_CDAT_PARTICULARS, serializeEnrolmentAdult(enrolmentForm.getNaHolder(), LoginUserType.CDA_TRUSTEE));
            jsonEnrolDetail.put(SerializedNames.SEC_ENROLMENT_MOTHER_DECLARE, serializeChildDeclarations(enrolmentForm.getChildDeclarations()));

            JSONObject jsonCgAuthorizer = new JSONObject();
            BankAccount cgBankAccount = enrolmentForm.getCashGiftBankAccount();
            jsonCgAuthorizer.put(SerializedNames.SN_BANK_ID, cgBankAccount.getBank().getId());
            jsonCgAuthorizer.put(SerializedNames.SN_BRANCH_ID, cgBankAccount.getBankBranch());
            jsonCgAuthorizer.put(SerializedNames.SN_BANK_ACC_NO, cgBankAccount.getBankAccountNo());
            jsonEnrolDetail.put(SerializedNames.SEC_ENROLMENT_CASH_GIFT_ACCOUNT, jsonCgAuthorizer);

            JSONObject jsonCda = new JSONObject();
            CdaBankAccount cdaBankAccount = enrolmentForm.getCdaBankAccount();
            jsonCda.put(SerializedNames.SN_CDAB_NETS_CARD_NAME, cdaBankAccount.getNetsCardName());

            JSONObject jsonCdaBank = new JSONObject();
            jsonCdaBank.put(SerializedNames.SN_BANK_ID, cdaBankAccount.getBank().getId());
            jsonCdaBank.put(SerializedNames.SN_BANK_NAME, cdaBankAccount.getBank().getName());
            jsonCda.put(SerializedNames.SEC_ENROLMENT_CHILD_DEV_ACCOUNT_BANK, jsonCdaBank);
            jsonEnrolDetail.put(SerializedNames.SEC_ENROLMENT_CHILD_DEV_ACCOUNT, jsonCda);

            JSONObject jsonDeclaration = new JSONObject();
            jsonDeclaration.put(SerializedNames.SN_ENROLMENT_IS_IS_DECLARE1, enrolmentForm.isDeclare1());
            jsonDeclaration.put(SerializedNames.SN_ENROLMENT_IS_IS_DECLARE2, enrolmentForm.isDeclare2());
            jsonDeclaration.put(SerializedNames.SN_ENROLMENT_IS_IS_DECLARE3, enrolmentForm.isDeclare3());
            jsonDeclaration.put(SerializedNames.SN_ENROLMENT_IS_IS_DECLARE4, enrolmentForm.isDeclare4());
            jsonEnrolDetail.put(SerializedNames.SEC_ENROLMENT_DECLARE, jsonDeclaration);

            JSONObject jsonFinal = new JSONObject();
            jsonFinal.put(SerializedNames.SEC_ENROLMENT_ROOT, jsonEnrolDetail);

            HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
            String jsonString = httpJsonCaller.post(AppUrls.UPDATE_ENROLMENT_APPLICATION, jsonFinal.toString());
            JSONObject jsonResponse = new JSONObject(jsonString);
            JSONObject jsonApplication = jsonResponse.getJSONObject(SerializedNames.SEC_RESPONSE_DATA)
                    .getJSONObject(SerializedNames.SEC_RESPONSE_APPLICATION);

            updateResponse.setResponseType(ServerResponseType.SUCCESS);
            updateResponse.setCode(jsonResponse.getString(SerializedNames.SEC_RESPONSE_CODE));
            updateResponse.setAppId(jsonApplication.getString(SerializedNames.SEC_RESPONSE_SERVICE_APP_ID));
            updateResponse.setAppId(jsonApplication.getString(SerializedNames.SEC_RESPONSE_STATUS));
            updateResponse.setMessage(jsonApplication.getString(SerializedNames.SEC_RESPONSE_MESSAGE));
        } catch (JSONException e) {
            updateResponse.setResponseType(ServerResponseType.APPLICATION_ERROR);
            updateResponse.setMessage(e.getMessage());
        } catch (Exception e) {
            updateResponse.setResponseType(ServerResponseType.SERVICE_ERROR);
            updateResponse.setMessage(e.getMessage());
        }
        return null;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public EnrolmentFormStatus getEnrolmentFormStatus(){
        String user = LoginManager.getSessionContainer().getNric();
        String url = String.format(
                sg.gov.msf.bbss.logic.server.proxy.dep.AppUrls.ENROLMENT_GET_STATUS, user);

        HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
        ArrayList<EnrolmentStatus> enrolmentStatuses = new ArrayList<EnrolmentStatus>();
        EnrolmentFormStatus enrolmentFormStatus = new EnrolmentFormStatus();

        try {
            String jsonString = httpJsonCaller.get(url);
            JSONArray jsonAppArray = new JSONArray(jsonString);

            for(int appIndex = 0; appIndex < jsonAppArray.length(); appIndex ++){

                ArrayList<EnrolmentChildStatus> enrolmentChildStatuses = new ArrayList<EnrolmentChildStatus>();

                JSONObject jsonApItem = jsonAppArray.getJSONObject(appIndex);
                JSONArray jsonChildrenArray = jsonApItem.getJSONArray(SerializedNames.SEC_ENROLMENT_APP_STATUS_CHILD);

                for(int childIndex = 0; childIndex < jsonChildrenArray.length(); childIndex ++) {
                    JSONObject jsonChild = jsonChildrenArray.getJSONObject(childIndex);

                    EnrolmentChildStatus enrolmentChildStatus = new EnrolmentChildStatus();
                    enrolmentChildStatus.setStatus(jsonChild.getString(SerializedNames.SN_ENROLMENT_STATUS_CHILD_STATUS));
                    enrolmentChildStatus.setName(jsonChild.getString(SerializedNames.SN_ENROLMENT_STATUS_CHILD_NAME));

                    enrolmentChildStatuses.add(enrolmentChildStatus);
                }

                EnrolmentStatus enrolmentStatus = new EnrolmentStatus();
                enrolmentStatus.setAppId(jsonApItem.getString(SerializedNames.SN_ENROLMENT_STATUS_APP_ID));
                enrolmentStatus.setEnrolmentChildStatuses(enrolmentChildStatuses.toArray(
                        new EnrolmentChildStatus[0]));

                enrolmentStatuses.add(enrolmentStatus);
            }

            enrolmentFormStatus.setEnrollStatus(enrolmentStatuses.toArray(new EnrolmentStatus[0]));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return enrolmentFormStatus;
    }

    //----------------------------------------------------------------------------------------------

    private JSONObject serializeEnrolmentAdult(Adult adult, LoginUserType userType) throws JSONException {
        JSONObject jsonAdult = Adult.serialize(adult, false);

        if(userType == LoginUserType.PARENT) {
            jsonAdult.put(SerializedNames.SN_ADULT_OCCUPATION, adult.getOccupation().getId());
            jsonAdult.put(SerializedNames.SN_ADULT_INCOME, adult.getMonthlyIncome());
        }

        if (userType == LoginUserType.CDA_TRUSTEE ||
                userType == LoginUserType.NOMINATED_ACCOUNT_HOLDER) {
            jsonAdult.put(SerializedNames.SN_ADULT_RELATIONSHIP, adult.getRelationshipType().getCode());
            jsonAdult.put(SerializedNames.SN_ADULT_ADDR_TYPE, adult.getAddressType().getCode());
            jsonAdult.put(SerializedNames.SEC_ADDRESS, Address.serialize(adult.getPostalAddress()));
        }

        return jsonAdult;
    }

    private Adult deserializeEnrolmentAdult(JSONObject jsonAdult, LoginUserType userType) throws JSONException {
        Adult adult = Adult.deserialize(jsonAdult, false);

        if(userType == LoginUserType.PARENT) {
            adult.setOccupation(new GenericDataItem(jsonAdult.getString(SerializedNames.SN_ADULT_OCCUPATION)));
            adult.setMonthlyIncome(jsonAdult.getDouble(SerializedNames.SN_ADULT_INCOME));
        }

        if (userType == LoginUserType.CDA_TRUSTEE ||
                userType == LoginUserType.NOMINATED_ACCOUNT_HOLDER) {
            adult.setRelationshipType(RelationshipType.parseType(jsonAdult.getString(SerializedNames.SN_ADULT_RELATIONSHIP)));
            adult.setAddressType(AddressType.parseType(jsonAdult.getString(SerializedNames.SN_ADULT_ADDR_TYPE)));
            adult.setPostalAddress(Address.deserialize(jsonAdult.getJSONObject(SerializedNames.SEC_ADDRESS)));
        }

        return adult;
    }

    private JSONObject serializeEnrolChild(ChildRegistration childRegistration) throws JSONException {
        JSONObject jsonEnrolChild = new JSONObject();

        jsonEnrolChild.put(SerializedNames.SN_CHILD_REG_TYPE, childRegistration.getRegistrationType().getCode());
        jsonEnrolChild.put(SerializedNames.SN_CHILD_REG_EST_DELIVERY_DATE, StringHelper.formatDate(childRegistration.getEstimatedDelivery()));
        jsonEnrolChild.put(SerializedNames.SEC_SUPPORTING_FILES, SupportingFile.serialize(childRegistration.getSupportingFiles()));
        jsonEnrolChild.put(SerializedNames.SN_CHILD_REG_IS_MARRIED, childRegistration.isMarried());
        jsonEnrolChild.put(SerializedNames.SN_CHILD_REG_IS_MARRIAGE_REG_IN_SINGAPORE, childRegistration.isRegisteredInSingapore());
        jsonEnrolChild.put(SerializedNames.SEC_ENROLMENT_CHILD, serializeChildren(childRegistration.getChildren()));

        return jsonEnrolChild;
    }

    private ChildRegistration deserializeEnrolChild(JSONObject jsonEnrolChild) throws JSONException {
        ChildRegistration childRegistration = new ChildRegistration();

        childRegistration.setRegistrationType(ChildRegistrationType.parseType(jsonEnrolChild.getString(SerializedNames.SN_CHILD_REG_TYPE)));
        childRegistration.setEstimatedDelivery(StringHelper.parseDate(jsonEnrolChild.getString(SerializedNames.SN_CHILD_REG_EST_DELIVERY_DATE)));
        childRegistration.setSupportingFiles(SupportingFile.deserialize(jsonEnrolChild.getJSONArray(SerializedNames.SEC_SUPPORTING_FILES)));
        childRegistration.setMarried(YesNoType.parseType(jsonEnrolChild.getString(SerializedNames.SN_CHILD_REG_IS_MARRIED)));
        childRegistration.setRegisteredInSingapore(YesNoType.parseType(jsonEnrolChild.getString(SerializedNames.SN_CHILD_REG_IS_MARRIAGE_REG_IN_SINGAPORE)));
        childRegistration.setChildren(deserializeChildren(jsonEnrolChild.getJSONArray(SerializedNames.SEC_ENROLMENT_CHILD)));

        return childRegistration;
    }

    private JSONArray serializeChildren(ArrayList<Child> children) throws JSONException {
        JSONArray jsonChildren = new JSONArray();
        JSONObject jsonChild = null;

        for (Child child : children) {
            jsonChild = new JSONObject();

            jsonChild.put(SerializedNames.SN_PERSON_ID, child.getId());
            jsonChild.put(SerializedNames.SN_PERSON_NRIC, child.getNric());
            jsonChild.put(SerializedNames.SN_PERSON_NAME, child.getName());
            jsonChild.put(SerializedNames.SN_CHILD_BIRTH_CERT_NO, child.getBirthCertNo());
            jsonChild.put(SerializedNames.SN_PERSON_BIRTHDAY, StringHelper.formatDate(child.getDateOfBirth()));
            jsonChild.put(SerializedNames.SN_CHILD_IS_BORN_OVERSEAS, child.isBornOnOverseas().getCode());
            jsonChild.put(SerializedNames.SEC_SUPPORTING_FILES, SupportingFile.serialize(child.getSupportingFiles()));

            jsonChildren.put(child);
        }

        return jsonChildren;
    }

    private ArrayList<Child> deserializeChildren(JSONArray jsonChildren) throws JSONException {
        ArrayList<Child> children = new ArrayList<Child>();
        JSONObject jsonChild = null;
        Child child = null;
        int arrayLength = jsonChildren.length();

        for (int index = 0; index < arrayLength; index++) {
            jsonChild = jsonChildren.getJSONObject(index);
            child = new Child();

            child.setId(jsonChild.getString(SerializedNames.SN_PERSON_ID));
            child.setNric(jsonChild.getString(SerializedNames.SN_PERSON_NRIC));
            child.setName(jsonChild.getString(SerializedNames.SN_PERSON_NAME));
            child.setBirthCertNo(jsonChild.getString(SerializedNames.SN_CHILD_BIRTH_CERT_NO));
            child.setDateOfBirth(StringHelper.parseDate(jsonChild.getString(SerializedNames.SN_PERSON_BIRTHDAY)));
            child.setBornOnOverseas(YesNoType.parseType(jsonChild.getString(SerializedNames.SN_CHILD_IS_BORN_OVERSEAS)));
            child.setSupportingFiles(SupportingFile.deserialize(jsonChild.getJSONArray(SerializedNames.SEC_SUPPORTING_FILES)));

            children.add(child);
        }

        return children;
    }

    private JSONArray serializeChildDeclarations(ArrayList<ChildDeclaration> childDeclarations) throws JSONException {
        JSONArray jsonChildDeclarations = new JSONArray();
        JSONObject jsonChildDeclaration = null;
        JSONObject jsonChild = null;
        Child child = null;

        for (ChildDeclaration childDeclaration : childDeclarations) {
            jsonChildDeclaration = new JSONObject();
            jsonChild = new JSONObject();
            child = childDeclaration.getChild();

            jsonChildDeclaration.put(SerializedNames.SN_CHILD_DEC_TYPE, childDeclaration.getDeclarationType().getCode());
            jsonChildDeclaration.put(SerializedNames.SN_CHILD_DEC_ADOPTION_GIVEN_DATE, StringHelper.formatDate(childDeclaration.getDateOfAdoption()));
            jsonChildDeclaration.put(SerializedNames.SN_CHILD_DEC_ADOPTION_ORDER_DATE, StringHelper.formatDate(childDeclaration.getDateOfAdoptionOrder()));
            jsonChildDeclaration.put(SerializedNames.SN_CHILD_DEC_DE_DATE, StringHelper.formatDate(childDeclaration.getDeceasedDate()));
            jsonChildDeclaration.put(SerializedNames.SN_CHILD_DEC_COUNTRY, childDeclaration.getCountryOfBirth().getId());
            jsonChildDeclaration.put(SerializedNames.SN_CHILD_DEC_SINGA_CITIZEN_NO, childDeclaration.getCitizenshipNo());
            jsonChildDeclaration.put(SerializedNames.SN_CHILD_DEC_REMARK, childDeclaration.getRemarks());
            jsonChildDeclaration.put(SerializedNames.SEC_SUPPORTING_FILES, SupportingFile.serialize(childDeclaration.getSupportingFiles()));

            jsonChild.put(SerializedNames.SN_PERSON_SEQ, child.getId());
            jsonChild.put(SerializedNames.SN_PERSON_NAME, child.getName());
            jsonChild.put(SerializedNames.SN_PERSON_BIRTHDAY, StringHelper.formatDate(child.getDateOfBirth()));
            jsonChild.put(SerializedNames.SN_CHILD_BIRTH_CERT_NO, child.getBirthCertNo());
            jsonChildDeclaration.put(SerializedNames.SEC_CHILD_LIST_ROOT, jsonChild);

            jsonChildDeclarations.put(jsonChildDeclaration);
        }

        return jsonChildDeclarations;
    }

    private ArrayList<ChildDeclaration> deserializeChildDeclarations(JSONArray jsonMotherDeclares) throws JSONException {
        int arrayLength = jsonMotherDeclares.length();
        JSONObject jsonMotherDeclare = null;
        ArrayList<ChildDeclaration> childDeclarations = new ArrayList<ChildDeclaration>();
        ChildDeclaration childDeclaration = null;
        JSONObject jsonChild = null;
        Child child = null;

        for (int index = 0; index < arrayLength; index ++) {
            jsonMotherDeclare = jsonMotherDeclares.getJSONObject(index);
            jsonChild = jsonMotherDeclare.getJSONObject(SerializedNames.SEC_CHILD_LIST_ROOT);
            child = new Child();

            childDeclaration = new ChildDeclaration(ChildDeclarationType.parseType(jsonMotherDeclare.getString(SerializedNames.SN_CHILD_DEC_TYPE)));
            childDeclaration.setDateOfAdoption(StringHelper.parseDate(jsonMotherDeclare.getString(SerializedNames.SN_CHILD_DEC_ADOPTION_GIVEN_DATE)));
            childDeclaration.setDateOfAdoptionOrder(StringHelper.parseDate(jsonMotherDeclare.getString(SerializedNames.SN_CHILD_DEC_ADOPTION_ORDER_DATE)));
            childDeclaration.setDeceasedDate(StringHelper.parseDate(jsonMotherDeclare.getString(SerializedNames.SN_CHILD_DEC_DE_DATE)));
            childDeclaration.setCountryOfBirth(new GenericDataItem(jsonMotherDeclare.getString(SerializedNames.SN_CHILD_DEC_COUNTRY)));
            childDeclaration.setCitizenshipNo(jsonMotherDeclare.getString(SerializedNames.SN_CHILD_DEC_SINGA_CITIZEN_NO));
            childDeclaration.setRemarks(jsonMotherDeclare.getString(SerializedNames.SN_CHILD_DEC_REMARK));
            childDeclaration.setSupportingFiles(SupportingFile.deserialize(jsonMotherDeclare.getJSONArray(SerializedNames.SEC_SUPPORTING_FILES)));

            child.setId(jsonChild.getString(SerializedNames.SN_PERSON_SEQ));
            child.setName(jsonChild.getString(SerializedNames.SN_PERSON_NAME));
            child.setDateOfBirth(StringHelper.parseDate(jsonChild.getString(SerializedNames.SN_PERSON_BIRTHDAY)));
            child.setBirthCertNo(jsonChild.getString(SerializedNames.SN_CHILD_BIRTH_CERT_NO));
            childDeclaration.setChild(child);

            childDeclarations.add(childDeclaration);
        }

        return childDeclarations;
    }

}
