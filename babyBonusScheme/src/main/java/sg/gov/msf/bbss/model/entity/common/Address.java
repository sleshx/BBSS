package sg.gov.msf.bbss.model.entity.common;

import org.json.JSONException;
import org.json.JSONObject;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.annotation.DisplayNameId;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.logic.type.EnvironmentType;

/**
 * Created by bandaray
 */
public class Address {

    public static final String FIELD_ADDRESS_ID = "addressId";
    public static final String FIELD_POSTAL_CODE = "postalCode";
    public static final String FIELD_FLOOR_NO = "floorNo";
    public static final String FIELD_UNIT_NO = "unitNo";
    public static final String FIELD_BLOCK_HOUSE_NO = "blockHouseNo";
    public static final String FIELD_STREET_NAME = "streetName";
    public static final String FIELD_BUILDING_NAME = "buildingName";
    public static final String FIELD_ADDRESS1 = "address1";
    public static final String FIELD_ADDRESS2 = "address2";

    /*
     * WARNING : If you refactor any of the property names below,
     *           you have to rename the above constant values as well.
     */

    public static final String[] SERIAL_NAMES = new String [] {
        SerializedNames.SN_ADDRESS_POST_CODE,
        SerializedNames.SN_ADDRESS_UNIT_NO,
        SerializedNames.SN_ADDRESS_BLOCK_HOUSE_NO,
        SerializedNames.SN_ADDRESS_STREET,
        SerializedNames.SN_ADDRESS_BUILDING,
        SerializedNames.SN_ADDRESS_ADDRESS1,
        SerializedNames.SN_ADDRESS_ADDRESS2
    };
    @DisplayNameId(R.string.label_common_id)
    private String addressId;

    @DisplayNameId(R.string.label_address_postal_code)
    private int postalCode;

    @DisplayNameId(R.string.label_address_floor_no)
    private String floorNo;

    @DisplayNameId(R.string.label_address_unit_no)
    private String unitNo;

    @DisplayNameId(R.string.label_address_block_house)
    private String blockHouseNo;

    @DisplayNameId(R.string.label_address_street_name)
    private String streetName;

    @DisplayNameId(R.string.label_address_building_name)
    private String buildingName;

    @DisplayNameId(R.string.label_address_address1)
    private String address1;

    @DisplayNameId(R.string.label_address_address2)
    private String address2;

    //----------------------------------------------------------------------------------------------

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public String getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(String floorNo) {
        this.floorNo = floorNo;
    }

    public String getUnitNo() {
        return unitNo;
    }

    public void setUnitNo(String unitNo) {
        this.unitNo = unitNo;
    }

    public String getBlockHouseNo() {
        return blockHouseNo;
    }

    public void setBlockHouseNo(String blockHouseNo) {
        this.blockHouseNo = blockHouseNo;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public static JSONObject serialize(Address address) throws JSONException {
        JSONObject jsonAddress = new JSONObject();

        if(address != null) {
            jsonAddress.put(SerializedNames.SN_ADDRESS_POST_CODE, address.getPostalCode());
            jsonAddress.put(SerializedNames.SN_ADDRESS_UNIT_NO, String.format("#%s-%s", address.getFloorNo(), address.getUnitNo()));
            jsonAddress.put(SerializedNames.SN_ADDRESS_BLOCK_HOUSE_NO, address.getBlockHouseNo());
            jsonAddress.put(SerializedNames.SN_ADDRESS_STREET, address.getStreetName());
            jsonAddress.put(SerializedNames.SN_ADDRESS_BUILDING, address.getBuildingName());
            jsonAddress.put(SerializedNames.SN_ADDRESS_ADDRESS1, address.getAddress1());
            jsonAddress.put(SerializedNames.SN_ADDRESS_ADDRESS2, address.getAddress2());
        }
        return jsonAddress;
    }

    public static Address deserialize(JSONObject jsonAddress) throws JSONException {
        Address address = new Address();

        if(ProxyFactory.ENVIRONMENT == EnvironmentType.DEV) {
            address.setPostalCode(Integer.parseInt(jsonAddress.getString(SerializedNames.SN_ADDRESS_POST_CODE)));
            address.setUnitNo(jsonAddress.getString(SerializedNames.SN_ADDRESS_UNIT_NO));
            address.setBlockHouseNo(jsonAddress.getString(SerializedNames.SN_ADDRESS_BLOCK_HOUSE_NO));
            address.setStreetName(jsonAddress.getString(SerializedNames.SN_ADDRESS_STREET));
            address.setBuildingName(jsonAddress.getString(SerializedNames.SN_ADDRESS_BUILDING));
            address.setAddress1(jsonAddress.getString(SerializedNames.SN_ADDRESS_ADDRESS1));
            address.setAddress2(jsonAddress.getString(SerializedNames.SN_ADDRESS_ADDRESS2));
        } else if(ProxyFactory.ENVIRONMENT == EnvironmentType.DEP) {
            address.setPostalCode(Integer.parseInt(jsonAddress.optString(SerializedNames.SN_ADDRESS_POST_CODE)));
            address.setUnitNo(jsonAddress.optString(SerializedNames.SN_ADDRESS_UNIT_NO));
            address.setBlockHouseNo(jsonAddress.optString(SerializedNames.SN_ADDRESS_BLOCK_HOUSE_NO));
            address.setStreetName(jsonAddress.optString(SerializedNames.SN_ADDRESS_STREET));
            address.setBuildingName(jsonAddress.optString(SerializedNames.SN_ADDRESS_BUILDING));
            address.setAddress1(jsonAddress.optString(SerializedNames.SN_ADDRESS_ADDRESS1));
            address.setAddress2(jsonAddress.optString(SerializedNames.SN_ADDRESS_ADDRESS2));
        }

        return address;
    }
}
