package sg.gov.msf.bbss.model.entity.people;

import org.json.JSONException;
import org.json.JSONObject;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.annotation.DisplayNameId;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.logic.type.AddressType;
import sg.gov.msf.bbss.logic.type.CommunicationType;
import sg.gov.msf.bbss.logic.type.EnvironmentType;
import sg.gov.msf.bbss.logic.type.IdentificationType;
import sg.gov.msf.bbss.logic.type.RelationshipType;
import sg.gov.msf.bbss.model.entity.common.Address;
import sg.gov.msf.bbss.model.entity.masterdata.GenericDataItem;

/**
 * Created by bandaray
 */
public class Adult extends Person {

    public static final String FIELD_IDENTIFICATION_TYPE = "identificationType";
    public static final String FIELD_IDENTIFICATION_NO = "identificationNo";
    public static final String FIELD_NATIONALITY = "nationality";
    public static final String FIELD_MODE_OF_COM = "modeOfCommunication";
    public static final String FIELD_MOBILE_NO = "mobileNumber";
    public static final String FIELD_EMAIL_ADDR = "emailAddress";
    public static final String FIELD_ADDR_TYPE = "addressType";
    public static final String FIELD_POSTAL_ADDR = "postalAddress";
    public static final String FIELD_OCCUPATION = "occupation";
    public static final String FIELD_MONTHLY_INCOME = "monthlyIncome";
    public static final String FIELD_RELATIONSHIP = "relationshipType";

    /*
     * WARNING : If you refactor any of the property names below,
     *           you have to rename the above constant values as well.
     */

    public static final String[] SERIAL_NAMES = new String [] {
            SerializedNames.SN_ADULT_RELATIONSHIP,
            SerializedNames.SN_PERSON_NRIC,
            SerializedNames.SN_PERSON_ID,
            SerializedNames.SN_ADULT_ID_TYPE,
            SerializedNames.SN_ADULT_ID_NO,
            SerializedNames.SN_PERSON_NAME,
            SerializedNames.SN_ADULT_NATIONALITY,
            SerializedNames.SN_PERSON_BIRTHDAY,
            SerializedNames.SN_ADULT_MODE_OF_COMM,
            SerializedNames.SN_ADULT_MOBILE,
            SerializedNames.SN_ADULT_EMAIL,
            SerializedNames.SN_ADULT_ADDR_TYPE,
            SerializedNames.SN_ADULT_OCCUPATION,
            SerializedNames.SN_ADULT_INCOME
    };

    @DisplayNameId(R.string.label_adult_identification_type)
    private IdentificationType identificationType;

    @DisplayNameId(R.string.label_adult_identification_no)
    private String identificationNo;

    @DisplayNameId(R.string.label_adult_nationality)
    private GenericDataItem nationality;

    @DisplayNameId(R.string.label_adult_communication_mode)
    private CommunicationType modeOfCommunication;

    @DisplayNameId(R.string.label_adult_mobile_no)
    private String mobileNumber;

    @DisplayNameId(R.string.label_adult_email_addr)
    private String emailAddress;

    @DisplayNameId(R.string.label_adult_addr_type)
    private AddressType addressType;

    @DisplayNameId(R.string.label_adult_addr_type)
    private Address postalAddress;

    @DisplayNameId(R.string.label_adult_occupation)
    private GenericDataItem occupation;

    @DisplayNameId(R.string.label_adult_monthly_income)
    private double monthlyIncome;

    @DisplayNameId(R.string.label_adult_relationship)
    private RelationshipType relationshipType;

    //----------------------------------------------------------------------------------------------

    public IdentificationType getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(IdentificationType identificationType) {
        this.identificationType = identificationType;
    }

    public String getIdentificationNo() {
        return identificationNo;
    }

    public void setIdentificationNo(String identificationNo) {
        this.identificationNo = identificationNo;
    }

    public GenericDataItem getNationality() {
        return nationality;
    }

    public void setNationality(GenericDataItem nationality) {
        this.nationality = nationality;
    }

    public CommunicationType getModeOfCommunication() {
        return modeOfCommunication;
    }

    public void setModeOfCommunication(CommunicationType modeOfCommunication) {
        this.modeOfCommunication = modeOfCommunication;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    public Address getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(Address postalAddress) {
        this.postalAddress = postalAddress;
    }

    public GenericDataItem getOccupation() {
        return occupation;
    }

    public void setOccupation(GenericDataItem occupation) {
        this.occupation = occupation;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    public static JSONObject serialize(Adult adult, boolean isAddressRequired) throws JSONException {
        JSONObject jsonAdult = new JSONObject();

        if(adult != null) {
            jsonAdult.put(SerializedNames.SN_PERSON_ID, adult.getId());
            jsonAdult.put(SerializedNames.SN_PERSON_NRIC, adult.getNric());
            jsonAdult.put(SerializedNames.SN_ADULT_ID_TYPE, adult.getIdentificationType() == null ? null : adult.getIdentificationType().getCode());
            jsonAdult.put(SerializedNames.SN_ADULT_ID_NO, adult.getIdentificationNo());
            jsonAdult.put(SerializedNames.SN_PERSON_NAME, adult.getName());
            jsonAdult.put(SerializedNames.SN_ADULT_NATIONALITY, adult.getNationality() == null ? null : adult.getNationality().getId());
            jsonAdult.put(SerializedNames.SN_PERSON_BIRTHDAY, StringHelper.formatDate(adult.getDateOfBirth()));
            jsonAdult.put(SerializedNames.SN_ADULT_MODE_OF_COMM, adult.getModeOfCommunication() == null ? null : adult.getModeOfCommunication().getCode());
            jsonAdult.put(SerializedNames.SN_ADULT_MOBILE, adult.getMobileNumber());
            jsonAdult.put(SerializedNames.SN_ADULT_EMAIL, adult.getEmailAddress());

            if (isAddressRequired) {
                jsonAdult.put(SerializedNames.SN_ADULT_ADDR_TYPE, adult.getAddressType() == null ? null : adult.getAddressType().getCode());
                jsonAdult.put(SerializedNames.SEC_ADDRESS, Address.serialize(adult.getPostalAddress()));
            }
        }

        return jsonAdult;
    }

    public static Adult deserialize(JSONObject jsonAdult, boolean isAddressRequired) throws JSONException {
        Adult adult = new Adult();

        if(ProxyFactory.ENVIRONMENT == EnvironmentType.DEV) {
            adult.setId(jsonAdult.getString(SerializedNames.SN_PERSON_ID));
            adult.setNric(jsonAdult.getString(SerializedNames.SN_PERSON_NRIC));
            adult.setIdentificationType(IdentificationType.parseType(jsonAdult.getString(SerializedNames.SN_ADULT_ID_TYPE)));
            adult.setIdentificationNo(jsonAdult.getString(SerializedNames.SN_ADULT_ID_NO));
            adult.setName(jsonAdult.getString(SerializedNames.SN_PERSON_NAME));
            adult.setNationality(new GenericDataItem(jsonAdult.getString(SerializedNames.SN_ADULT_NATIONALITY)));
            adult.setDateOfBirth(StringHelper.parseDate(jsonAdult.getString(SerializedNames.SN_PERSON_BIRTHDAY)));
            adult.setModeOfCommunication(CommunicationType.parseType(jsonAdult.getString(SerializedNames.SN_ADULT_MODE_OF_COMM)));
            adult.setMobileNumber(jsonAdult.getString(SerializedNames.SN_ADULT_MOBILE));
            adult.setEmailAddress(jsonAdult.getString(SerializedNames.SN_ADULT_EMAIL));

            if (isAddressRequired) {
                adult.setAddressType(AddressType.parseType(jsonAdult.getString(SerializedNames.SN_ADULT_ADDR_TYPE)));
                adult.setPostalAddress(Address.deserialize(jsonAdult.getJSONObject(SerializedNames.SEC_ADDRESS)));
            }
        } else if(ProxyFactory.ENVIRONMENT == EnvironmentType.DEP) {
            adult.setId(jsonAdult.optString(SerializedNames.SN_PERSON_ID));
            adult.setNric(jsonAdult.optString(SerializedNames.SN_PERSON_NRIC));
            adult.setIdentificationType(IdentificationType.parseType(jsonAdult.optString(SerializedNames.SN_ADULT_ID_TYPE)));
            adult.setIdentificationNo(jsonAdult.optString(SerializedNames.SN_ADULT_ID_NO));
            adult.setName(jsonAdult.optString(SerializedNames.SN_PERSON_NAME));
            adult.setNationality(new GenericDataItem(jsonAdult.optString(SerializedNames.SN_ADULT_NATIONALITY)));
            adult.setDateOfBirth(StringHelper.parseDate(jsonAdult.optString(SerializedNames.SN_PERSON_BIRTHDAY)));
            adult.setModeOfCommunication(CommunicationType.parseType(jsonAdult.optString(SerializedNames.SN_ADULT_MODE_OF_COMM)));
            adult.setMobileNumber(jsonAdult.optString(SerializedNames.SN_ADULT_MOBILE));
            adult.setEmailAddress(jsonAdult.optString(SerializedNames.SN_ADULT_EMAIL));

            if (isAddressRequired) {
                adult.setAddressType(AddressType.parseType(jsonAdult.optString(SerializedNames.SN_ADULT_ADDR_TYPE)));
                adult.setPostalAddress(Address.deserialize(jsonAdult.getJSONObject(SerializedNames.SEC_ADDRESS)));
            }

            return adult;
        }

        return adult;
    }
}
